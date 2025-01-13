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

public class PresetsCommand implements CommandInterface {

    @Override
    public String getName() {
        return "presets";
    }


    @Override
    public String getDescription() {
        return "Choose from a list of preset profile pictures";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(
            new OptionData(OptionType.STRING, "presets", "Choose a preset for your profile picture", true)
                .addChoice("Kablooey", "kablooey")
                .addChoice("Authicer", "authicer")
                .addChoice("Paul", "paul")
                .addChoice("Deadkick", "deadkick")
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (!Helpers.currentUser.getLocation().equals("body") && !Helpers.currentUser.getLocation().equals("eyes")) {
            e.reply("Please use /presets in either the Body Menu or Eyes Menu!").setEphemeral(true).queue();
            return;
        }

        switch (Objects.requireNonNull(e.getOption("presets")).getAsString()) {
            case "kablooey":
                Helpers.currentUser.setBodyColour(new Color(0, 122, 254));
                Helpers.currentUser.setEyePic("GreenEyes.png");
                break;
            case "authicer":
                Helpers.currentUser.setBodyColour(new Color(101, 75, 174));
                Helpers.currentUser.setCustomEyeColour(new Color(163, 235, 219));
                break;
            case "paul":
                Helpers.currentUser.setBodyColour(new Color(233, 138, 3));
                Helpers.currentUser.setCustomEyeColour(new Color(24, 158, 170));
                break;
            case "deadkick":
                Helpers.currentUser.setEyePic("SquintEyes.png");
                Helpers.currentUser.setBodyColour(null);
                Helpers.currentUser.addToBSL(new Color(220, 209, 175));
                Helpers.currentUser.addToBSL(new Color(220, 209, 175));
                Helpers.currentUser.addToBSL(new Color(220, 209, 175));
                Helpers.currentUser.addToBSL(new Color(220, 209, 175));
                Helpers.currentUser.addToBSL(new Color(220, 209, 175));
                Helpers.currentUser.addToBSL(new Color(163, 140, 71));
                Helpers.currentUser.setDeadkick(true);
                break;
        }

        try {
            SendPFP.editEmbed(e);
        }
        catch (Exception exception) {
            e.reply("ERROR: Failed to edit the profile picture embed: " + exception.getMessage()).setEphemeral(true).queue();
        }
    }
}