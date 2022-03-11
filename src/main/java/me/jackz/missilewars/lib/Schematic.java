package me.jackz.missilewars.lib;

import com.sk89q.jnbt.*;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Schematic {
    public final short width;
    public final short length;
    public final short height;
    public List<Tag> tileEntities;
    public byte[] blockdata;
    public Map<String, Tag> palette;
    public Integer version;

    public Schematic(short width, short length, short height, List<Tag> tileEntities, byte[] blockdata, Map<String, Tag> palette, int version) {

        this.width = width;
        this.length = length;
        this.height = height;
        this.tileEntities = tileEntities;
        this.blockdata = blockdata;
        this.palette = palette;
        this.version = version;
    }

    public static Schematic loadSchematic(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        NBTInputStream nbtStream = new NBTInputStream(stream);

        NamedTag schematicTag = nbtStream.readNamedTag();
        stream.close();
        nbtStream.close();
        Map<String, Tag> schematic = (Map<String, Tag>) schematicTag.getTag().getValue();

        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
        int version = getChildTag(schematic, "Version", IntTag.class).getValue();
        Map<String, Tag> palette = getChildTag(schematic, "Palette", CompoundTag.class).getValue();
        byte[] blockdata = getChildTag(schematic, "BlockData", ByteArrayTag.class).getValue();
        if (version == 1) {
            List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class).getValue();
            return new Schematic(width, length, height, tileEntities, blockdata, palette, version);
        } else if (version == 2) {
            List<Tag> BlockEntities = getChildTag(schematic, "BlockEntities", ListTag.class).getValue();
            return new Schematic(width, length, height, BlockEntities, blockdata, palette, version);
        } else {
            return new Schematic(width, length, height, Collections.emptyList(), blockdata, palette, version);
        }
    }

    public static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws
            IllegalArgumentException {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }

    public void paste(Location location, CompletableFuture<SchematicResult> completableFuture) {
        short length = this.length;
        short width = this.width;
        short height = this.height;
        location.subtract(width / 2.00, height / 2.00, length / 2.00); // Centers the schematic
        ArrayList<Block> blocks = new ArrayList<>();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    Block block = new Location(location.getWorld(), x + location.getX(), y + location.getY(), z + location.getZ()).getBlock();
                    for (String blockData : this.palette.keySet()) {
                        if (palette.containsKey(blockData)) {
                            int i = getChildTag(this.palette, blockData, IntTag.class).getValue();
                            if (this.blockdata[index] == i) {
                                // Is a valid block
                                block.setBlockData(Bukkit.createBlockData(blockData), false);
                                blocks.add(block);
                            }
                        }
                    }
                }
            }
        }

        Map<Material, ArrayList<Block>> spawnedTiles = new HashMap<>();

        for (Tag tag : this.tileEntities) {
            if (!(tag instanceof CompoundTag))
                continue;
            CompoundTag t = (CompoundTag) tag;
            Map<String, Tag> tags = t.getValue();

            int[] pos = getChildTag(tags, "Pos", IntArrayTag.class).getValue();

            int x = pos[0];
            int y = pos[1];
            int z = pos[2];

            Block block = new Location(location.getWorld(), x + location.getX(), y + location.getY(), z + location.getZ()).getBlock();
            Material material = Material.matchMaterial(getChildTag(tags, "Id", StringTag.class).getValue());
            if(material != null) {
                if(!spawnedTiles.containsKey(material)) {
                    spawnedTiles.put(material, new ArrayList<>());
                }
                spawnedTiles.get(material).add(block);
            }
        }

        SchematicResult result = new SchematicResult(blocks, spawnedTiles);

        completableFuture.complete(result);
    }
}
