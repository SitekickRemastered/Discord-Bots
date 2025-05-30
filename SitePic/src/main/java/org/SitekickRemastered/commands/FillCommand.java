package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;
import org.SitekickRemastered.utils.SendPFP;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class FillCommand implements CommandInterface {

    @Override
    public String getName() {
        return "fill";
    }


    @Override
    public String getDescription() {
        return "Allows you to add colour based on Hex or RGB values.";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "hex_or_rgb", "Hex or RGB Code", true));
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (!Helpers.currentUser.getLocation().equals("body") && !Helpers.currentUser.getLocation().equals("eyes")) {
            e.reply("Please use /fill in either the Body Menu or Eyes Menu!").setEphemeral(true).queue();
            return;
        }

        // Split the HEX / RGB up to make sure it's valid
        Color nc = Helpers.convertHex(Objects.requireNonNull(e.getOption("hex_or_rgb")).getAsString().split(" "));
        if (nc == null) {
            e.reply("Command or colour code not recognized. Please input either RGB or HEX formatting.").setEphemeral(true).queue();
            return;
        }

        // Set the body colour or custom eye colour if valid
        if (Helpers.currentUser.getLocation().equals("body"))
            Helpers.currentUser.setBodyColour(nc);
        else
            Helpers.currentUser.setCustomEyeColour(nc);

        try {
            SendPFP.editEmbed(e);
        }
        catch (Exception exception) {
            e.reply("ERROR: Failed to edit the profile picture embed: " + exception.getMessage()).setEphemeral(true).queue();
        }
    }
}