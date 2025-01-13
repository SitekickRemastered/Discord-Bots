package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class EndPollCommand implements CommandInterface {

    @Override
    public String getName() {
        return "end_poll";
    }


    @Override
    public String getDescription() {
        return "Ends a poll using the poll's message ID.";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "message_id", "The ID of the poll to end.", true));
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.VIEW_AUDIT_LOGS);
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        String[] fullId = Objects.requireNonNull(e.getOption("message_id")).getAsString().split("-");
        Objects.requireNonNull(e.getJDA().getTextChannelById(fullId[0])).endPollById(fullId[1]).queue();
        e.reply("Poll was ended successfully!").setEphemeral(true).queue();
    }
}
