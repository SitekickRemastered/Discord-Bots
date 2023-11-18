import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandManager extends ListenerAdapter {

    Dotenv dotenv = Dotenv.configure().filename(".env").load();

    //These are for checking Authicer's status
    String statusURL = dotenv.get("AUTHICER_PING_LINK");
    ScheduledExecutorService threadPool = Executors.newSingleThreadScheduledExecutor();

    /** This function runs when the bot is ready. It gets the Status URL of the bot, and then
     *  gets the URL every 45 seconds. If this fails, another bot will alert us, so it can be fixed.
     *  @param e - The ReadyEvent listener. Activates when the bot is ready / starts up
     */
    public void onReady(ReadyEvent e){
        threadPool.scheduleWithFixedDelay(() -> {
            try {
                URLConnection conn = new URL(statusURL).openConnection();
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                InputStream response = conn.getInputStream();
                response.close();
            }
            catch (IOException ex) { ex.printStackTrace(); }
        }, 0, 45, TimeUnit.SECONDS);
    }

    /** Initializes Authicer. Sets up all the commands.
     *  @param e - The GuildReadyEvent listener. Activates when the bot is ready / starts up
     */
    public void onGuildReady(GuildReadyEvent e){
        List<CommandData> commandData = new ArrayList<>();
        OptionData code = new OptionData(OptionType.STRING, "code", "Your code in the \"Link Discord\" Tab in Sitekick Remastered", true);
        commandData.add(Commands.slash("link", "Links your Sitekick Remastered and Discord accounts").addOptions(code));
        e.getGuild().updateCommands().addCommands(commandData).queue();
    }

    /** The hub for all slash commands for Authicer.
     *  @param e - The SlashCommandInteractionEvent listener. Activates this function whenever it hears a slash command
     */
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e){

        // If the user types the command /link, we check if this command came from the #link channel in the discord
        // If it did, we get the code attached and go to the verifyCommand function
        // Otherwise we alert the user.
        if (e.getName().equals("link")) {
            if (e.getChannel().toString().contains("link")) {
                try {
                    Verify.verifyCommand(e, e.getOption("code").getAsString().replace(" ", ""));
                } catch (IOException | ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else {
                e.reply("Oak's words echoed... \"There's a time and place for everything but not now!\"\n\nPlease use the `/link` command in the <#" + e.getJDA().getTextChannelsByName("link", true).get(0).getId() + "> channel!").setEphemeral(true).queue();
            }
        }
    }

    /** The function that handles messages from users.
     *  @param e - The Message listener. Activates this function whenever it hears a message
     */
    public void onMessageReceived(MessageReceivedEvent e) {

        // If the user sent a private message to Authicer, we send them some messages telling them how to use Authicer
        if (e.getChannelType() == ChannelType.PRIVATE && !e.getAuthor().isBot()){
            e.getChannel().sendMessage("Message the https://discord.com/channels/603580736250970144/999864676982722560 channel to verify your account.").queue(message -> message.delete().queueAfter(60, TimeUnit.SECONDS));
            e.getChannel().sendMessage("This message will self destruct in one minute.").queue(message -> message.delete().queueAfter(60, TimeUnit.SECONDS));
        }

        //If the user posted in #link with something that is not /link, delete the message
        if (e.getChannel().toString().contains("link") && !e.getMember().getUser().isBot()) {
            e.getMessage().delete().queue();
        }
    }
}