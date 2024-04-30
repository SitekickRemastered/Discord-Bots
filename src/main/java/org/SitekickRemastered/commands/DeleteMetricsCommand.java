package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class DeleteMetricsCommand implements CommandInterface {

    String metricsMessageChannel;
    String metricsMessageId;


    public DeleteMetricsCommand(String metricsMessageChannel, String metricsMessageId) {
        this.metricsMessageChannel = metricsMessageChannel;
        this.metricsMessageId = metricsMessageId;
    }


    @Override
    public String getName() {
        return "delete_metrics";
    }


    @Override
    public String getDescription() {
        return "Deletes the metrics message and the messageId2.txt file so you can repost it.";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (metricsMessageChannel.isEmpty() || metricsMessageId.isEmpty()) {
            e.reply("Metrics message does not seem to exist!").setEphemeral(true).queue();
            return;
        }

        Objects.requireNonNull(Objects.requireNonNull(e.getGuild()).getTextChannelById(metricsMessageChannel)).retrieveMessageById(metricsMessageId).queue(m -> {
            m.delete().queue();
            metricsMessageChannel = "";
            metricsMessageId = "";
        });
        File messageId2 = new File("src/messageId2.txt");
        if (messageId2.delete())
            e.reply("Deleted metrics message and messageId2.txt successfully.").setEphemeral(true).queue();
        else
            e.reply("Failed to delete metrics file. Please delete it manually if the message was successfully deleted.").setEphemeral(true).queue();

    }
}
