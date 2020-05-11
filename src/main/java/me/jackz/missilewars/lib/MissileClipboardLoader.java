package me.jackz.missilewars.lib;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import me.jackz.missilewars.MissileWars;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MissileClipboardLoader {
    private MissileWars plugin;
    private File[] schematics;

    private Map<String,Clipboard> clips = new HashMap<>();

    public MissileClipboardLoader(MissileWars plugin) {
        this.plugin = plugin;
        loadSchematics();
    }

    public Clipboard getClipboard(String name) {
        if(clips.containsKey(name)) {
            return clips.get(name);
        }else{
            return fetchClipboard(name);
        }
    }

    private void loadSchematics() {
        File directory = new File(plugin.getDataFolder() + File.separator + "schematics");
        directory.mkdirs();
        schematics = directory.listFiles();

        loadList("guardian","lightning","shieldbuster","tomahawk","juggernaut","shield");

    }

    private void loadList(String... args) {
        for (String schematic : args) {
            String greenName = "Green-" + schematic;
            String redName = "Red-" + schematic;
            clips.put(greenName, fetchClipboard(greenName));
            clips.put(redName, fetchClipboard(redName));
        }
    }


    private Clipboard fetchClipboard(String name) {
        //name can be 'Green-guardian' -> try Green-guardian.schem and Green-guardian.schematic
        for (File schematic : schematics) {
            if(schematic.getName().equalsIgnoreCase(name + ".schem")) {
                return getClipboard(schematic);
            }else if(schematic.getName().equalsIgnoreCase(name + ".schematic")) {
                return getClipboard(schematic);
            }
        }
        return null;
    }
    private Clipboard getClipboard(File schematic) {
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            return reader.read();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load schematic file " + schematic.getName());
            return null;
        }
    }

}
