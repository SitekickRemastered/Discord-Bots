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

All libraries should be added through Maven

Amazon Corretto 21 (OpenJDK) - https://corretto.aws/downloads/latest/amazon-corretto-21-x64-windows-jdk.msi

# Other Notes:
Make sure the environment variables file is named ".env".

If running from the source, The `.env` file should be placed in the main directory.

If running with the `.jar`, the `.env` file should be placed in the same directory as the `.jar`.

There should be two files named `metricsMessageId.txt` and `roleMessageId.txt` also located in same directory.
