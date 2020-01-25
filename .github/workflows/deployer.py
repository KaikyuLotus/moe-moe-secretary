#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import json

from telegram import Bot

token = os.environ["BOT_TOKEN"]
target_chat_ids = json.loads(os.environ["TARGET_CHAT_IDS"])
actor = os.environ["GITHUB_ACTOR"]
message = os.environ["COMMIT_MESSAGE"]

print(f"Sending to the following chat IDs: {target_chat_ids}")

bot = Bot(token)
for target_chat_id in target_chat_ids:
    bot.send_document(target_chat_id, open('target/moe-moe-secretary.jar', 'rb'))
