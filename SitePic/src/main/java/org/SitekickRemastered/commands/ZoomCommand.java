package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;
import org.SitekickRemastered.utils.SendPFP;

import java.util.List;
import java.util.Objects;

public class ZoomCommand implements CommandInterface {

    @Override
    public String getName() {
        return "zoom";
    }


    @Override
    public String getDescription() {
        return "Zoom into your profile picture.";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(
            new OptionData(OptionType.INTEGER, "zoom_amount", "The amount to zoom in (percentage)", true)
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (!Helpers.currentUser.getLocation().equals("body") && !Helpers.currentUser.getLocation().equals("eyes")) {
            e.reply("Please use /zoom in either the Body Menu or Eyes Menu!").setEphemeral(true).queue();
            return;
        }

        int zoom = Objects.requireNonNull(e.getOption("zoom_amount")).getAsInt();
        Helpers.currentUser.setZA(zoom);

        try {
            SendPFP.editEmbed(e);
        }
        catch (Exception exception) {
            e.reply("ERROR: Failed to edit the profile picture embed: " + exception.getMessage()).setEphemeral(true).queue();
        }
    }
}