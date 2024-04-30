package org.SitekickRemastered.listeners;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventListeners extends ListenerAdapter {

    String statusURL;
    ScheduledExecutorService pingThread = Executors.newSingleThreadScheduledExecutor();


    public EventListeners(Dotenv dotenv) {
        statusURL = dotenv.get("AUTHICER_PING_LINK");
    }


    public void onReady(@NotNull ReadyEvent e) {

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


    public void onMessageReceived(MessageReceivedEvent e) {

        // If the user sent a private message to Authicer, we send them some messages telling them how to use Authicer
        if (e.getChannelType() == ChannelType.PRIVATE && !e.getAuthor().isBot()) {
            e.getChannel().sendMessage("Message the https://discord.com/channels/603580736250970144/999864676982722560 channel to verify your account.").queue(message -> message.delete().queueAfter(60, TimeUnit.SECONDS));
            e.getChannel().sendMessage("This message will self destruct in one minute.").queue(message -> message.delete().queueAfter(60, TimeUnit.SECONDS));
        }

        //If the user posted in #link with something that is not /link, delete the message
        if (e.getChannel().toString().contains("link") && !Objects.requireNonNull(e.getMember()).getUser().isBot()) {
            e.getMessage().delete().queue();
        }
    }
}