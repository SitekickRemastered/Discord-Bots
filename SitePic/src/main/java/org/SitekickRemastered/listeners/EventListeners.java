package org.SitekickRemastered.listeners;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.SitekickRemastered.commands.CommandInterface;
import org.SitekickRemastered.utils.Helpers;
import org.SitekickRemastered.utils.SendPFP;
import org.SitekickRemastered.utils.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventListeners extends ListenerAdapter {

    String statusURL;
    ScheduledExecutorService pingThread = Executors.newSingleThreadScheduledExecutor();


    public EventListeners(Dotenv dotenv) {
        statusURL = dotenv.get("SITEPIC_PING_LINK");
    }


    public void onReady(@NotNull ReadyEvent e) {

        // Sets a thread to run every minute to ping SitePic's status URL. If it fails, another bot alerts us, so we can fix it.
        pingThread.scheduleAtFixedRate(() -> {
            try {
                URLConnection conn = URI.create(statusURL).toURL().openConnection();
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                InputStream response = conn.getInputStream();
                response.close();
            }
            catch (IOException ex) {
                System.err.println("ERROR: Failed to send status ping to SitePic.");
            }
        }, 0, 60, TimeUnit.SECONDS);
    }


    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        setUser(e.getUser().getName());
    }


    /**
     * The bot seems to change message's IDs if the message is edited.
     * This function makes sure that a message is included if it's edited.
     */
    public void onMessageUpdate(MessageUpdateEvent e) {
        setUser(e.getChannel().getName());
        if (e.getAuthor().isBot() && !e.getMessage().isEphemeral() && !Helpers.currentUser.messageExists(e.getMessage()))
            Helpers.currentUser.addBotMessage(e.getMessage());
    }


    /**
     * This is the Message Listener. It logs the bot's message, so they can be deleted later.
     * If you send anything that isn't a command, it will give you a help message (assuming it doesn't already exist).
     * Type menu to go to the menu function, Type back in the body
     *
     * @param e - Message Event Listener.
     */
    public void onMessageReceived(MessageReceivedEvent e) {

        if (e.getChannelType() == ChannelType.PRIVATE) {

            setUser(e.getChannel().getName());

            // Add the bot message in case the user wants to delete it later.
            if (e.getAuthor().isBot() && !e.getMessage().isEphemeral() && !Helpers.currentUser.messageExists(e.getMessage())) {
                Helpers.currentUser.addBotMessage(e.getMessage());
                return;
            }

            if (Helpers.currentUser.getHelpMessage() != null) {
                e.getChannel().sendMessage("Help Message already exists! Link: " + Helpers.currentUser.getHelpMessage().getJumpUrl()).queue();
                return;
            }

            //If the help message doesn't exist then delete the bot messages before and send the help message
            if (!Helpers.currentUser.getBotMessages().isEmpty()) {
                for (String mId : Helpers.currentUser.getBotMessages().keySet()) {
                    e.getChannel().deleteMessageById(mId).queue();
                }
                Helpers.currentUser.clearBotMessages();
            }
            try {
                Helpers.help(e);
            }
            catch (ExecutionException | InterruptedException executionException) {
                executionException.printStackTrace();
            }
        }
    }


    /**
     * This function sends the user to wherever they want to go.
     *
     * @param e - The Button Event Listener.
     */
    public void onButtonInteraction(@NotNull ButtonInteractionEvent e) {
        try {
            boolean editEmbed = false;
            String reactionName = e.getButton().getEmoji().getName();
            String fullReaction = e.getButton().getEmoji().getAsReactionCode();

            setUser(e.getUser().getName());

            // If the button we clicked is a traversal option, we can only go to the Main Menu, Body Menu, Eyes Menu, or Help Menu
            if (Arrays.asList(Helpers.traversalMenuStrings).contains(e.getButton().getLabel())) {
                Helpers.deleteMessages(e);

                if (e.getButton().getLabel().equals("Back") || e.getButton().getLabel().equals("Main Menu")) {
                    Helpers.menu(e);
                    e.deferReply().setEphemeral(true).queue(m -> m.deleteOriginal().queue());
                }

                if (e.getButton().getLabel().equals("Body Menu"))
                    Helpers.body(e);

                if (e.getButton().getLabel().equals("Eyes Menu"))
                    Helpers.eyes(e);

                if (e.getButton().getLabel().equals("Help"))
                    Helpers.help(e);
            }
            // Otherwise the button was a preset, so we have to set the BodyColour or EyePic depending on the current location.
            else {
                if (Helpers.currentUser.getLocation().equals("body"))
                    Helpers.currentUser.setBodyColour(Helpers.colourArray[Arrays.asList(Helpers.bodyReactions).indexOf(fullReaction)]);

                if (Helpers.currentUser.getLocation().equals("eyes"))
                    Helpers.currentUser.setEyePic(reactionName + ".png");
                editEmbed = true;
            }
            if (editEmbed)
                SendPFP.editEmbed(e);

            // If the button we clicked is attached to the help message,
            // delete the buttons on the help menu so the user doesn't break anything.
            if (e.getMessage().equals(Helpers.currentUser.getHelpMessage()))
                e.getMessage().editMessageEmbeds(e.getMessage().getEmbeds()).setComponents().queue();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setUser(String author) {
        //Save the user if they don't exist in the userList
        if (!Helpers.uc.userExists(Helpers.uc.getUser(author))) {
            User user = new User(author);
            Helpers.uc.addUser(user);
        }
        //Set the current user
        Helpers.currentUser = Helpers.uc.getUser(author);
    }
}