package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class DelistCommand implements CommandInterface {

    List<String> bannedUsers;
    String listPath;


    public DelistCommand(List<String> bannedUsers, String listPath) {
        this.bannedUsers = bannedUsers;
        this.listPath = listPath;
    }


    @Override
    public String getName() {
        return "enpaul";
    }


    @Override
    public String getDescription() {
        return "Makes Paul re-watch a user. Name will be removed from a list (bannedUsers.txt).";
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

        // Removes the user from the bannedUsers file.
        User user = Objects.requireNonNull(e.getOption("user")).getAsUser();
        File bannedListFile = new File(listPath);

        try {
            // Create a temp file and copy over the names that aren't the one to remove
            File tempFile = new File("src/temp.txt");
            BufferedReader reader = new BufferedReader(new FileReader(bannedListFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().equals(user.getId()))
                    continue;
                writer.write(currentLine + System.lineSeparator());
            }
            writer.close();
            reader.close();

            // Remove their name from the list, and rename the temp file
            if (bannedListFile.delete() && bannedUsers.remove(user.getId()) && tempFile.renameTo(new File(listPath)))
                e.reply(user.getName() + " has been removed from the blacklist.").setEphemeral(true).queue();
            else
                e.reply("Failed to remove " + user.getName() + " from the blacklist").setEphemeral(true).queue();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
