package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.SitekickRemastered.utils.Helpers;
import org.SitekickRemastered.utils.SendPFP;

import java.util.List;
import java.util.Objects;

public class FlipCommand implements CommandInterface {

    @Override
    public String getName() {
        return "flip";
    }


    @Override
    public String getDescription() {
        return "Flip your profile picture.";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, "flip_direction", "Which axis to flip on?", true).addChoice("Horizontal", 1).addChoice("Vertical", 2));
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        if (!Helpers.currentUser.getLocation().equals("body") && !Helpers.currentUser.getLocation().equals("eyes")) {
            e.reply("Please use /flip in either the Body Menu or Eyes Menu!").setEphemeral(true).queue();
            return;
        }

        int userOption = Objects.requireNonNull(e.getOption("flip_direction")).getAsInt();

        // Flip horizontally and vertically.
        if (Helpers.currentUser.getFlipType() == 1 && userOption == 2 || Helpers.currentUser.getFlipType() == 2 && userOption == 1)
            Helpers.currentUser.setFlipType(3);

        // Flipped vertically
        else if (Helpers.currentUser.getFlipType() == 3 && userOption == 1)
            Helpers.currentUser.setFlipType(2);

        // Flip horizontally
        else if (Helpers.currentUser.getFlipType() == 3 && userOption == 2)
            Helpers.currentUser.setFlipType(1);

        // No flip
        else if (Helpers.currentUser.getFlipType() != 0)
            Helpers.currentUser.setFlipType(0);

        // Otherwise normally (FlipType is already 0)
        else
            Helpers.currentUser.setFlipType(userOption);

        try {
            SendPFP.editEmbed(e);
        }
        catch (Exception exception) {
            e.reply("ERROR: Failed to edit the profile picture embed: " + exception.getMessage()).setEphemeral(true).queue();
        }
    }
}