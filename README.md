# Kablooey
- Grants announcement commands for Kablooey

- Keeps track of and updates live metrics message

---

# Commands
**GLOBAL SYNTAX:**
>/command #channel Optional: [message] [send_as_bot] [mentions] [attachments]

---
**announce** - *Makes an announcement to a specified channel with the pfp and user who sent said announcement*
>/announce #channel [message] Optional: [send_as_bot] [mentions] [attachments]
>
**edit_announcement** - *Edits a message embed.*
>/edit_announcement [messageId] [message] Optional: [attachments]
>
**role_assigner** - *Creates an embed that lets people choose roles. Should only be made once.*
> /role_assigner #channel Optional: [message] Optional: [send_as_bot] [mentions] [attachments]
>
---
**metrics** - *An announcement that should only be made once. Makes an embed message that contains game metrics in a specific channel.*
>/metrics #channel
>
**delete_metrics** - *Deletes the metrics message and any .txt file associated with it*
> /delete_metrics
>

---

# Dependencies
JDA v5.0.0-beta.23 (Included) - https://github.com/discord-jda/JDA

HttpComponents 5.1.3 - https://hc.apache.org/downloads.cgi

Logback Classic 1.5.6 (Should be installed with Maven) - https://logback.qos.ch/download.html

Logback Core 1.5.6 (Should be installed with Maven) - https://logback.qos.ch/download.html

SLF4J API 2.0.1.3 (Should be installed with Maven) - https://www.slf4j.org/download.html

Amazon Corretto 21 (OpenJDK) - https://corretto.aws/downloads/latest/amazon-corretto-21-x64-windows-jdk.msi

# Other Notes:
Make sure the environment variables file is named ".env".

If running from the source, The `.env` file should be placed in the main directory.

If running with the `.jar`, the `.env` file should be placed in the same directory as the `.jar`.

There should be two files named `messageId1.txt` and `messageId2.txt` also located in same directory.