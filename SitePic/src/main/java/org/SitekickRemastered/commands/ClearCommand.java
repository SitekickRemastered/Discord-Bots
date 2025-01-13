package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;

import java.util.List;

public class ClearCommand implements CommandInterface {

    @Override
    public String getName() {
        return "clear";
    }


    @Override
    public String getDescription() {
        return "Deletes 100 Bot Messages (This might take a while).";
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
        for (Message m : e.getChannel().getHistory().retrievePast(100).complete()) {
            if (m.getAuthor().isBot())
                m.delete().queue();
        }
        Helpers.currentUser.clearHelpMessage();
        Helpers.currentUser.clearBotMessages();
        e.reply("Deleting 100 Bot Messages (Please be patient. Discord has rate limits, so this might take a while)!").setEphemeral(true).queue();
    }
}