# MissileWars

Still WIP, mostly complete. Everything should work fine if you setup the manual regions and have schematic files, but they aren't provided right now. Regions constants you can find in the game package's code. Once done of course this will change. 

You can't change any the configuration unless you recompile right now. Not meant for production, has semi-testing. Maps must be based on the Z-axis, spawnpoints are also hardcoded.

### Requires:
* WorldEdit
* WorldGuard (possibly not in future)
* Vault _(optional)_ for chat prefix

AsyncWorldEdit can possibly be used, as the resetting process can lag a server, and possibly lag clients out. I've tested it, and it works fine in normal operations (may need to tweak memory settings to allow it work always). Only problem is on resetting, it doesn't wait and reset correctly.

(and the hardcoded region names)

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

## Commands
| Command                      | Permissions                | Description                                               |
|------------------------------|----------------------------|-----------------------------------------------------------|
| /spectate [player]           | missilewars.spectate.others| Switch between spectating and not spectating              |
| /game, /gameinfo             | _none_                     | View game stats / players                                 |
| /stat(s), /wins, /loses      | _none_                     | View stats of others or yourself                          |
| /missilewarsadmin, /mwa      | OP Status                  | The main management command. Use /mwa help for info       |

