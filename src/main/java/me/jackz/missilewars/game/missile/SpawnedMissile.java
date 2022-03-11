package me.jackz.missilewars.game.missile;

import com.sk89q.worldedit.math.BlockVector3;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GameManager;
import me.jackz.missilewars.game.GamePlayers;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.*;

public class SpawnedMissile {
    private Map<BlockVector3, Material> blocks = new HashMap<>();
    private int width;
    private int height;
    private final GamePlayers.MWTeam team;
    private final Player creator;

    public SpawnedMissile(List<Block> incoming, GamePlayers.MWTeam team, Player creator) {
        for(Block block: incoming) {
            BlockVector3 vec = BlockVector3.at(block.getX(), block.getY(), block.getZ());
            blocks.put(vec, block.getType());
        }
        this.team = team;
        this.creator = creator;
    }

    public boolean isActive() {
        boolean result = false;
        for(BlockVector3 vec : blocks.keySet()) {
            Material material = blocks.get(vec);
            if(material == Material.TNT) {
                if(GameManager.getWorld().getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()).getType() == material) {
                    result = true;
                } else {
                    blocks.remove(vec);
                }
            }
        }
        return result;
    }

    public void updateBlock(BlockVector3 from, BlockVector3 to) {
        Material material = blocks.get(from);
       if(material != null) {
           blocks.remove(from);
           blocks.put(to, material);
       }
    }

    public void deleteBlock(BlockVector3 pos) {
        blocks.remove(pos);
    }

    public GamePlayers.MWTeam getTeam() {
        return team;
    }

    public Player getCreator() {
        return creator;
    }


}
