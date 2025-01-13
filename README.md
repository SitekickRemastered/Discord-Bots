# Paul
- Paul

---

# Commands
**GLOBAL SYNTAX:**
> Paul

---
**create_poll** - *Creates a poll.*
>/create_poll #channel [question] [answers] Optional: [mention] [emojis] [duration]
>
> **Note: Answers and Emojis should be separated by commas. (i.e. Option 1, Option 2, Option 3)**

**end_poll** - *Ends a poll using the poll's message ID.*
>/end_poll [message_id]

**depaul** - *Makes Paul ignore a user. Name will be added to a list (bannedUsers.txt).*
> /depaul [user]
>
**enpaul** - *Makes Paul re-watch a user. Name will be removed from a list (bannedUsers.txt).*
> /enpaul [user]
---

# Dependencies

All libraries should be added through Maven

Amazon Corretto 21 (OpenJDK) - https://corretto.aws/downloads/latest/amazon-corretto-21-x64-windows-jdk.msi

# Other Notes:
Make sure the environment variables file is named ".env".

If running from the source, The `.env` file should be placed in the main directory.

If running with the `.jar`, the `.env` file should be placed in the same directory as the `.jar`.

There should be a file named `bannedUsers.txt` also located in same directory.