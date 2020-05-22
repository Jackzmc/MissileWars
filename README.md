# MissileWars

Still WIP, mostly complete. Everything should work fine if you use the provided world download, and download the schematics. Will be able to setup a world later, and the file for a premade world will be provided. Any custom maps must be based on the Z-axis (bases face each other on Z).


### Requires:
* WorldEdit
* WorldGuard (possibly not in future)
* Vault _(optional)_ for chat prefix

AsyncWorldEdit can possibly be used, as the resetting process can lag a server, and possibly lag clients out. I've tested it, and it works fine in normal operations (may need to tweak memory settings to allow it work always). Only problem is on resetting, it doesn't wait and reset correctly.

### Features:
* Full MissileWars vanilla replacement
* Uses worldedit to spawn missiles & clear game
* Tracks statistics of players, wins/loses, more
* (SOON) PlaceholderAPI support
* (WIP) Add custom missiles
* Change game settings during game
* (SOON) Custom map support
* Free
* and probably more things I forgot about

# Downloads
#### Item Schematics
Download all the schematics required here:
Plugin should be by default configured to use them.

https://dl.jackz.me/MissileWars/schematics.zip


#### Working World Download
Download the Missile Wars World
(All commandblocks have been removed)

https://dl.jackz.me/MissileWars/WORLD.zip


#### Development Builds 

[![Build Status](https://ci.jackz.me/view/Java/job/MissileWars/badge/icon)](https://ci.jackz.me/view/Java/job/MissileWars/)

Builds can be found at this link:

https://ci.jackz.me/view/Java/job/MissileWars/


# Commands
| Command                      | Permissions                | Description                                               |
|------------------------------|----------------------------|-----------------------------------------------------------|
| /spectate [player]           | missilewars.spectate.others| Switch between spectating and not spectating              |
| /game, /gameinfo             | _none_                     | View game stats / players                                 |
| /stat(s), /wins, /loses      | _none_                     | View stats of others or yourself                          |
| /missilewarsadmin, /mwa      | OP Status                  | The main management command. Use /mwa help for info       |

