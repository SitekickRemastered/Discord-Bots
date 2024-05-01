package org.SitekickRemastered;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.SitekickRemastered.commands.*;
import org.SitekickRemastered.listeners.CommandManager;
import org.SitekickRemastered.listeners.EventListeners;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        System.out.println("""
                                  :+#.                                                                        \s
                                   =-                                                                         \s
                                   :.                                                                 .:      .
                                    -                                                           :=+##@@*+*#%@@:
                                    =                                      .:=+:   .:--        .@*=-=@#++=*@#.\s
                                    :.                                 :##%#*@@####@@@#- :=+*+-##---#+--=#@=  \s
                                    -##*==++=-:    ..   :=+*#@@.       %#---#%=--=*=-=#@%#+===#@=--==--+@%:   \s
                                    *@@#*++=++#@*#%#*%##%+=-*@#==+##*==@=--+*---*@%**%@*--++-+%#------+@*     \s
                                   #@*=--=-----%@#==+#@%=---**%%*=--=*@*---=--=#@%--=%=-=%@@@@@=----:.-@+     \s
                                  #%=--*@@%=+*#@@*+=#-..  .:-+-.:*#::-@-------*@@=--#=--%@@#@@+. .*   :@#     \s
                                 .@+-----=+*#%@@#  .#=-   #@@:  ...:-*+   .   +@=  -@.  ==. -%. .=%   .@@     \s
                                  %%*=:..     :%.  #@@.  =@@*  *%***#%   ==   =#   %@+.  .-*@@@@@@%-=+#@@:    \s
                                +#%#*%@@@@=   -=  -@@=   :=*#  ..  +@=.:-%+   :##%@@@%#*****++=======-:       \s
                               ##    -+=-.   =*  .%@@-    :@@#==+#@@@@@@@@#-====::.                           \s
                               @+         :+%@%#######*++++==-:::::..                                         \s
                               :##+===+*%@#=:.                                                                \s
                                 .:===-:.                                                                     \s""");
        
        Dotenv dotenv = Dotenv.configure().filename(".env").load();
        String token = dotenv.get("KABLOOEY_TOKEN");
        String roleAnnounceFilePath = "messageId1.txt";
        String metricsFilePath = "messageId2.txt";

        String roleAnnounceMessageChannel, roleAnnounceMessageId;
        String metricsMessageChannel, metricsMessageId;

        // Try to get the information from the .txt files
        try {
            String[] temp = Files.readString(Path.of(roleAnnounceFilePath)).split("-");
            roleAnnounceMessageChannel = temp[0];
            roleAnnounceMessageId = temp[1];

            temp = Files.readString(Path.of(metricsFilePath)).split("-");
            metricsMessageChannel = temp[0];
            metricsMessageId = temp[1];
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setActivity(Activity.of(Activity.ActivityType.PLAYING, "Message for help!"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        ShardManager sm = builder.build();
        sm.addEventListener(new EventListeners(dotenv, roleAnnounceMessageChannel, roleAnnounceMessageId, metricsMessageChannel, metricsMessageId));

        CommandManager cm = new CommandManager();
        cm.add(new AnnounceCommand());
        cm.add(new EditAnnounceCommand());
        cm.add(new RoleAssignCommand(roleAnnounceFilePath, roleAnnounceMessageChannel, roleAnnounceMessageId));
        cm.add(new MetricsCommand(metricsFilePath, metricsMessageChannel, metricsMessageId));
        cm.add(new DeleteMetricsCommand(metricsMessageChannel, metricsMessageId));

        sm.addEventListener(cm);
    }
}