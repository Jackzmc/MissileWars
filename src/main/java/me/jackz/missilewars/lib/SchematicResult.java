package me.jackz.missilewars.lib;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SchematicResult {
    private final List<Block> blocks;
    private final Map<Material, ArrayList<Block>> tileEntities;

    SchematicResult(List<Block> blocks, Map<Material, ArrayList<Block>> spawnedTiles) {
        this.blocks = blocks;
        this.tileEntities = spawnedTiles;
    }

    public ArrayList<Block> getTileEntities(Material material) {
        return tileEntities.get(material);
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    public List<Block> getBlocksByType(Material material) {
        return blocks.stream().filter(block -> {
            return block.getType() == material;
        }).collect(Collectors.toList());
    }

}
