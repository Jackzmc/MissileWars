package me.jackz.missilewars.lib;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import me.jackz.missilewars.MissileWars;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClipboardLoader {
    private static File[] schematics;
    private static Map<String,Clipboard> clips = new HashMap<>();

    public ClipboardLoader(MissileWars plugin) {
        loadSchematics();
    }

    public static Clipboard getClipboard(String name) {
        if(clips.containsKey(name)) {
            return clips.get(name);
        }else{
            return fetchClipboard(name);
        }
    }

    private static void loadSchematics() {
        File directory = new File(MissileWars.getInstance().getDataFolder() + File.separator + "schematics");
        directory.mkdirs();
        schematics = directory.listFiles();

        loadList("guardian","lightning","shieldbuster","tomahawk","juggernaut","shield");

    }

    private static void loadList(String... args) {
        for (String schematic : args) {
            String greenName = "Green-" + schematic;
            String redName = "Red-" + schematic;
            clips.put(greenName, fetchClipboard(greenName));
            clips.put(redName, fetchClipboard(redName));
        }
    }


    private static Clipboard fetchClipboard(String name) {
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
    private static Clipboard getClipboard(File schematic) {
        ClipboardFormat format = ClipboardFormats.findByFile(schematic);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
            return reader.read();
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to load schematic file " + schematic.getName());
            return null;
        }
    }

}
