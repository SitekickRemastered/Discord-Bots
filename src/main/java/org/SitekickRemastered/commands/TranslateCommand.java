package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;
import org.SitekickRemastered.utils.SendPFP;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TranslateCommand implements CommandInterface {

    @Override
    public String getName() {
        return "translate";
    }


    @Override
    public String getDescription() {
        return "Shift your profile picture from its centre (Default Position is 0, 0).";
    }


    @Override
    public List<OptionData> getOptions() {
        return Arrays.asList(
            new OptionData(OptionType.INTEGER, "x", "x direction movement amount", true),
            new OptionData(OptionType.INTEGER, "y", "y direction movement amount", true)
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (!Helpers.currentUser.getLocation().equals("body") && !Helpers.currentUser.getLocation().equals("eyes")) {
            e.reply("Please use /translate in either the Body Menu or Eyes Menu!").setEphemeral(true).queue();
            return;
        }

        int x = Objects.requireNonNull(e.getOption("x")).getAsInt();
        int y = Objects.requireNonNull(e.getOption("y")).getAsInt();
        Helpers.currentUser.setTA(x, y);
        try {
            SendPFP.editEmbed(e);
        }
        catch (Exception exception) {
            e.reply("ERROR: Failed to edit the profile picture embed: " + exception.getMessage()).setEphemeral(true).queue();
        }
    }
}
