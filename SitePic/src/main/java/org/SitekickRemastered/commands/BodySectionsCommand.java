package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;
import org.SitekickRemastered.utils.SendPFP;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BodySectionsCommand implements CommandInterface {

    @Override
    public String getName() {
        return "body_sections";
    }


    @Override
    public String getDescription() {
        return "Choose a different colour for every section of the body.";
    }


    @Override
    public List<OptionData> getOptions() {
        return Arrays.asList(
            new OptionData(OptionType.STRING, "section1", "Upper Left Section Colour", true),
            new OptionData(OptionType.STRING, "section2", "Upper Right Section Colour", true),
            new OptionData(OptionType.STRING, "section3", "Middle Right Section Colour", true),
            new OptionData(OptionType.STRING, "section4", "Lower Left Section Colour", true),
            new OptionData(OptionType.STRING, "section5", "Lower Right Section Colour", true),
            new OptionData(OptionType.STRING, "lines", "The lines of the Sitekick", true)
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (!Helpers.currentUser.getLocation().equals("body") && !Helpers.currentUser.getLocation().equals("eyes")) {
            e.reply("Please use /body_sections in either the Body Menu or Eyes Menu!").setEphemeral(true).queue();
            return;
        }

        // Get all arguments for the command
        Color g1 = Helpers.convertHex(Objects.requireNonNull(e.getOption("section1")).getAsString().split(" "));
        Color g2 = Helpers.convertHex(Objects.requireNonNull(e.getOption("section2")).getAsString().split(" "));
        Color g3 = Helpers.convertHex(Objects.requireNonNull(e.getOption("section3")).getAsString().split(" "));
        Color g4 = Helpers.convertHex(Objects.requireNonNull(e.getOption("section4")).getAsString().split(" "));
        Color g5 = Helpers.convertHex(Objects.requireNonNull(e.getOption("section5")).getAsString().split(" "));
        Color lines = Helpers.convertHex(Objects.requireNonNull(e.getOption("lines")).getAsString().split(" "));

        // Check validity
        if (g1 == null || g2 == null || g3 == null || g4 == null || g5 == null || lines == null) {
            e.reply("Command or colour code not recognized. Please input either RGB or HEX formatting.").setEphemeral(true).queue();
            return;
        }

        Helpers.currentUser.clearBSL();

        // If  valid, add each colour to a list that will be used to set the colour to each section.
        Helpers.currentUser.setBodyColour(null);
        Helpers.currentUser.addToBSL(g1);
        Helpers.currentUser.addToBSL(g2);
        Helpers.currentUser.addToBSL(g3);
        Helpers.currentUser.addToBSL(g4);
        Helpers.currentUser.addToBSL(g5);
        Helpers.currentUser.addToBSL(lines);

        try {
            SendPFP.editEmbed(e);
        }
        catch (Exception exception) {
            e.reply("ERROR: Failed to edit the profile picture embed: " + exception.getMessage()).setEphemeral(true).queue();
        }
    }
}