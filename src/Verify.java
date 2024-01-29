import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Verify {

    /** The verify command function. Sends a POST request to the server to verify a user.
     *  @param e - The Slash Command Interaction Event. Activates this function whenever it hears a slash command.
     *  @param code - The user's verify code that they send to the bot.
     */
    public static void verifyCommand(SlashCommandInteractionEvent e, String code) throws IOException, ParseException {

        // Setup and sending of HTTP POST request stuff
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://game.sitekickremastered.com/discord/verify_account");
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("token", code));
        params.add(new BasicNameValuePair("discord_id", e.getMember().getId()));
        httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        // Get the response from the server and parse it to a .json
        CloseableHttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        JSONObject json = new JSONObject(EntityUtils.toString(entity, StandardCharsets.UTF_8));
        //System.out.println(json);

        // If the response's error code was 1, we give the user the "Verified" role, then change their nickname in the server
        // Then the #general channel sends a message that the user's been verified.
        if (json.get("errorCode").equals(1)) {

            // Add the verified role
            Role role = e.getGuild().getRolesByName("Verified", true).get(0);
            e.getGuild().addRoleToMember(e.getMember(), role).queue();

            // Add the rank if it's they have at least 1XP
            if (!Objects.equals(json.get("rank").toString(), "None")){
                Role rank = e.getGuild().getRolesByName(json.get("rank").toString(), true).get(0);
                e.getGuild().addRoleToMember(e.getMember(), rank).queue();
            }

            // Change their name
            String gameName = json.get("username").toString();
            e.getMember().modifyNickname(gameName).queue();
            e.getGuild().getTextChannelsByName("general", true).get(0).sendMessage("<@" + e.getMember().getId() + "> has been verified.").queue();
            e.deferReply().queue(m -> m.deleteOriginal().queue());
        }
        // Otherwise, alert the user that they entered an invalid link code.
        else {
            e.reply("<@" + e.getMember().getId() + "> you have entered an invalid link code.").setEphemeral(true).queue();
        }
        httpclient.close();
        response.close();
        entity.close();
    }
}
