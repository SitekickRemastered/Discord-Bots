package org.SitekickRemastered.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import net.dv8tion.jda.api.utils.messages.MessagePollData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PollCommand implements CommandInterface {

    @Override
    public String getName() {
        return "create_poll";
    }


    @Override
    public String getDescription() {
        return "Creates a new poll.";
    }


    @Override
    public List<OptionData> getOptions() {
        return Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "The channel the message will appear in.", true).setChannelTypes(ChannelType.TEXT, ChannelType.NEWS, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_NEWS_THREAD, ChannelType.FORUM),
            new OptionData(OptionType.STRING, "question", "What question do you want to ask?", true),
            new OptionData(OptionType.STRING, "answers", "What are the answers for this poll?", true),
            new OptionData(OptionType.MENTIONABLE, "mention", "Which roles would you like to mention?", false),
            new OptionData(OptionType.STRING, "emojis", "What are the emojis you want to associate with each answer?", false),
            new OptionData(OptionType.STRING, "duration", "How long should this poll last?", false)
        );
    }


    @Override
    public DefaultMemberPermissions getPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.VIEW_AUDIT_LOGS);
    }


    @Override
    public void execute(SlashCommandInteractionEvent e) {

        GuildChannelUnion channel = Objects.requireNonNull(e.getOption("channel")).getAsChannel();

        String question = Objects.requireNonNull(e.getOption("question")).getAsString();
        MessagePollBuilder mpd = MessagePollData.builder(question);

        String[] answers = Objects.requireNonNull(e.getOption("answers")).getAsString().split(",");
        String[] emojis = new String[answers.length];
        if (e.getOption("emojis") != null)
            emojis = Objects.requireNonNull(e.getOption("emojis")).getAsString().split(",");

        for (int i = 0; i < answers.length; i++) {
            if (emojis[i] != null)
                mpd.addAnswer(answers[i].trim(), Emoji.fromFormatted(emojis[i].trim()));
            else
                mpd.addAnswer(answers[i].trim());
        }

        String[] duration;
        TimeUnit durUnit = TimeUnit.DAYS;
        int durLength = 1;

        // If duration is specified, parse the input.
        if (e.getOption("duration") != null) {
            duration = Objects.requireNonNull(e.getOption("duration")).getAsString().split(" ");

            if (duration[0] == null || duration[1] == null) {
                e.reply("Duration syntax is incorrect.\nUsage: <Length> <Unit> (ex. 2 hour, 3 days, 1 week)").setEphemeral(true).queue();
                return;
            }

            durLength = Integer.parseInt(duration[0]);

            if (duration[1].toLowerCase().endsWith("s"))
                duration[1] = duration[1].substring(0, duration[1].length() - 1);

            switch (duration[1].toLowerCase()) {
                case "hour":
                    durUnit = TimeUnit.HOURS;
                    break;
                case "day":
                    break;
                case "week":
                    durLength = 7;
                    break;
                default: {
                    e.reply("Poll duration must be at least one hour and at most one week!").setEphemeral(true).queue();
                    return;
                }
            }
        }

        if (durUnit == TimeUnit.DAYS && durLength > 7) {
            e.reply("Poll duration cannot last more than one week!").setEphemeral(true).queue();
            return;
        }

        mpd.setDuration(durLength, durUnit);

        if (e.getOption("mention") != null)
            channel.asGuildMessageChannel().sendMessage(Objects.requireNonNull(e.getOption("mention")).getAsMentionable().getAsMention())
                .setPoll(mpd.build()).queue();
        else
            channel.asGuildMessageChannel().sendMessagePoll(mpd.build()).queue();
        e.reply("Poll was successfully created in [**#" + channel.getName() + "**](<" + channel.getJumpUrl() + ">)!").setEphemeral(true).queue();
    }
}