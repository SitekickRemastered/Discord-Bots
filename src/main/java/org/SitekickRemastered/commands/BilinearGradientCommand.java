package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.BiLinearGradientPaint;
import org.SitekickRemastered.utils.Helpers;
import org.SitekickRemastered.utils.SendPFP;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BilinearGradientCommand implements CommandInterface {

    @Override
    public String getName() {
        return "bilinear_gradient";
    }


    @Override
    public String getDescription() {
        return "Set the colour to a bilinear gradient.";
    }


    @Override
    public List<OptionData> getOptions() {
        return Arrays.asList(
            new OptionData(OptionType.STRING, "colour1", "First Colour", true),
            new OptionData(OptionType.STRING, "colour2", "Second Colour", true),
            new OptionData(OptionType.STRING, "colour3", "Third Colour", true),
            new OptionData(OptionType.STRING, "colour4", "Fourth Colour", false)
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (!Helpers.currentUser.getLocation().equals("body") && !Helpers.currentUser.getLocation().equals("eyes")) {
            e.reply("Please use /bilinear_gradient in either the Body Menu or Eyes Menu!").setEphemeral(true).queue();
            return;
        }

        // Get all arguments for the command
        Color g1 = Helpers.convertHex(Objects.requireNonNull(e.getOption("colour1")).getAsString().split(" "));
        Color g2 = Helpers.convertHex(Objects.requireNonNull(e.getOption("colour2")).getAsString().split(" "));
        Color g3 = Helpers.convertHex(Objects.requireNonNull(e.getOption("colour3")).getAsString().split(" "));
        Color g4 = e.getOption("colour4") != null ? Helpers.convertHex(Objects.requireNonNull(e.getOption("colour4")).getAsString().split(" ")) : null;

        // Check validity of inputs
        if (g1 == null || g2 == null || g3 == null || (e.getOption("colour4") != null && g4 == null)) {
            e.reply("Command or colour code not recognized. Please input either RGB or HEX formatting.").setEphemeral(true).queue();
            return;
        }

        Paint newColour = getBLGradientColour(g1, g2, g3, g4);
        if (Helpers.currentUser.getLocation().equals("body"))
            Helpers.currentUser.setBodyColour(newColour);
        else
            Helpers.currentUser.setCustomEyeColour(newColour);

        try {
            SendPFP.editEmbed(e);
        }
        catch (Exception exception) {
            e.reply("ERROR: Failed to edit the profile picture embed: " + exception.getMessage()).setEphemeral(true).queue();
        }
    }


    /**
     * A function that converts a user's colour inputs into a gradient.
     *
     * @param g1 - The first colour of the gradient
     * @param g2 - The second colour of the gradient
     * @param g3 - The third colour of the gradient
     * @param g4 - The (optional) fourth colour of the gradient
     * @return a Paint that will be the gradient of the Body or Eyes.
     */
    public Paint getBLGradientColour(Color g1, Color g2, Color g3, Color g4) {
        BufferedImage image = new BufferedImage((int) Helpers.imageSize, (int) Helpers.imageSize, BufferedImage.TYPE_INT_ARGB);
        return (g4 == null) ? new BiLinearGradientPaint(image, g1, g2, g3) : new BiLinearGradientPaint(image, g1, g2, g3, g4);
    }
}
