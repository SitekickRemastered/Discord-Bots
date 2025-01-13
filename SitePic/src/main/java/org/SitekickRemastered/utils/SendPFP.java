package org.SitekickRemastered.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

public class SendPFP {

    /**
     * This function builds the starting embed that we will continually edit throughout the program.
     * It first initialises the image and the traversal options for the embed, then it creates the embed
     * After creating the embed, we have to edit the description so the download link is included.
     *
     * @param e       - The button interaction event that sent us here.
     * @param message - The title of the embed. Is different depending on the menu the user is in.
     * @param buttons - The Buttons that will be attached to the embed depending on the menu the user is in.
     */
    public static void sendImage(ButtonInteractionEvent e, String message, List<LayoutComponent> buttons) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(CreatePFP.buildImage(), "png", bytes);

        List<LayoutComponent> traversalOptions = List.of(
            ActionRow.of(
                Button.of(ButtonStyle.DANGER, "Back", "Back", Emoji.fromUnicode("⬅️")),
                Helpers.currentUser.getLocation().equals("body") ? Helpers.createButton("PurpleEyes:768624726275784704", "Eyes Menu") : Helpers.createButton("Body:768627497423339542", "Body Menu"),
                Button.of(Helpers.currentUser.getHelpMessage() == null ? ButtonStyle.SUCCESS : ButtonStyle.LINK, Helpers.currentUser.getHelpMessage() == null ? "Help" : Helpers.currentUser.getHelpMessage().getJumpUrl(), "Help", Emoji.fromUnicode("❓"))
            )
        );

        EmbedBuilder eb = new EmbedBuilder()
            .setColor(0xE1306C)
            .setTitle(message)
            .setImage("attachment://" + Helpers.currentUser.getName() + ".png")
            .setTimestamp(new Date().toInstant());

        e.getChannel().sendMessageEmbeds(eb.build()).addFiles(FileUpload.fromData(bytes.toByteArray(), Helpers.currentUser.getName() + ".png")).addComponents(buttons).addComponents(traversalOptions).queue(m -> {
            m.editMessageEmbeds(eb.setDescription(getDescription(m.getEmbeds().getFirst().getImage().getUrl())).build()).queue();
            Helpers.lastEmbed.put(e.getChannel().getId(), m);
        });
        e.deferReply().setEphemeral(true).queue(m -> m.deleteOriginal().queue());
        bytes.flush();
        bytes.close();
    }


    /**
     * This function is used to edit the embed with the image so that we don't have to load all the buttons again
     *
     * @param e - The Interaction even that cause this function to occur.
     */
    public static void editEmbed(GenericInteractionCreateEvent e) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(CreatePFP.buildImage(), "png", bytes);

        IReplyCallback callback = (IReplyCallback) e.getInteraction();
        e.getMessageChannel().retrieveMessageById(Helpers.lastEmbed.get(e.getChannel().getId()).getId()).queue(m -> createEmbed(m, bytes));
        callback.deferReply().setEphemeral(true).queue(m -> m.deleteOriginal().queue());
    }


    /**
     * This function is used to create the embed for editEmbed.
     *
     * @param m     - The embed that we will be editing. We need this to take the title and to edit the embed
     * @param bytes - The byte array of the image.
     */
    public static void createEmbed(Message m, ByteArrayOutputStream bytes) {
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(0xE1306C)
            .setTitle(m.getEmbeds().getFirst().getTitle())
            .setImage("attachment://" + Helpers.currentUser.getName() + ".png")
            .setTimestamp(new Date().toInstant());
        m.editMessageEmbeds(eb.build()).setFiles(FileUpload.fromData(bytes.toByteArray(), Helpers.currentUser.getName() + ".png")).queue(m2 -> {
            m2.editMessageEmbeds(eb.setDescription(getDescription(m2.getEmbeds().getFirst().getImage().getUrl())).build()).queue();
        });
    }


    /**
     * A function that builds the description for embeds.
     *
     * @param url - The url of the image, so we can include a download link
     * @return - Returns the string for the description
     */
    public static String getDescription(String url) {
        return """
            Here are some other commands available:
            `/fill`, `/linear_gradient`, `/bilinear_gradient`,
            `/sections`, `/heterochromia`, `/rotate`
            `/zoom`, `/translate`, `/flip`
            
            Finished building your avatar?
            [**DOWNLOAD**](""" + url + ")";
    }
}
