# MissileWars

Still WIP, mostly complete. Everything should work fine if you setup the manual regions and have schematic files, but they aren't provided right now. Regions constants you can find in the game package's code. Once done of course this will change. 

You can't change any the configuration unless you recompile right now. Not meant for production, has semi-testing. Maps must be based on the Z-axis, spawnpoints are also hardcoded.

### Requires:
* WorldEdit
* WorldGuard
* Vault _(optional)_

(and the hardcoded region names)

### Features:
* Full MissileWars vanilla replacement
* Uses worldedit to spawn missiles & clear game
* (WIP) Tracks statistics of players, wins/loses, more
* (SOON) PlaceholderAPI support
* (SOON) Add custom missiles
* (SOON) Change game settings during game
* Free
* and probably more things I forgot about

## Commands
| Command                      | Description                                               |
|------------------------------|-----------------------------------------------------------|
| /spectate [player]           |Switch between spectating and not spectating              |
| /game, /gameinfo             |View game stats / players                                 |
| /stat(s), /wins, /loses      |View stats of others or yourself                          |
| /missilewarsadmin, /mwa      |The main management command. Use /mwa help for info       |

## Permissions
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