package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.List;

public interface CommandInterface {

    // Returns the name of the command:
    String getName();

    // Returns the description of the command:
    String getDescription();

    // Returns the options for the command:
    List<OptionData> getOptions();

    DefaultMemberPermissions getPermissions();

    // Executes the command:
    void execute(SlashCommandInteractionEvent e) throws IOException, ParseException;
}