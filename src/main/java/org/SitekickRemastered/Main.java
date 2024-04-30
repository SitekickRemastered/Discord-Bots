package org.SitekickRemastered;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.SitekickRemastered.commands.LinkCommand;
import org.SitekickRemastered.listeners.CommandManager;
import org.SitekickRemastered.listeners.EventListeners;

public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory("src/main/java/org/SitekickRemastered/.env").load();
        String token = dotenv.get("AUTHICER_TOKEN");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setActivity(Activity.of(Activity.ActivityType.PLAYING, "Authenticate in #link!"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        ShardManager sm = builder.build();
        sm.addEventListener(new EventListeners(dotenv));

        CommandManager cm = new CommandManager();
        cm.add(new LinkCommand());

        sm.addEventListener(cm);
    }
}