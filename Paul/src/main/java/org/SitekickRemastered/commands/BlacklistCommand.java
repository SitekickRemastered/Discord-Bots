package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class BlacklistCommand implements CommandInterface {

    List<String> bannedUsers;
    String listPath;


    public BlacklistCommand(List<String> bannedUsers, String listPath) {
        this.bannedUsers = bannedUsers;
        this.listPath = listPath;
    }


    @Override
    public String getName() {
        return "depaul";
    }


    @Override
    public String getDescription() {
        return "Makes Paul ignore a user. Name will be added to a list (bannedUsers.txt).";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "user", "Member you want to select.", true));
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.DISABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {

        // Writes the user's ID to the bannedUsers file
        User user = Objects.requireNonNull(e.getOption("user")).getAsUser();
        File bannedListFile = new File(listPath);

        try {
            BufferedWriter log = new BufferedWriter(new FileWriter(bannedListFile));
            log.write(user.getId());
            log.close();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // Add the user to the list.
        bannedUsers.add(user.getId());
        e.reply(user.getName() + " has been added to the blacklist.").setEphemeral(true).queue();

    }
}