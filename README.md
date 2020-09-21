<p align="center">
  <img src="https://i.imgur.com/YM4Xd4o.png"/>
</p>

![CI Status](https://github.com/KaikyuDev/moe-moe-secretary/workflows/Java%20CI/badge.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/c2a2976174b94b11ae748978a211c9b2)](https://www.codacy.com/manual/kaikyu.lotus/moe-moe-secretary?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=KaikyuLotus/moe-moe-secretary&amp;utm_campaign=Badge_Grade)

#### Moe Moe Secretary - Your waifu (almost) always with you!

### Index
- [Installing](#installing)
- [Usage](#usage)
- [Configuration](#configuration)
- [Adapters](#adapters)
- [Help](#help)
- [Contributing](#contributing)
- [Screenshots](#screenshots)

### Installing
In order to install Moe Moe Secretary, please download the latest jar from the [Github Releases](TODO)\
You'll need a JRE (1.8+) installed, you can get it [here](https://adoptopenjdk.net/installation.html#x64_win-jre).\
Place the JAR file in a secure place, better if it's in a folder
 
### Usage
All the key commands must be used after clicking the waifu in order to obtains window focus.
- Double click the jar file to start Moe Moe Secretary,\
once started you'll find a waifu floating on your desktop (Ptilopsis from Arknights)
- Move her around by dragging her
- Click her to trigger "on click" dialogs
- Close her by clicking the mouse wheel on her\
(if you don't have a mouse wheel, ALT F4 combination will do the job)
- If the adapter supports skin, you can switch them pressing J and K keys
- By default the waifu should be on always-on-top mode, to toggle it press the T key
- Flip the waifu by pressing S key
- Toggle floating effect by pressing the F key

### Configuration
In the JAR file's folder, after starting MMS at least once, there will be a folder named "config",\
inside it you'll find a file named **config.properties**.

**config.properties** file contains **all** the settings for your secretary.

**TIP**: Saving this file will apply the changes to the secretary on the fly.

Check the next table to see all the possible Adapters and adapter-configurations.\
All the other settings are self-explanatory.

### Adapters
Adapters are used to access different waifus on the internet.\
Moe Moe Secretary uses public wikis data to download images and dialogs, where available.\
If you think that a wiki does not like this behaviour, please open an issue.

The following table shows the adapter names to be used in the .properties file and their relative detailed chapter.

| Adapter Name            | Chapter                                             | 
| :---:                   | :---:                                               |
| AzurLane                | [Azur Lane Chapter](#azur-lane-adapter)             |
| Arknights               | [Arknights Chapter](#arknights-adapter)             |
| SinoAlice               | [SinoAlice Chapter](#sinoalice-adapter)             |
| SIFIdol                 | [SIFIdol Chapter](#school-idol-festival-adapter)    |
| GirlsFrontline          | [Girls Frontline Chapter](#girls-frontline-adapter) |
| MirageMemorial          | [Mirage Memorial Chapter](#mirage-memorial-adapter) |
| Github                  | [Github Chapter](#github-adapter)                   |
| **MMS Official Github** | [MMS Github Chapter](#mms-github-adapter)           |
| File                    | [File Chapter](#file-adapter)                       |

##### Features table
| Feature | Azur Lane | Arknights | SINoALICE | SIFIdol | GirlsFrontline | MirageMemorial | 
| :---    | :---:     | :---:     | :---:     | :---:   | :---:          | :---:          |
| Dialogs |   ✅      |   ✅      |   ✅       |   ✅    |   ✅           |   ✅            |
| Voices  |   ✅      |   ✅      |   ❌       |   ✅    |   ✅           |   ❌            |
| Skins   |   ✅      |   ✅      |   ❌       |   ✅    |   ✅           |   ❌            |

#### Azur Lane Adapter
This adapter takes the data from https://azurlane.koumakan.jp \
Ship names are the same as in-game names, if you can't really find one take a look [here](https://azurlane.koumakan.jp/List_of_Ships).

#### Arknights Adapter
This adapter takes the data from https://github.com/Aceship/AN-EN-Tags \
Operator names are the same as in-game names, if you can't really find one, search for it [here](https://aceship.github.io/AN-EN-Tags/akhrchars.html?opname=Ptilopsis)

#### SinoAlice Adapter
This adapter takes the data from https://sinoalice.game-db.tw \
Characters names are the same as in-game names, if you can't really find one take a look [here](https://sinoalice.game-db.tw/characters/).

#### School Idol Festival Adapter
This adapter takes card images from https://schoolido.lu and quotes from https://decaf.kouhi.me/lovelive/index.php \
This adapter requires the card ID as waifu.name in the config.properties, be sure to match the card ID from [here](https://schoolido.lu/cards/).

#### Girls Frontline Adapter
This adapter takes the data from https://en.gfwiki.com \
Weapon names are the same as in-game names, if you can't really find one take a look [here](https://en.gfwiki.com/wiki/T-Doll_Index)

#### Mirage Memorial Adapter
This adapter takes the data from https://miragememorialglobal.fandom.com/wiki \
Servant names are the same as in-game names, if you can't really find one take a look [here](https://miragememorialglobal.fandom.com/wiki/Special:Images):\
find your servant, click on the image and look at the URL, it'll end with "?file=Aristotle.png"\
use the string after = (without .png) (in this case Aristotle)

#### Github Adapter
Github adapter is the best one, but it has a cost: waifus must be implemented manually first.\
Moe Moe Secretary has an official repository for custom waifus (mostly VTubers), check the next chapter.

Github adapter requires some additional parameters in the config.properties:
```properties
adapter=Github
waifu.name=path/Name
adapter.file.format=format
github.repo=Username/repo
github.branch=branch
```

Those are the required parameters in order to use the github adapter,\
check the next chapter to see some example values.

##### Extra features
- You can create your own waifus and host them on Github.

This adapter supports all MMS features!

#### MMS Github Adapter
Moe Moe Secretary has its own [Github waifu repository](https://github.com/KaikyuLotus/moe-moe-secretary-waifus).

In order to use it set the following values in your config.properties:
```properties
adapter=Github
waifu.name=VTuber/Hololive/Calliope
adapter.file.format=YAML
github.repo=KaikyuLotus/moe-moe-secretary-waifus
github.branch=master
```

With those settings [Calliope-sama](https://twitter.com/moricalliope) should pop-up on your desktop!

Want to add more waifus?\
Create an issue to add them or fork that repo and add them yourself, I'll accept PRs.

#### File Adapter
Please take a look at this link:\
https://telegra.ph/Moe-Moe-Secretary-File-Adapter-Configuration-01-12 \
It may be out of date, if so please open an issue or [contact me on Telegram](https://t.me/KaikyuLotus).

### Help
If MMS crashes with a certain adapter or character you can open an issue or join the [official Telegram group](https://t.me/joinchat/HQxrAhRw3k8Zznib57V5Uw)!\
We also have a CI bot, so you can update your MMS version directly from Telegram!\
Also check the FAQs

### FAQ
- **Q**: I don't want my waifu to start on my PC startup, how to disable it?\
**A**: Set `waifu.autoStartupEnabled` to `false` in your config.properties
- **Q**: MMS requires internet access to work?\
**A**: Yes, it does, if you don't want it you could use the file adapter.
- **Q**: Can you add <**character name**>?\
**A**: Yes, most probably, please open an issue with some details of the requested character.

### Contributing
Details on contributions will be added later.

### Screenshots

___

![Ptilopsis from Arknights helping me with docs](https://i.imgur.com/06BgCqI.jpg)

![Fubuki and her conifguration](https://i.imgur.com/0iVkBUC.jpg)
