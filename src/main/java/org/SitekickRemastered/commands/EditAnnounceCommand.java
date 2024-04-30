package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EditAnnounceCommand implements CommandInterface {

    @Override
    public String getName() {
        return "edit_announcement";
    }


    @Override
    public String getDescription() {
        return "Edits an announcement using the message's ID";
    }


    @Override
    public List<OptionData> getOptions() {
        return Arrays.asList(
            new OptionData(OptionType.STRING, "message_id", "The ID of the message to edit.", true),
            new OptionData(OptionType.STRING, "message", "The message you want to send.", false),
            new OptionData(OptionType.ATTACHMENT, "attachment", "The image / gif in the embed.", false)
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.VIEW_AUDIT_LOGS);
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {
        String channelId = "";

        String messageId = (e.getOption("message_id") != null) ? Objects.requireNonNull(e.getOption("message_id")).getAsString() : null;
        String message = (e.getOption("message") != null) ? Objects.requireNonNull(e.getOption("message")).getAsString() : null;
        Message.Attachment attachment = (e.getOption("attachment") == null) ? Objects.requireNonNull(e.getOption("attachment")).getAsAttachment() : null;

        // Parse the message ID. Normally, IDs are in the form CHANNEL-MESSAGE, so we have to take out the CHANNEL part
        String[] tempArray = messageId.split("-");
        if (tempArray.length > 1) {
            channelId = tempArray[0];
            messageId = tempArray[1];
        }

        // Get the message and check if it exists
        Message m = Objects.requireNonNull(Objects.requireNonNull(e.getGuild()).getTextChannelById(channelId)).retrieveMessageById(messageId).complete();
        MessageEmbed me = m.getEmbeds().getFirst();
        if (me == null) {
            e.reply("Failed to find the message. Please make sure you got the correct ID.").setEphemeral(true).queue();
            return;
        }

        // If there was no message, alert the user and return.
        if (message == null && attachment == null) {
            e.reply("You need to add a message or attachment!").setEphemeral(true).queue();
            return;
        }

        // Set the new embed to all the stuff that we're not going to edit.
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(me.getColor());

        if (me.getAuthor() != null) {
            eb.setAuthor(me.getAuthor().getName(), me.getAuthor().getUrl(), me.getAuthor().getIconUrl());
        }

        // Set the description in the embed to the message
        if (message != null) {
            message = message.replace("\\n", "\n");
            eb.setDescription(message);
        }

        //Set up the date
        eb.setTimestamp(new Date().toInstant());

        //If the user added an attachment
        if (attachment != null) {
            eb.setImage(attachment.getUrl());
        }

        // Edit the message by ID
        Objects.requireNonNull(e.getGuild().getTextChannelById(channelId)).editMessageEmbedsById(messageId, eb.build()).queue();

        eb.clear(); //Clear the message at the end for next time
        e.deferReply().queue(m2 -> m2.deleteOriginal().queue());
    }
}
