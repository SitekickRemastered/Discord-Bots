package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class HelpCommand implements CommandInterface {

    @Override
    public String getName() {
        return "help";
    }


    @Override
    public String getDescription() {
        return "Opens the help menu.";
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

        // Only send if it doesn't exist
        if (Helpers.currentUser.getHelpMessage() != null) {
            e.reply("Help Message already exists! Link: " + Helpers.currentUser.getHelpMessage().getJumpUrl()).setEphemeral(true).queue();
            return;
        }

        Helpers.deleteMessages(e);
        try {
            Helpers.help(e);
        }
        catch (ExecutionException | InterruptedException executionException) {
            e.reply("ERROR: Failed to open the help menu: " + executionException.getMessage()).setEphemeral(true).queue();
        }
        e.deferReply().setEphemeral(true).queue(m -> m.deleteOriginal().queue());
    }
}