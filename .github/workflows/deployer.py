#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import json

from telegram import Bot

token = os.environ["BOT_TOKEN"]
artifact = os.environ["ARTIFACT"]
actor = os.environ["GITHUB_ACTOR"]
message = os.environ["COMMIT_MESSAGE"]
short_commit = os.environ["SHA7"]
target_chat_ids = json.loads(os.environ["TARGET_CHAT_IDS"])

print(f"Sending '{artifact}' to the following chat IDs: {target_chat_ids}")
print(f"Issued by user {actor}")
print(f"With commit message '{message}'")

caption = f"New MMS release:\n\n" \
          f"'{message}'\n\n" \
          f"Issue by `{actor}`\n" \
          f"Short commit: `{short_commit}`"

bot = Bot(token)
for target_chat_id in target_chat_ids:
    bot.send_document(target_chat_id, open(artifact, 'rb'), caption=caption, parse_mode="markdown")
