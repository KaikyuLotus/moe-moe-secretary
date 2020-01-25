#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import json
import sys

from telegram import Bot

mode = sys.argv[1]

token = os.environ["BOT_TOKEN"]
artifact = os.environ["ARTIFACT"]
actor = os.environ["GITHUB_ACTOR"]
commit_message = os.environ["COMMIT_MESSAGE"]
short_commit = os.environ["GITHUB_SHA"][:7]
target_chat_ids = json.loads(os.environ["TARGET_CHAT_IDS"])

bot = Bot(token)


def deploy_to_telegram():
    time_taken = os.environ["BUILD_TIME_TAKEN"]
    print(f"Sending '{artifact}' to the following chat IDs: {target_chat_ids}")
    print(f"Issued by user {actor}")
    print(f"With commit message '{commit_message}'")
    print(f"Maven time taken: {time_taken}")

    y = 1 / int("0")

    caption = f"*New MMS release*\n\n" \
              f"'{commit_message}'\n\n" \
              f"Issued by `{actor}`\n" \
              f"Short commit: `{short_commit}`"

    for target_chat_id in target_chat_ids:
        bot.send_document(target_chat_id, open(artifact, 'rb'), caption=caption, parse_mode="markdown")


def broadcast_message(fail_message):
    print(fail_message)
    for target_chat_id in target_chat_ids:
        bot.send_message(target_chat_id, fail_message, parse_mode="markdown")
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
