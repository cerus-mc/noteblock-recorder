package de.cerus.noteblockrecorder.inventory;

import de.cerus.noteblockrecorder.song.Song;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class SongManageInventoryHolder implements InventoryHolder {

    private Song song;
    private Inventory inventory;

    public SongManageInventoryHolder(Song song) {
        this.song = song;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Song getSong() {
        return song;
    }
}
