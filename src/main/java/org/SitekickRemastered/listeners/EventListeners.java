package org.SitekickRemastered.listeners;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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

import static org.SitekickRemastered.utils.Helpers.*;

public class EventListeners extends ListenerAdapter {

    String statusURL;
    Dotenv dotenv;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    Guild SK;
    public static ArrayList<Role> rankList = new ArrayList<>();
    public static ArrayList<Role> adminRoles = new ArrayList<>();
    public static ArrayList<Role> allRoles = new ArrayList<>();


    public EventListeners(Dotenv dotenv) {
        statusURL = dotenv.get("AUTHICER_PING_LINK");
        this.dotenv = dotenv;
    }

    public void onReady(@NotNull ReadyEvent e) {

        SK = e.getJDA().getGuildById("603580736250970144");

        rankList.addAll(List.of(
            SK.getRolesByName("Bronze", true).getFirst(),
            SK.getRolesByName("Silver", true).getFirst(),
            SK.getRolesByName("Gold", true).getFirst(),
            SK.getRolesByName("Amethyst", true).getFirst(),
            SK.getRolesByName("Onyx", true).getFirst()
        ));

        adminRoles.addAll(List.of(
            SK.getRolesByName("Administrator", true).getFirst(),
            SK.getRolesByName("Developer", true).getFirst(),
            SK.getRolesByName("Moderator", true).getFirst()
        ));

        allRoles.addAll(adminRoles);
        allRoles.addAll(rankList);
        allRoles.addAll( List.of(
            SK.getRolesByName("Artist", true).getFirst(),
            SK.getRolesByName("Writer", true).getFirst(),
            SK.getBoostRole(),
            SK.getRolesByName("YAP!", true).getFirst(),
            SK.getRolesByName("Contributor", true).getFirst(),
            SK.getRolesByName("Beta Tester", true).getFirst(),
            SK.getRolesByName("Verified", true).getFirst()
        ));

        // Sets a thread to run every minute to ping Authicer's status URL. If it fails, another bot alerts us, so we can fix it.
        scheduler.scheduleAtFixedRate(() -> {
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


    public void onMessageReceived(MessageReceivedEvent e) {

        // If the user sent a private message to Authicer, we send them some messages telling them how to use Authicer
        if (e.getChannelType() == ChannelType.PRIVATE && !e.getAuthor().isBot()) {
            e.getChannel().sendMessage("Message the https://discord.com/channels/603580736250970144/999864676982722560 channel to verify your account.").queue(message -> message.delete().queueAfter(60, TimeUnit.SECONDS));
            e.getChannel().sendMessage("This message will self destruct in one minute.").queue(message -> message.delete().queueAfter(60, TimeUnit.SECONDS));
        }

        //If the user posted in #link with something that is not /link, delete the message
        if (e.getChannel().toString().contains("link") && !Objects.requireNonNull(e.getMember()).getUser().isBot())
            e.getMessage().delete().queue();
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
     * Sends a list of all verified users with their roles to the game for badges and nitro emblems.
     *
     * @param e - Event listener - Generic event listener.
     */
    public void sendNames(Event e) throws IOException, ParseException {

        JSONObject json = new JSONObject();

        // Loads the list of members with the roles in allRoles, then adds them to the json
        List<String> members;
        for (Role r : allRoles){
            members = SK.getMembersWithRoles(r).stream().map(ISnowflake::getId).toList();
            json.put(r.getName(), members.isEmpty() ? "0" : String.join(",", members));
        }

        // Send off the information to the server.
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("token", dotenv.get("POST_TOKEN")));
        params.add(new BasicNameValuePair("json", json.toString()));
        postRequest(dotenv.get("NITRO_LINK"), params, "Failed to get the list of Nitro Users");
    }


    /**
     * Updates the ranks and nicknames of users in the Discord from the game.
     *
     * @param e - Event listener - Generic event listener.
     */
    public void updateUsers(Event e) throws IOException, ParseException {

        Role verifiedRole = SK.getRolesByName("Verified", true).getFirst();

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
                JSONObject jsonData = players.getJSONObject(j);
                Member m = Objects.requireNonNull(SK).getMemberById(jsonData.get("discordId").toString());
                if (m == null) {
                    System.out.println(Instant.now() + " Player '" + jsonData.get("username") + "' is no longer in the discord");
                    continue;
                }

                //Set Discord name to name in game
                if (m.getRoles().stream().noneMatch(r -> adminRoles.contains(r)) && (m.getNickname() == null || !Objects.equals(m.getNickname(), jsonData.get("username").toString()))) {
                    m.modifyNickname(jsonData.get("username").toString()).queue(
                        (success) -> System.out.println(Instant.now() + " " + m.getUser().getName() + " Nickname changed to in game name: " + m.getNickname()),
                        (error) -> System.out.println(Instant.now() + " Failed to set new nickname for user: " + m.getUser().getName())
                    );
                }

                // Set Rank Stuff
                if (!jsonData.get("rank").toString().equals("None")) {
                    Role gameRank = SK.getRolesByName(jsonData.get("rank").toString(), true).getFirst();

                    for (Role r : m.getRoles()){
                        if (r != gameRank && rankList.contains(r))
                            SK.removeRoleFromMember(UserSnowflake.fromId(m.getUser().getId()), r).queue(
                                (success) -> System.out.println(Instant.now() + " Rank Role '" + r.getName() + "' REMOVED from user: " + m.getEffectiveName())
                            );
                    }

                    // Add their current rank from the game to the discord
                    if (!m.getRoles().contains(gameRank))
                        SK.addRoleToMember(m, gameRank).queue(
                            (success) -> System.out.println(Instant.now() + " Rank Role '" + gameRank.getName() + "' ADDED to user: " + m.getEffectiveName())
                        );
                }

                // Add verified role if they don't have it.
                if (!m.getRoles().contains(verifiedRole))
                    SK.addRoleToMember(UserSnowflake.fromId(m.getUser().getId()), verifiedRole).queue(
                        (success) -> System.out.println(Instant.now() + " ADDED VERIFIED Role to user: " + m.getEffectiveName())
                    );
            }

            // Do another POST request for the next page.
            json = postRequest(dotenv.get("VERIFIED_MEMBERS_LINK") + "?page=" + i, params, "Failed to get the Verified Members List");
            players = json.getJSONArray("players");
            System.out.println("\n" + Instant.now() + " Retrieved Page " + (i + 1) + " of " + (json.getInt("totalPages") + 1) + " from the verified members link successfully.");
        }

        System.out.println("\n" + Instant.now() + " Finished scanning the Verified Members List!");
    }
}