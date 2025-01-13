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

public class LinearGradientCommand implements CommandInterface {

    @Override
    public String getName() {
        return "linear_gradient";
    }


    @Override
    public String getDescription() {
        return "Set the colour to a linear gradient.";
    }


    @Override
    public List<OptionData> getOptions() {
        return Arrays.asList(
            new OptionData(OptionType.STRING, "colour1", "First Colour", true),
            new OptionData(OptionType.STRING, "colour2", "Second Colour", true),
            new OptionData(OptionType.INTEGER, "angle", "Angle of the gradient (in degrees)", false),
            new OptionData(OptionType.INTEGER, "colour1_percentage", "The amount of space that the first colour will take (percentage)", false)
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (!Helpers.currentUser.getLocation().equals("body") && !Helpers.currentUser.getLocation().equals("eyes")) {
            e.reply("Please use /linear_gradient in either the Body Menu or Eyes Menu!").setEphemeral(true).queue();
            return;
        }

        // Get all arguments for the command
        Color g1 = Helpers.convertHex(Objects.requireNonNull(e.getOption("colour1")).getAsString().split(" "));
        Color g2 = Helpers.convertHex(Objects.requireNonNull(e.getOption("colour2")).getAsString().split(" "));
        int angle = e.getOption("angle") != null ? Objects.requireNonNull(e.getOption("angle")).getAsInt() : 0;

        // If not valid, send an error
        if (g1 == null || g2 == null) {
            e.reply("Command or colour code not recognized. Please input either RGB or HEX formatting.").setEphemeral(true).queue();
            return;
        }

        int percentage = (e.getOption("colour1_percentage") != null) ? Objects.requireNonNull(e.getOption("colour1_percentage")).getAsInt() : 50;

        // Otherwise get a new paint using getGradientColour(), and set the body or custom eye colour
        Paint newColour = getLGradientColour(g1, g2, angle, percentage);
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
     * @param g1    - The first colour of the gradient
     * @param g2    - The second colour of the gradient
     * @param angle - The angle the user wants the gradient to be
     * @return a Paint that will be the gradient of the Body or Eyes.
     */
    public Paint getLGradientColour(Color g1, Color g2, int angle, int percentage) {
        Point2D[] points = getAngle(angle, percentage);
        return new GradientPaint(points[0], g1, points[1], g2);
    }


    /**
     * This function takes an angle and calculates the starting and end points for a gradient.
     *
     * @param angle - A given angle in degrees.
     * @return - Returns an array that includes the start and end points.
     */
    public Point2D[] getAngle(int angle, int percentage) {

        // Get the angle within a range of 360 degrees, and get the centre of the image.
        angle = angle % 360;
        float centre = Helpers.imageSize / 2;
        float offset = (float) (Helpers.imageSize * (Math.tan(Math.PI / 2 * (((float) percentage / 100) - 0.5))));

        // If the angle is facing left or right,
        if (angle <= 45 || angle >= 135 && angle <= 225 || angle >= 315) {

            // Get the y coord using the centre and the tan function.
            float yCoord = (float) (centre * Math.tan(Math.toRadians(angle)));

            if (angle >= 135 && angle <= 225) {
                return new Point2D[]{
                    new Point2D.Float(Helpers.imageSize - offset, centre - yCoord),
                    new Point2D.Float(0.0f - offset, centre + yCoord)
                };
            }
            else {
                return new Point2D[]{
                    new Point2D.Float(0.0f + offset, centre + yCoord),
                    new Point2D.Float(Helpers.imageSize + offset, centre - yCoord)
                };
            }
        }

        // If the angle is facing up or down.
        else {

            // If the angle is directly up or down, we can just make the gradient go from one corner to the adjacent corner
            if (angle == 90) {
                return new Point2D[]{
                    new Point2D.Float(0.0f, Helpers.imageSize - offset),
                    new Point2D.Float(0.0f, 0.0f - offset)
                };
            }
            else if (angle == 270) {
                return new Point2D[]{
                    new Point2D.Float(0.0f, 0.0f + offset),
                    new Point2D.Float(0.0f, Helpers.imageSize + offset)
                };
            }

            // Get the x coord using the centre and the tan function.
            float xCoord = (float) (centre + (Helpers.imageSize / (2 * Math.tan(Math.toRadians(angle)))));

            if (angle <= 135) {
                return new Point2D[]{
                    new Point2D.Float(Helpers.imageSize - xCoord, Helpers.imageSize - offset),
                    new Point2D.Float(0.0f + xCoord, 0.0f - offset)
                };
            }
            else {
                return new Point2D[]{
                    new Point2D.Float(0.0f + xCoord, 0.0f + offset),
                    new Point2D.Float(Helpers.imageSize - xCoord, Helpers.imageSize + offset)
                };
            }
        }
    }
}