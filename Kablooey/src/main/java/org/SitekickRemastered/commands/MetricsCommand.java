package org.SitekickRemastered.commands;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.SitekickRemastered.utils.Helpers.postRequest;
import static org.SitekickRemastered.utils.Helpers.save;

public class MetricsCommand implements CommandInterface {

    String metricsFilePath, metricsMessageChannel, metricsMessageId;
    static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public MetricsCommand(String mfp, String mmc, String mmi) {
        this.metricsFilePath = mfp;
        this.metricsMessageChannel = mmc;
        this.metricsMessageId = mmi;
    }


    @Override
    public String getName() {
        return "metrics";
    }


    @Override
    public String getDescription() {
        return "Creates a message that displays the current game metrics. Should only be made once.";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.CHANNEL, "channel", "The channel the message will appear in.", true).setChannelTypes(ChannelType.TEXT, ChannelType.NEWS, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_NEWS_THREAD, ChannelType.FORUM));
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        try {
            metricsCommand(e, Objects.requireNonNull(e.getOption("channel")).getAsChannel());
        }
        catch (IOException | ParseException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * Makes an embed message that contains game metrics in a specific channel.
     *
     * @param e       - The SlashCommandInteractionEvent listener. Activates this function whenever it hears a slash command.
     * @param channel - The channel that we will send the metrics message to (usually just the #metrics channel).
     */
    public void metricsCommand(SlashCommandInteractionEvent e, GuildChannelUnion channel) throws IOException, ParseException, InterruptedException {
        EmbedBuilder tempEmbed = new EmbedBuilder();
        createEmbed(tempEmbed);

        // If the message already exists, alert the user and return
        if (!metricsMessageId.isEmpty()) {
            e.reply("Hey, " + e.getUser().getAsMention() + "! This message already exists! Its ID is: " + metricsMessageId).setEphemeral(true).queue();
            return;
        }

        // If it doesn't exist, Send the message to the channel, and save the information into the CommandManager variables (and the messageId2 txt file).
        // Start the scheduler to update the message after.
        channel.asGuildMessageChannel().sendMessageEmbeds(tempEmbed.build()).queue(m -> {
            try {
                save(metricsFilePath, channel.getId() + "-" + m.getId());
                metricsMessageChannel = channel.getId();
                metricsMessageId = m.getId();

                scheduler.scheduleAtFixedRate(() -> {
                    if (!metricsMessageChannel.isEmpty()) {
                        try {
                            updateMetrics(m);
                        }
                        catch (IOException | ParseException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    else
                        scheduler.shutdownNow();
                }, 0, 60, TimeUnit.SECONDS);
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        tempEmbed.clear();
        e.deferReply().queue(m -> m.deleteOriginal().queue());
    }


    /**
     * Updates the metrics message via a message. Used when the metrics message is first created.
     *
     * @param m - The metrics message itself.
     */
    public static void updateMetrics(Message m) throws IOException, ParseException {
        EmbedBuilder tempEmbed = new EmbedBuilder();
        try {
            createEmbed(tempEmbed);
        }
        catch (IOException | ParseException ex) {
            throw new RuntimeException(ex);
        }
        m.editMessageEmbeds(tempEmbed.build()).queue();
        tempEmbed.clear();
    }


    /**
     * Creates the metrics embed.
     *
     * @param eb - EmbedBuilder eb - The embed message that we edit for the message.
     */
    public static void createEmbed(EmbedBuilder eb) throws IOException, ParseException {
        eb.setTitle("__**Sitekick Remastered Game Metrics**__");
        eb.setColor(0x007AFE); // Kablooey's blue
        eb.setTimestamp(new Date().toInstant()); //Set up the date

        // Get the metrics information from the POSTRequest function
        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        String link = dotenv.get("METRICS_LINK");
        JSONObject json = postRequest(link, new ArrayList<>(), "Failed to retrieve metrics from POST");

        // On success, edit the metrics
        if (json != null) {
            eb.setDescription("**Online Players:** " + json.get("online_players") +
                            "\n**Players Today:** " + json.get("daily_online_players") +
                            "\n**Registrations Today:** " + json.get("daily_registrations") +
                            "\n**Total Players:** " + json.get("total_players") +
                            "\n**Total Active Chips:** " + json.get("total_chips") +
                            "\n\nBrought to you by me <:kablrury:1036832644631117954>"
            );
            System.out.println(Instant.now() + " Updated Metrics Successfully.");
        }
        // On fail, alert that metrics is broken.
        else {
            eb.setDescription("I'm unable to retrieve metrics at this time <:nootsad:747467956308410461>");
            System.out.println(Instant.now() + " failed to get the Metrics from the POST.");
        }
    }
}
