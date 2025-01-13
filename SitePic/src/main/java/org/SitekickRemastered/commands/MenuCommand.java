package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;

import java.util.List;

public class MenuCommand implements CommandInterface {

    @Override
    public String getName() {
        return "menu";
    }


    @Override
    public String getDescription() {
        return "Opens the main menu.";
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

        // Change the help message button colour if it exists.
        if (Helpers.currentUser.getHelpMessage() != null && !Helpers.currentUser.getHelpMessage().getActionRows().isEmpty())
            Helpers.currentUser.getHelpMessage().editMessageEmbeds(Helpers.currentUser.getHelpMessage().getEmbeds().getFirst()).setComponents().queue();

        Helpers.deleteMessages(e);
        try {
            Helpers.menu(e);
        }
        catch (Exception exception) {
            e.reply("ERROR: Failed to open the main menu: " + exception.getMessage()).setEphemeral(true).queue();
        }
        e.deferReply().setEphemeral(true).queue(m -> m.deleteOriginal().queue());
    }
}