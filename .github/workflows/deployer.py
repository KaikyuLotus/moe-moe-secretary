#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import json

from telegram import Bot

token = os.environ["bot_token"]
target_chat_ids = json.loads(os.environ["INPUT_chat_ids"])

print(f"Sending to the following chat IDs: {target_chat_ids}")

bot = Bot(token)
for target_chat_id in target_chat_ids:
    bot.send_document(target_chat_id, open('target/moe-moe-secretary.jar', 'rb'))
