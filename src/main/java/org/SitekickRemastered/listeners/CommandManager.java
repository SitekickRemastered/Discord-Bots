package org.SitekickRemastered.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.SitekickRemastered.commands.CommandInterface;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private final List<CommandInterface> commands = new ArrayList<>();


    public void onReady(@NotNull ReadyEvent e) {
        for (CommandInterface ci : commands) {
            e.getJDA().upsertCommand(ci.getName(), ci.getDescription()).addOptions(ci.getOptions()).setDefaultPermissions(ci.getPermissions()).queue();
        }
    }


    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {

        // Checks which slash command has been used based on name
        for (CommandInterface command : commands) {
            if (e.getName().equals(command.getName())) {
                command.execute(e);
                return;
            }
        }
    }


    public void add(CommandInterface command) {
        commands.add(command);
    }
}
