package me.jackz.missilewars.game.missile;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import me.jackz.missilewars.MissileWars;
import me.jackz.missilewars.game.GamePlayers;
import me.jackz.missilewars.game.missile.SpawnedMissile;
import me.jackz.missilewars.lib.ClipboardLoader;
import me.jackz.missilewars.lib.Schematic;
import me.jackz.missilewars.lib.SchematicResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SpawnableMissile {
    private final String id;
    private String display;
    private String hint;
    private final Material material;
    private Schematic greenTeamSchematic;
    private Schematic redTeamSchematic;
    private String schematicName;
    private int rotation = 0;
    private int offset;

    public SpawnableMissile(String name, Material material) {
        this.id = name;
        this.material = material;
    }
    public SpawnableMissile(String name, Material material, int rotation, int offset) {
        this.id = name;
        this.material = material;
        this.rotation = rotation;
        this.offset = offset;
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        if(display != null) {
            return display;
        } else {
            return id.substring(0, 1).toUpperCase() + id.substring(1);
        }
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Material getMaterial() {
        return material;
    }

    public String getSchematicName() {
        return schematicName != null ? schematicName : id;
    }

    public String getFullSchematic(GamePlayers.MWTeam team) {
        return GamePlayers.getTeamName(team).toLowerCase() + "-" + getSchematicName();
    }

    public void loadSchematic() throws IOException {
        File file = new File(MissileWars.getInstance().getDataFolder(),"schematics/" + this.getFullSchematic(GamePlayers.MWTeam.RED));
        this.redTeamSchematic = Schematic.loadSchematic(file);
        file = new File(MissileWars.getInstance().getDataFolder(),"schematics/" + this.getFullSchematic(GamePlayers.MWTeam.GREEN));
        this.greenTeamSchematic = Schematic.loadSchematic(file);
    }

    public void setSchematicName(String schematic) {
        this.schematicName = schematic;
    }

    public void spawn(Player creator, Location origin){
        GamePlayers.MWTeam team = MissileWars.gameManager.players().getTeam(creator);

        CompletableFuture<SchematicResult> future = new CompletableFuture<SchematicResult>();
        future.thenApply((result) -> {
            SpawnedMissile missile = new SpawnedMissile(result.getBlocks(), team, creator);
            MissileWars.gameManager.missiles().addMissile(missile);
            return result;
        });

        origin = origin.add(0,-3, this.offset );

        if(team == GamePlayers.MWTeam.GREEN)
            this.greenTeamSchematic.paste(origin, future);
        else
            this.redTeamSchematic.paste(origin, future);

        /*BukkitPlayer wePlayer = BukkitAdapter.adapt(creator);

        Clipboard clipboard = ClipboardLoader.getClipboard(this.schematicName);
        if(clipboard == null) {
            Bukkit.getLogger().warning("Could not find schematic '" + this.id + "'");
            creator.sendMessage("§cSorry, there is no schematic for this item to place.");
            return null;
        } else {
            AffineTransform transform = new AffineTransform();
            transform = transform.rotateY(this.rotation);
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(wePlayer.getWorld(), 200)) {
                //editSession.setFastMode(true);
                ClipboardHolder holder = new ClipboardHolder(clipboard);
                holder.setTransform(holder.getTransform().combine(transform));
                Operation operation = holder
                        .createPaste(editSession)
                        .copyEntities(false)
                        .to(BlockVector3.at(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ()))
                        .ignoreAirBlocks(true)
                        // configure here
                        .build();
                Operations.complete(operation);

                BlockVector3 min = editSession.getMinimumPoint();
                BlockVector3 max = editSession.getMaximumPoint();
                for(int x = min.getBlockX(); x < max.getBlockX(); x++) {
                    for(int y = min.getBlockY(); y < max.getBlockY(); y++) {
                        for(int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                            BlockState state = editSession.getBlock(BlockVector3.at(x, y, z));
                        }
                    }
                }

                return new SpawnedMissile(team, creator);
            } catch (WorldEditException ex) {
                ex.printStackTrace();
                creator.sendMessage("§cException while placing: " + ex.getMessage());
                return null;
            }
        }*/
    }
}
