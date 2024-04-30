package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AnnounceCommand implements CommandInterface {

    @Override
    public String getName() {
        return "announce";
    }


    @Override
    public String getDescription() {
        return "Sends an announcement to a specified channel.";
    }


    @Override
    public List<OptionData> getOptions() {
        return Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "The channel the message will appear in.", true).setChannelTypes(ChannelType.TEXT, ChannelType.NEWS, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_NEWS_THREAD, ChannelType.FORUM),
            new OptionData(OptionType.STRING, "message", "The message you want to send.", false),
            new OptionData(OptionType.BOOLEAN, "send_as_bot", "Do you want the message to be from you or the bot?", false),
            new OptionData(OptionType.MENTIONABLE, "mention", "The group you want to notify with the message.", false),
            new OptionData(OptionType.ATTACHMENT, "attachment", "The image / gif in the embed.", false)
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.VIEW_AUDIT_LOGS);
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {

        GuildChannelUnion channel = Objects.requireNonNull(e.getOption("channel")).getAsChannel();
        String message = (e.getOption("message") != null) ? Objects.requireNonNull(e.getOption("message")).getAsString() : null;
        boolean sendAsBot = e.getOption("send_as_bot") == null || Objects.requireNonNull(e.getOption("send_as_bot")).getAsBoolean();
        IMentionable mention = (e.getOption("mention") == null) ? Objects.requireNonNull(e.getOption("mention")).getAsMentionable() : null;
        Message.Attachment attachment = (e.getOption("attachment") == null) ? Objects.requireNonNull(e.getOption("attachment")).getAsAttachment() : null;

        // If there was no message, alert the user and return.
        if (message == null && attachment == null) {
            e.reply("You need to add a message or attachment!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();

        //Set colour of sidebar (Kablooey's Blue)
        eb.setColor(0x007AFE);

        //If the user does not want to send it as a bot, then it will display their name and pfp
        if (!sendAsBot) {
            eb.setAuthor(Objects.requireNonNull(e.getMember()).getUser().getEffectiveName(), null, e.getMember().getUser().getAvatarUrl());
            eb.setColor(0x660199); //Set the colour to purple
        }

        // Set the description in the embed to the message
        if (message != null) {
            message = message.replace("\\n", "\n");
            eb.setDescription(message);
        }

        //Set up the date
        eb.setTimestamp(new Date().toInstant());

        //If the user typed a specific role, mention that role before sending the message
        if (mention != null)
            channel.asGuildMessageChannel().sendMessage(mention.getAsMention()).queue();

        //If the user added an attachment
        if (attachment != null)
            eb.setImage(attachment.getUrl());

        channel.asGuildMessageChannel().sendMessageEmbeds(eb.build()).queue();
        eb.clear(); //Clear the message at the end for next time
        e.deferReply().queue(m -> m.deleteOriginal().queue());
    }
}
