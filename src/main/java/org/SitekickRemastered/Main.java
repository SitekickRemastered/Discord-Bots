package org.SitekickRemastered;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.SitekickRemastered.commands.*;
import org.SitekickRemastered.listeners.CommandManager;
import org.SitekickRemastered.listeners.EventListeners;

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
        String token = dotenv.get("SITEPIC_TOKEN");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setActivity(Activity.of(Activity.ActivityType.PLAYING, "Message me for avatar!"));

        ShardManager sm = builder.build();
        sm.addEventListener(new EventListeners(dotenv));

        CommandManager cm = new CommandManager();
        cm.add(new MenuCommand());
        cm.add(new HelpCommand());
        cm.add(new ClearCommand());
        cm.add(new ResetCommand());

        cm.add(new FillCommand());
        cm.add(new LinearGradientCommand());
        cm.add(new BilinearGradientCommand());
        cm.add(new BodySectionsCommand());
        cm.add(new EyeSectionsCommand());
        cm.add(new HeterochromiaCommand());
        cm.add(new RotateCommand());
        cm.add(new ZoomCommand());
        cm.add(new TranslateCommand());
        cm.add(new FlipCommand());
        cm.add(new PresetsCommand());

        sm.addEventListener(cm);
    }
}