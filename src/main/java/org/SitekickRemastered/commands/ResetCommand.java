package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;
import org.SitekickRemastered.utils.SendPFP;

import java.util.List;

public class ResetCommand implements CommandInterface {

    @Override
    public String getName() {
        return "reset";
    }


    @Override
    public String getDescription() {
        return "Resets your Profile Picture back to default.";
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
        Helpers.currentUser.resetDefault();
        e.reply("Profile Picture Reset!").setEphemeral(true).queue();
        try {
            SendPFP.editEmbed(e);
        }
        catch (Exception ignored) {
        }
    }
}