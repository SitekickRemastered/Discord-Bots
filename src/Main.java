import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        String token = dotenv.get("PAUL_TOKEN"); //PAUL'S TOKEN
        DefaultShardManagerBuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.of(Activity.ActivityType.PLAYING, "Paul"))
                .addEventListeners(new CommandManager()).build();
    }
}
