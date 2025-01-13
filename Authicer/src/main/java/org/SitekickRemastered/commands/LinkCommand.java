package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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

public class LinkCommand implements CommandInterface {

    @Override
    public String getName() {
        return "link";
    }


    @Override
    public String getDescription() {
        return "Links your Sitekick Remastered and Discord accounts.";
    }


    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "code", "Your code from the \"Link Discord\" Tab in Sitekick Remastered", true));
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.ENABLED;
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) throws IOException, ParseException {

        if (!e.getChannel().toString().contains("link")) {
            String replyMessage = String.format("""
                Oak's words echoed... "There's a time and place for everything but not now!"
                
                Please use the `/link` command in the <#%s> channel!
                [More information can be found here](https://sitekickremastered.com/wiki/guides/discord/#how-to-link-your-own-account)
                """, e.getJDA().getTextChannelsByName("link", true).getFirst().getId());
            e.reply(replyMessage).setEphemeral(true).queue();
            return;
        }

        if (e.getOption("code") == null) {
            String replyMessage = """
                Code syntax was incorrect. Please make sure you're typing the command properly.
                Usage: /link [code]
                
                [More information can be found here](https://sitekickremastered.com/wiki/guides/discord/#how-to-link-your-own-account)
                """;
            e.reply(replyMessage).setEphemeral(true).queue();
            return;
        }

        String code = Objects.requireNonNull(e.getOption("code")).getAsString().trim();

        // Setup and sending of HTTP POST request stuff
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://game.sitekickremastered.com/discord/verify_account");
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("token", code));
        params.add(new BasicNameValuePair("discord_id", Objects.requireNonNull(e.getMember()).getId()));
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
            Role role = Objects.requireNonNull(e.getGuild()).getRolesByName("Verified", true).getFirst();
            if (!addRoleToUser(e.getGuild(), e.getMember(), role))
                System.out.println("Failed to add verified role to " + e.getMember().getEffectiveName() + ".");

            // Add the rank if it's they have at least 1XP
            if (!Objects.equals(json.get("rank").toString(), "None")) {
                Role rank = e.getGuild().getRolesByName(json.get("rank").toString(), true).getFirst();
                if (!addRoleToUser(e.getGuild(), e.getMember(), rank))
                    System.out.println("Failed to update " + e.getMember().getEffectiveName() + "'s rank role.");
            }

            // Change their name
            String gameName = json.get("username").toString();
            e.getMember().modifyNickname(gameName).queue();
            if (!changeUsername(e.getMember(), gameName))
                System.out.println("Failed to update " + e.getMember().getEffectiveName() + "'s name.");

            // Send success message to general and defer reply
            e.getGuild().getTextChannelsByName("general", true).getFirst().sendMessage("<@" + e.getMember().getId() + "> has been verified.").queue();
            e.deferReply().queue(m -> m.deleteOriginal().queue());
        }
        // Otherwise, alert the user that they entered an invalid link code.
        else
            e.reply("Hey <@" + e.getMember().getId() + ">! You have entered an invalid link code.").setEphemeral(true).queue();

        httpclient.close();
        response.close();
        entity.close();
    }

    // Runs a loop to ensure a role was added to the user
    public boolean addRoleToUser(Guild g, Member m, Role r){
        for (int i = 0; i < 5; i++){
            g.addRoleToMember(m, r).queue();
            if (m.getRoles().contains(r))
                break;
        }
        return m.getRoles().contains(r);
    }


    // Runs a loop to ensure the users nickname was changed
    public boolean changeUsername(Member m, String gameName) {
        for (int i = 0; i < 5; i++) {
            m.modifyNickname(gameName).queue();
            if (Objects.equals(m.getNickname(), gameName))
                break;
        }
        return Objects.equals(m.getNickname(), gameName);
    }
}
