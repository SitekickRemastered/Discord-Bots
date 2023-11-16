# Kablooey
- Grants announcement commands for Kablooey

- Creates & syncs a list of Discord Nitro boosters with Sitekick Remastered

- Keeps track of updates and live metrics message

---

## Commands
**GLOBAL SYNTAX:**
>/command #channel Optional: [message] [send_as_bot] [mentions] [attachments]

---
**announce** - *Makes an announcement to a specified channel with the pfp and user who sent said announcement*
>/announce #channel [message] [send_as_bot] Optional: [mentions] [attachments]
> 
**role_assigner** - *An announcement that should only be made once. Lets people choose roles for messages*
> /role_assigner #channel Optional: [message] [send_as_bot] Optional: [mentions] [attachments]
>
---
**metrics** - *An announcement that should only be made once. Makes an embed message that contains game metrics in a specific channel.*
>/metrics #channel
>
**delete_metrics** - *Deletes the metrics message and any .txt file associated with it*
> /delete_metrics
>

---

## Dependencies
JDA - https://github.com/discord-jda/JDA

JDK - 16.0.2
