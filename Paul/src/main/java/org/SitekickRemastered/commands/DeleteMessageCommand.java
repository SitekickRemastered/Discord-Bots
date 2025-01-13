package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class DeleteMessageCommand implements CommandInterface {

    @Override
    public String getName() {
        return "delete_message";
    }


    @Override
    public String getDescription() {
        return "Deletes a message using the message's ID.";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "message_id", "The ID of the message to delete.", true));
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        String[] fullId = Objects.requireNonNull(e.getOption("message_id")).getAsString().split("-");
        e.getChannel().deleteMessageById(fullId[1]).queue();
        e.reply("Message was deleted successfully!").setEphemeral(true).queue();
    }
}