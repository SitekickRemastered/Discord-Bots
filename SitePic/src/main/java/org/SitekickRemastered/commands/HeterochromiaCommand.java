package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;
import org.SitekickRemastered.utils.SendPFP;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HeterochromiaCommand implements CommandInterface {

    @Override
    public String getName() {
        return "heterochromia";
    }


    @Override
    public String getDescription() {
        return "Choose a different colour for each eye.";
    }


    @Override
    public List<OptionData> getOptions() {
        return Arrays.asList(
            new OptionData(OptionType.STRING, "colour1", "Colour for the left eye.", true),
            new OptionData(OptionType.STRING, "colour2", "Colour for the right eye.", true)
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (!Helpers.currentUser.getLocation().equals("eyes")) {
            e.reply("Please use /heterochromia in the Eyes Menu!").setEphemeral(true).queue();
            return;
        }

        // Get all arguments for the command
        Color g1 = Helpers.convertHex(Objects.requireNonNull(e.getOption("colour1")).getAsString().split(" "));
        Color g2 = Helpers.convertHex(Objects.requireNonNull(e.getOption("colour2")).getAsString().split(" "));

        // Check validity
        if (g1 == null || g2 == null) {
            e.reply("Command or colour code not recognized. Please input either RGB or HEX formatting.").setEphemeral(true).queue();
            return;
        }

        // If valid, set a 50 / 50 gradient across the entire body that will make the eyes be different colours
        Point2D start = new Point2D.Float((Helpers.imageSize / 2) + 4, 0.0f);
        Point2D end = new Point2D.Float((Helpers.imageSize / 2) + 8, 0.0f);
        Paint newColour = new GradientPaint(start, g1, end, g2);
        Helpers.currentUser.setCustomEyeColour(newColour);

        try {
            SendPFP.editEmbed(e);
        }
        catch (Exception exception) {
            e.reply("ERROR: Failed to edit the profile picture embed: " + exception.getMessage()).setEphemeral(true).queue();
        }
    }
}