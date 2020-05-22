# MissileWars

Still WIP, mostly complete. Everything should work fine if you use the provided world download, and download the schematics. Will be able to setup a world later, and the file for a premade world will be provided. Any custom maps must be based on the Z-axis (bases face each other on Z).

You can't change any the configuration unless you recompile right now. Not meant for production, has semi-testing. Maps must be based on the Z-axis, spawnpoints are also hardcoded.

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
| Command                      | Description                                               |
|------------------------------|-----------------------------------------------------------|
| /spectate [player]           | Switch between spectating and not spectating              |
| /game, /gameinfo             | View game stats / players                                 |
| /stat(s), /wins, /loses      | View stats of others or yourself                          |
| /missilewarsadmin, /mwa      | The main management command. Use /mwa help for info       |

# Permissions
* missilewars.spectate - Permission to switch between spectator mode and adventure mode
* missilewars.spectate.others - Permission to switch another player
* missilewars.stats - Permission to view stats of self, and session stats
* missilewars.stats.other - Permission to view stats of another 
* missilewars.stats.global - Permission to view global statistics
* missilewars.admin.give - Allows access to the `/mwa give` command
* missilewars.admin.choose - Allows access to the `/mwa choose` command
* missilewars.admin.game - Allows access to the `/mwa game` command
* missilewars.admin.game.start - Allows force starting the game
* missilewars.admin.game.stop - Allows resetting and stopping the game
* missilewars.admin.config - Allows access to the `/mwa config` command
* missilewars.admin.config.[configID] - Allows changing settings in game
* missilewars.admin.config.save - Allows saving changed settings to disk
* missilewars.admin.reload - Allows access to reloading all yaml files