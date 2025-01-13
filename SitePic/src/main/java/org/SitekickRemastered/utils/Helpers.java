package org.SitekickRemastered.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Helpers {

    public static UserController uc = new UserController();
    public static User currentUser;
    public static Hashtable<String, Message> lastEmbed = new Hashtable<>();
    public static float imageSize = 512.0f;
    public static String[] bodyReactions = {
        "default:768644817378476044", "ytvgreen:768644817881923635", "ytvpurple:768644818208555028",
        "white:768644817907220501", "silver:768644817642586112", "black:768644817617551410",
        "red:768644817604968498", "maroon:768644817700913162", "orange:769023013143838755",
        "yellow:768644818259148820", "olive:768644817587535902", "lime:768644817281744917",
        "green:768644817248059433", "aqua:768644817562632192", "blurple:768644817394728982",
        "teal:768644817671553024", "blue:768644817579540500", "navy:768644817630527558",
        "fuchsia:768644817848238132", "purple:771441164090867752"
    };

    public static Color[] colourArray = {
        new Color(255, 204, 0),
        new Color(104, 194, 8),
        new Color(74, 42, 169),
        new Color(255, 255, 255),
        new Color(192, 192, 192),
        new Color(50, 50, 50),
        new Color(255, 0, 0),
        new Color(128, 0, 0),
        new Color(254, 94, 0),
        new Color(255, 255, 0),
        new Color(128, 128, 0),
        new Color(0, 255, 0),
        new Color(0, 128, 0),
        new Color(0, 255, 255),
        new Color(144, 137, 218),
        new Color(0, 128, 128),
        new Color(0, 0, 255),
        new Color(0, 0, 128),
        new Color(255, 0, 255),
        new Color(128, 0, 128)
    };

    public static java.util.List<LayoutComponent> mainMenuOptions = new ArrayList<>(java.util.List.of(
        ActionRow.of(
            createButton("Body:768627497423339542", "Body Menu"),
            createButton("PurpleEyes:768624726275784704", "Eyes Menu")
        )
    ));

    public static String[] traversalMenuStrings = { "Back", "Main Menu", "Body Menu", "Eyes Menu", "Help" };
    public static java.util.List<LayoutComponent> bodyMenuOptions = new ArrayList<>(Arrays.asList(
        ActionRow.of(
            createButton("default:768644817378476044", "Default"),
            createButton("ytvgreen:768644817881923635", "YTV Green"),
            createButton("ytvpurple:768644818208555028", "YTV Purple"),
            createButton("white:768644817907220501", "White"),
            createButton("silver:768644817642586112", "Silver")
        ),
        ActionRow.of(
            createButton("black:768644817617551410", "Black"),
            createButton("red:768644817604968498", "Red"),
            createButton("maroon:768644817700913162", "Maroon"),
            createButton("orange:769023013143838755", "Orange"),
            createButton("yellow:768644818259148820", "Yellow")
        ),
        ActionRow.of(
            createButton("olive:768644817587535902", "Olive"),
            createButton("lime:768644817281744917", "Lime"),
            createButton("green:768644817248059433", "Green"),
            createButton("aqua:768644817562632192", "Aqua"),
            createButton("blurple:768644817394728982", "Blurple")
        ),
        ActionRow.of(
            createButton("teal:768644817671553024", "Teal"),
            createButton("blue:768644817579540500", "Blue"),
            createButton("navy:768644817630527558", "Navy"),
            createButton("fuchsia:768644817848238132", "Fuchsia"),
            createButton("purple:771441164090867752", "Purple")
        )
    ));
    public static java.util.List<LayoutComponent> eyeMenuOptions = new ArrayList<>(Arrays.asList(
        ActionRow.of(
            createButton("PurpleEyes:768624726275784704", "Purple Eyes"),
            createButton("BlackEyes:768567817476767806", "Black Eyes"),
            createButton("WhiteEyes:768567817913499689", "White Eyes"),
            createButton("OrangeEyes:768567817820700763", "Orange Eyes"),
            createButton("GoldenEyes:768567817841934356", "Golden Eyes")
        ),
        ActionRow.of(
            createButton("ForestEyes:768567817590013953", "Forest Eyes"),
            createButton("GreenEyes:768567817620422758", "Green Eyes"),
            createButton("AquaEyes:768567817615573022", "Aqua Eyes"),
            createButton("BabyBlues:768567817422372895", "Baby Blues"),
            createButton("BlueEyes:768567817821618236", "Blue Eyes")
        ),
        ActionRow.of(
            createButton("VioletEyes:768567818219159573", "Violet Eyes"),
            createButton("PinkEyes:768567818253107290", "Pink Eyes"),
            createButton("RedEyes:768567818248650752", "Red Eyes"),
            createButton("CrimsonEyes:768567817694871623", "Crimson Eyes"),
            createButton("GreedyEyes:768567817976676432", "Greedy Eyes")
        ),
        ActionRow.of(
            createButton("KissyEyes:768567818249437244", "Kissy Eyes"),
            createButton("HypnoEyes:768567818245242880", "Hypno Eyes"),
            createButton("DinoEyes:768567817816506398", "Dino Eyes"),
            createButton("HollowEyes:768567818211164200", "Hollow Eyes"),
            createButton("XEyes:768567662065221662", "X-Eyes")
        )
    ));

    public static java.util.List<LayoutComponent> helpTraversalOptions = new ArrayList<>(List.of(
        ActionRow.of(
            createButton("sipik:1036834657850900530", "Main Menu"),
            createButton("Body:768627497423339542", "Body Menu"),
            createButton("PurpleEyes:768624726275784704", "Eyes Menu")
        )
    ));


    public static net.dv8tion.jda.api.interactions.components.buttons.Button createButton(String id, String label) {
        return Button.primary(id, label).withEmoji(Emoji.fromFormatted("<:" + id + ">"));
    }


    /**
     * Builds and sends the main menu. Allows users to traverse to the body and eyes menu
     *
     * @param e - The interaction event that caused the user to enter the main menu.
     */
    public static void menu(GenericInteractionCreateEvent e) {
        currentUser.setLocation("menu");
        e.getMessageChannel().sendFiles(FileUpload.fromData(new File("logo.png"), "Logo.png")).queue();
        e.getMessageChannel().sendMessage("Welcome to the Profile Picture Builder!\n\nClick an action below to choose the corresponding option!").addComponents(mainMenuOptions).queue();
    }


    /**
     * Builds and sends the body menu.
     *
     * @param e - The Button interaction event that caused the user to enter the body menu.
     */
    public static void body(ButtonInteractionEvent e) throws Exception {
        currentUser.setLocation("body");
        String message = "Select a colour below to change your avatar color.";
        SendPFP.sendImage(e, message, bodyMenuOptions);
    }


    /**
     * Builds and sends the eyes menu.
     *
     * @param e - The Button interaction event that caused the user to enter the eyes menu.
     */
    public static void eyes(ButtonInteractionEvent e) throws Exception {
        currentUser.setLocation("eyes");
        String message = "Select an eye-moji below to change your Sitekick's eyes!";
        SendPFP.sendImage(e, message, eyeMenuOptions);
    }


    /**
     * Builds and sends the help menu while also setting the current user's help message.
     *
     * @param e - A Message event listener. If the user types anything and the help message doesn't exist, this function will execute.
     */
    public static void help(MessageReceivedEvent e) throws ExecutionException, InterruptedException {
        currentUser.setHelpMessage(e.getChannel().sendMessageEmbeds(createHelpMessage().build()).addComponents(helpTraversalOptions).submit().get());
    }


    /**
     * Builds and sends the help menu while also setting the current user's help message.
     *
     * @param e - The Interaction event that sent us to this menu.
     */
    public static void help(GenericInteractionCreateEvent e) throws ExecutionException, InterruptedException {
        currentUser.setHelpMessage(e.getMessageChannel().sendMessageEmbeds(createHelpMessage().build()).addComponents(helpTraversalOptions).submit().get());
    }


    /** Builds the help menu embed. */
    public static EmbedBuilder createHelpMessage() {
        String message = """
             **General Commands:**
             `/help` - Opens this menu. If it already exists, a link to this message will be posted.
             `/menu` - Opens the main menu.
             `/clear` - Clears 100 bot messages. *Note: This might take a while*.
             `/reset` - Resets your profile picture to the default Sitekick.
            
             **Image Editing Commands:**
             `/fill` - Allows you to input an RGB or HEX value for the body or eyes.
             `/linear_gradient` - Set the colour of the body or eyes to a linear gradient.
             `/bilinear_gradient` - Set the colour of the body or eyes to a bilinear gradient.
             `/body_sections` - Allows you to customize each section of the Sitekick's body.
             `/eye_sections` - Allows you to customize each section of the Sitekick's eyes.
             `/heterochromia` - Lets you make a Sitekick with two different colour eyes.
             `/rotate` - Rotates the profile picture.
             `/zoom` - Allows you to zoom into the profile picture.
             `/translate` - Allows you to shift the profile picture.
             `/flip` - Allows you to flip the profile picture... looks weird.
             `/presets` - Lets you choose from a list of presets.
            
             **Presets:**
             `kablooey` - Changes the profile picture to Kablooey.
             `authicer` - Changes the profile picture to Authicer.
             `paul` - Changes the profile picture to Paul.
             `deadkick` - Changes the profile picture to a deadkick.

             **Brief Tutorial:**
             Use the buttons below to navigate and make changes to your own Sitekick avatar.
             After you finish your avatar, you can download it and upload it to Discord.

             [**Discord Avatar Change Tutorial**](https://wiki.sitekickremastered.com/en/Home/Sitekick/Tutorials/SitePic)
            \s""";
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0xE1306C); //Set colour of sidebar
        eb.setDescription(message); //Set up the message
        eb.setTimestamp(new Date().toInstant()); //Set up the date
        return eb;
    }


    /**
     * Function that converts a String array of arguments into a HEX value.
     *
     * @param args - A String array of the user's input
     * @return a Color that will be used for a custom Body or Eye colour.
     */
    public static Color convertHex(String[] args) {

        // If the user typed only 1 argument (i.e. a HEX value or and RGB value all-together).
        if (args.length == 1) {
            if (args[0].matches("^(#|)((?:[A-Fa-f0-9]{3}){1,2})$")) {
                String colourInput = "";

                // Get rid of any # if they're there. It just makes things more annoying.
                if (args[0].startsWith("#"))
                    args[0] = args[0].substring(1);

                // If the user inputted RGB values.
                if (args[0].length() == 3)
                    colourInput = "" + args[0].charAt(0) + args[0].charAt(0) + args[0].charAt(1) + args[0].charAt(1) + args[0].charAt(2) + args[0].charAt(2);

                // If the argument is already a valid hex
                if (args[0].length() == 6)
                    colourInput = args[0];
                return Color.decode("#" + colourInput);
            }
        }
        // If the user input was an RGB string.
        if (args.length == 3) {
            if (args[0].matches("\\d{1,3} \\d{1,3} \\d{1,3}") && Integer.parseInt(args[0]) <= 255 && Integer.parseInt(args[1]) <= 255 && Integer.parseInt(args[2]) <= 255)
                return new Color(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        }

        return null;
    }


    /**
     * Deletes all bot messages that are in the Bot Messages HashMap.
     * Checks if the message isn't the help message first, then deletes it.
     *
     * @param e - The interaction event that cause this function to be launched.
     */
    public static void deleteMessages(GenericInteractionCreateEvent e) {
        for (String mId : currentUser.getBotMessages().keySet()) {
            if (!currentUser.getBotMessages().get(mId).equals(currentUser.getHelpMessage())) {
                e.getMessageChannel().deleteMessageById(mId).queue();
            }
        }
        currentUser.clearBotMessages();
    }
}
