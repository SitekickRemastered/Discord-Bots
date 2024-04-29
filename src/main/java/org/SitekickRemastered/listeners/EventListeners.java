package org.SitekickRemastered.listeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EventListeners extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e) {

        // If the user sent a private message to Authicer, we send them some messages telling them how to use Authicer
        if (e.getChannelType() == ChannelType.PRIVATE && !e.getAuthor().isBot()){
            e.getChannel().sendMessage("Message the https://discord.com/channels/603580736250970144/999864676982722560 channel to verify your account.").queue(message -> message.delete().queueAfter(60, TimeUnit.SECONDS));
            e.getChannel().sendMessage("This message will self destruct in one minute.").queue(message -> message.delete().queueAfter(60, TimeUnit.SECONDS));
        }

        //If the user posted in #link with something that is not /link, delete the message
        if (e.getChannel().toString().contains("link") && !Objects.requireNonNull(e.getMember()).getUser().isBot()) {
            e.getMessage().delete().queue();
        }
    }
}