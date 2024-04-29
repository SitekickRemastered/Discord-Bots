package org.SitekickRemastered.listeners;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.SitekickRemastered.commands.CommandInterface;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandManager extends ListenerAdapter {

    String statusURL;
    ScheduledExecutorService pingThread = Executors.newSingleThreadScheduledExecutor();

    private final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager(Dotenv dotenv){
        statusURL = dotenv.get("AUTHICER_PING_LINK");
    }

    public void onReady(@NotNull ReadyEvent e) {

        for (CommandInterface ci : commands){
            e.getJDA().upsertCommand(ci.getName(), ci.getDescription()).addOptions(ci.getOptions()).setDefaultPermissions(ci.getPermissions()).queue();
        }

        // Sets a thread to run every minute to ping Authicer's status URL. If it fails, another bot alerts us, so we can fix it.
        pingThread.scheduleAtFixedRate(() -> {
            try {
                URLConnection conn = URI.create(statusURL).toURL().openConnection();
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                InputStream response = conn.getInputStream();
                response.close();
            }
            catch (IOException ex) {
                System.err.println("ERROR: Failed to send status ping to Authicer.");
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {

        // Checks which slash command has been used based on name
        for (CommandInterface command : commands) {
            if (e.getName().equals(command.getName())) {
                try { command.execute(e); }
                catch (IOException | ParseException ex) {
                    throw new RuntimeException(ex);
                }
                return;
            }
        }

    }

    public void add(CommandInterface command) {
        commands.add(command);
    }
}
