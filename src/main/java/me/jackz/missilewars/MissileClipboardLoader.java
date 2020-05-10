package me.jackz.missilewars;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MissileClipboardLoader {
    private MissileWars plugin;
    private File[] schematics;

    private Map<String,Clipboard> clips = new HashMap<>();

    public MissileClipboardLoader(MissileWars plugin) {
        this.plugin = plugin;
        loadSchematics();
    }

    public Clipboard getClipboard(String name) {
        return clips.get(name);
    }

    private void loadSchematics() {
        File directory = new File(plugin.getDataFolder() + File.separator + "schematics");
        directory.mkdirs();
        schematics = directory.listFiles();

        loadList("guardian","lightning","shieldbuster","tomohawk","juggernaut","shield");

    }

    private void loadList(String... args) {
        for (String schematic : args) {
            String greenName = "Green-" + schematic;
            String redName = "Red-" + schematic;
            clips.put(greenName,getSchematic(greenName));
            clips.put(redName,getSchematic(redName));
        }
    }


    private Clipboard getSchematic(String name) {
        //name can be 'Green-guardian' -> try Green-guardian.schem and Green-guardian.schematic
        for (File schematic : schematics) {
            if(schematic.getName().equalsIgnoreCase(name + ".schem")) {
                return getClipboardInternal(schematic);
            }else if(schematic.getName().equalsIgnoreCase(name + ".schematic")) {
                return getClipboardInternal(schematic);
            }
        }
        return null;
    }
    private Clipboard getClipboardInternal(File schematic) {
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            return reader.read();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load schematic file " + schematic.getName());
            return null;
        }
    }

}
