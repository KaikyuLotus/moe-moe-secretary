#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import json
import sys

from random import choice
from telegram import Bot

mode = sys.argv[1]

token = os.environ["BOT_TOKEN"]
artifact = os.environ["ARTIFACT"]
actor = os.environ["GITHUB_ACTOR"]
commit_message = os.environ["COMMIT_MESSAGE"]
short_commit = os.environ["GITHUB_SHA"][:7]
target_chat_ids = json.loads(os.environ["TARGET_CHAT_IDS"])

# Stickers

success_stickers = [
    "CAACAgQAAxkBAAMSXizBH6EVAcELC6oDWD_TEeXZPsIAAuIBAAK6gRoGPKkaIcuBR1MYBA",
    "CAACAgQAAxkBAAMUXizBRicRjwzyVtNUNWhn0H_mbbAAAhUCAAK6gRoG7cgonAUMHpcYBA",
    "CAACAgIAAxkBAAMWXizBk1KXfmaN1iMaePxXZNYRwDgAAgkfAALgo4IHEHOZU6ZS6-MYBA",
    "CAACAgIAAxkBAAMXXizBo_k5PzSVorOq5vvR3afOy1IAAhIfAALgo4IHhx_wVc2O_C0YBA",
    "CAACAgQAAxkBAAMYXizBuRrzhY5ektJe7vi6BkhQsHMAAhQCAAK6gRoGU6MKcVTDAAFsGAQ"
]

fail_stickers = [
    "CAACAgUAAxkBAAMPXizAmFazxh4eyBrwPV477f9sNVgAAmgAAwM94R-m0c-xo2e6rxgE",
    "CAACAgUAAxkBAAMQXizAwPsfYxht2TY_aT6oITAozIYAAmcAAwM94R_zYjyOZ62F1BgE",
    "CAACAgQAAxkBAAMRXizBEDFfLZWkMWI3hW6wb_tZqdYAAhgCAAK6gRoGOiitY2QfK-IYBA",
    "CAACAgQAAxkBAAMTXizBNM7tAuwB48O3wbr9OVERjW8AAhICAAK6gRoGxqOdW8kSa0IYBA",
    "CAACAgUAAxkBAAMVXizBVrzA9F07fxdUCTEM6-X156sAAlAAA1rTAyifa33NO5J2LxgE"

]

bot = Bot(token)


def deploy_to_telegram():
    sticker = choice(success_stickers)
    print(f"Sending '{artifact}' to the following chat IDs: {target_chat_ids}")
    print(f"Issued by user {actor}")
    print(f"With commit message '{commit_message}'")

    caption = f"*New MMS release*\n\n" \
              f"'{commit_message}'\n\n" \
              f"Issued by `{actor}`\n" \
              f"Short commit: `{short_commit}`"

    for target_chat_id in target_chat_ids:
        bot.send_document(target_chat_id, open(artifact, 'rb'), caption=caption, parse_mode="markdown")
        bot.send_sticker(target_chat_id, sticker)


def broadcast_message(fail_message):
    sticker = choice(fail_stickers)
    print(fail_message)
    for target_chat_id in target_chat_ids:
        bot.send_message(target_chat_id, fail_message, parse_mode="markdown")
        bot.send_sticker(target_chat_id, sticker)
    print("Notifications sent")


def telegram_deploy_failed():
    broadcast_message("Telegram deploy failed, please check the logs.")


def maven_build_failed():
    broadcast_message("Maven build failed, please check the logs.")


def github_deploy_failed():
    broadcast_message("GitHub deploy failed, please check the logs.")


if mode == "github_deploy_failed":
    github_deploy_failed()
elif mode == "maven_build_failed":
    maven_build_failed()
elif mode == "telegram_deploy_failed":
    telegram_deploy_failed()
elif mode == "deploy_to_telegram":
    deploy_to_telegram()
else:
    raise NotImplemented(f"Mode '{mode}' is not implemented.")
