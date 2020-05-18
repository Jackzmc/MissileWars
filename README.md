# MissileWars

Still WIP, mostly complete. Everything should work fine if you setup the manual regions and have schematic files, but they aren't provided right now. Regions constants you can find in the game package's code. Once done of course this will change. 

You can't change any the configuration unless you recompile right now. Not meant for production, has semi-testing. Maps must be based on the Z-axis, spawnpoints are also hardcoded.

### Requires:
* WorldEdit
* WorldGuard

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
| Command                      | Permissions                | Description                                               |
|------------------------------|----------------------------|-----------------------------------------------------------|
| /spectate [player]           | missilewars.spectate.others| Switch between spectating and not spectating              |
| /game, /gameinfo             | _none_                     | View game stats / players                                 |
| /stat(s), /wins, /loses      | _none_                     | View stats of others or yourself                          |
| /missilewarsadmin, /mwa      | OP Status                  | The main management command. Use /mwa help for info       |

