package org.SitekickRemastered.listeners;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.SitekickRemastered.commands.MetricsCommand.updateMetrics;
import static org.SitekickRemastered.utils.Helpers.getRole;
import static org.SitekickRemastered.utils.Helpers.postRequest;

public class EventListeners extends ListenerAdapter {

    String statusURL;
    Dotenv dotenv;
    String roleAnnounceMessageChannel, roleAnnounceMessageId;
    String metricsMessageChannel, metricsMessageId;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    public ArrayList<String> rankList = new ArrayList<>() {
        {
            add("None");
            add("Bronze");
            add("Silver");
            add("Gold");
            add("Amethyst");
            add("Onyx");
            add("Diamond");
        }
    };


    public EventListeners(Dotenv dotenv, String ramc, String rami, String mmc, String mmi) {
        statusURL = dotenv.get("KABLOOEY_PING_LINK");
        this.dotenv = dotenv;
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

        // Nitro and Rank / Name change stuff.
        scheduler.scheduleAtFixedRate(() -> {
            try {
                sendNames(e);
            }
            catch (IOException | ParseException ignored) {
            }

            try {
                updateUsers(e);
            }
            catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
        }, 0, 1, TimeUnit.HOURS);
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
     * Updates the nitro user roles when the server is boosted.
     *
     * @param e - GuildMemberUpdateBoostTimeEvent listener - Listens for when a user boosts the server.
     */
    public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent e) {
        try {
            sendNames(e);
        }
        catch (IOException | ParseException ignored) {
        }
    }


    /**
     * Sends a list of all users with the Nitro Role to the server.
     * This is used so users in the game can have the nitro emblem on SK-TV
     *
     * @param e - Event listener - Generic event listener.
     */
    public void sendNames(Event e) throws IOException, ParseException {

        // Create a list of the IDs of all users who have boosted the server via their Nitro role.
        String boosters;
        List<String> members = new ArrayList<>();
        for (Member m : e.getJDA().getGuilds().getFirst().loadMembers().get()) {
            if (m.getRoles().contains(e.getJDA().getGuilds().getFirst().getBoostRole())) {
                members.add(m.getId());
            }
        }

        // If there are no members with the Nitro role, we make the send message 0.
        // Otherwise, join the list as a string using commas
        boosters = (members.isEmpty()) ? "0" : members.stream().map(Object::toString).collect(Collectors.joining(","));

        // Send off the information to the server.
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("token", dotenv.get("POST_TOKEN")));
        params.add(new BasicNameValuePair("ids", boosters));
        postRequest(dotenv.get("NITRO_LINK"), params, "Failed to get the list of Nitro Users");
    }


    /**
     * Updates the ranks and nicknames of users in the Discord.
     *
     * @param e - Event listener - Generic event listener.
     */
    public void updateUsers(Event e) throws IOException, ParseException {

        // Get the Sitekick server
        Guild SK = e.getJDA().getGuildById("603580736250970144");

        Role verifiedRole = SK.getRolesByName("Verified", true).getFirst();

        ArrayList<String> roleList = new ArrayList<>() {
            {
                add("Administrator");
                add("Developer");
                add("Moderator");
            }
        };

        // Set up the first post
        // We have to do this first to get the total pages from the request. Then we can loop.
        List<NameValuePair> params = new ArrayList<>(1);
        params.add(new BasicNameValuePair("token", dotenv.get("POST_TOKEN")));
        JSONObject json = postRequest(dotenv.get("VERIFIED_MEMBERS_LINK"), params, "Failed to get the Verified Members List");
        JSONArray players = json.getJSONArray("players");
        System.out.println("\n" + Instant.now() + " Retrieved Page 1 of " + (json.getInt("totalPages") + 1) + " from the verified members link successfully.");

        // Go through every page starting at 1 since we already got page 0 from the first thing
        for (int i = 1; i <= json.getInt("totalPages"); i++) {

            // Go through every player on this page.
            for (int j = 0; j < players.length(); j++) {

                // Get the member from the json and check if they're still in the discord
                JSONObject member = players.getJSONObject(j);
                Member m = Objects.requireNonNull(SK).getMemberById(member.get("discordId").toString());
                if (m == null) {
                    System.out.println(Instant.now() + " Player '" + member.get("username") + "' is no longer in the discord");
                    continue;
                }

                //Set Discord name to name in game
                if (m.getRoles().stream().noneMatch(element -> roleList.contains(element.getName())) && (m.getNickname() == null || !Objects.equals(m.getNickname(), member.get("username").toString()))) {
                    m.modifyNickname(member.get("username").toString()).queue(
                        (success) -> System.out.println(Instant.now() + " " + m.getUser().getName() + " Nickname changed to in game name: " + m.getNickname()),
                        (error) -> System.out.println(Instant.now() + " Failed to set new nickname for user: " + m.getUser().getName())
                    );
                }

                // Set Rank Stuff
                if (!member.get("rank").toString().equals("None")) {
                    Role gameRank = SK.getRolesByName(member.get("rank").toString(), true).getFirst();

                    // If the player currently has any of the roles in the rank list and that role is not the same as their current one, remove it.
                    if (m.getRoles().stream().anyMatch(element -> rankList.contains(element.getName()))) {
                        if (!m.getRoles().contains(gameRank)) {
                            for (Role r : m.getRoles()) {
                                if (rankList.contains(r.getName()))
                                    SK.removeRoleFromMember(UserSnowflake.fromId(m.getUser().getId()), r).queue(successMessage -> System.out.println(Instant.now() + " Rank Role '" + gameRank.getName() + "' REMOVED from user: " + m.getEffectiveName()));
                            }
                        }
                    }

                    // Add their current rank from the game to the discord
                    if (!m.getRoles().contains(gameRank))
                        SK.addRoleToMember(m, gameRank).queue(successMessage -> System.out.println(Instant.now() + " Rank Role '" + gameRank.getName() + "' ADDED to user: " + m.getEffectiveName()));
                }

                // Add verified role if they don't have it.
                if (!m.getRoles().contains(verifiedRole))
                    SK.addRoleToMember(UserSnowflake.fromId(m.getUser().getId()), verifiedRole).queue(successMessage -> System.out.println(Instant.now() + " ADDED VERIFIED Role to user: " + m.getEffectiveName()));
            }

            // Do another POST request for the next page.
            json = postRequest(dotenv.get("VERIFIED_MEMBERS_LINK") + "?page=" + i, params, "Failed to get the Verified Members List");
            players = json.getJSONArray("players");
            System.out.println("\n" + Instant.now() + " Retrieved Page " + (i + 1) + " of " + (json.getInt("totalPages") + 1) + " from the verified members link successfully.");
        }

        System.out.println("\n" + Instant.now() + " Finished scanning the Verified Members List!");
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