package org.SitekickRemastered.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.SitekickRemastered.commands.MetricsCommand.updateMetrics;
import static org.SitekickRemastered.utils.Helpers.getRole;

public class EventListeners extends ListenerAdapter {

    String statusURL;
    String roleAnnounceMessageChannel, roleAnnounceMessageId;
    String metricsMessageChannel, metricsMessageId;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);


    public EventListeners(String statusURL, String ramc, String rami, String mmc, String mmi) {
        this.statusURL = statusURL;
        this.roleAnnounceMessageChannel = ramc;
        this.roleAnnounceMessageId = rami;
        this.metricsMessageChannel = mmc;
        this.metricsMessageId = mmi;
    }


    public void onReady(@NotNull ReadyEvent e) {

        // Sets a thread to run every minute to ping Kablooey's status URL. If it fails, another bot alerts us, so we can fix it.
        scheduler.scheduleAtFixedRate(() -> {
            try {
                URLConnection conn = URI.create(statusURL).toURL().openConnection();
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                InputStream response = conn.getInputStream();
                response.close();
            }
            catch (IOException ex) {
                System.err.println("ERROR: Failed to send status ping to Kablooey.");
            }
        }, 0, 60, TimeUnit.SECONDS);

        // If we successfully got the metric channel message information, we start a scheduler that updates the info every minute
        if (!metricsMessageChannel.isEmpty()) {
            scheduler.scheduleAtFixedRate(() -> {
                if (!metricsMessageChannel.isEmpty()) {
                    // Get the message and create a new embed with the new information.
                    Objects.requireNonNull(e.getJDA().getTextChannelById(metricsMessageChannel)).retrieveMessageById(metricsMessageId).queue(m -> {
                        try {
                            updateMetrics(m);
                        }
                        catch (IOException | ParseException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
                else
                    scheduler.shutdownNow();
            }, 0, 60, TimeUnit.SECONDS);
        }
    }


    public void onGuildReady(@NotNull GuildReadyEvent e) {
        assignRoles(e);
    }


    public void onMessageReactionAdd(MessageReactionAddEvent r) {
        if (Objects.requireNonNull(r.getUser()).isBot())
            return;
        if (!r.getMessageId().equals(roleAnnounceMessageId))
            return;
        String roleToAdd = getRole.get(":" + r.getReaction().getEmoji().getAsReactionCode());
        if (roleToAdd != null)
            r.getGuild().addRoleToMember(UserSnowflake.fromId(r.getUserId()), r.getGuild().getRolesByName(roleToAdd, true).getFirst()).queue();
    }


    public void onMessageReactionRemove(MessageReactionRemoveEvent r) {
        if (!r.getMessageId().equals(roleAnnounceMessageId))
            return;
        String roleToRemove = getRole.get(":" + r.getReaction().getEmoji().getAsReactionCode());
        if (roleToRemove != null)
            r.getGuild().removeRoleFromMember(UserSnowflake.fromId(r.getUserId()), r.getGuild().getRolesByName(roleToRemove, true).getFirst()).queue();
    }


    /**
     * Assigns roles to a user from the reactions of the reaction message
     *
     * @param e - GuildReadyEvent listener - this activates whenever the bot successfully connects to the guild.
     */
    public void assignRoles(GuildReadyEvent e) {

        // If we have the message, loop through everyone who has reacted to that message.
        // Depending on the reaction, add the reaction if they don't already have it.
        // Role information is retrieved from the HashMap.
        if (!roleAnnounceMessageId.isEmpty()) {
            Objects.requireNonNull(e.getJDA().getTextChannelById(roleAnnounceMessageChannel)).retrieveMessageById(roleAnnounceMessageId).queue(m -> {
                for (MessageReaction r : m.getReactions()) {
                    String rtaStr = getRole.get(":" + r.getEmoji().getAsReactionCode());
                    if (rtaStr != null) {
                        Role roleToAdd = r.getGuild().getRolesByName(rtaStr, true).getFirst();
                        r.retrieveUsers().queue(users -> {
                            for (User u : users) {
                                Member member = e.getGuild().getMemberById(u.getId());
                                if (member != null && !u.isBot() && roleToAdd != null && !member.getRoles().contains(roleToAdd)) {
                                    e.getGuild().addRoleToMember(u, roleToAdd).queue();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}