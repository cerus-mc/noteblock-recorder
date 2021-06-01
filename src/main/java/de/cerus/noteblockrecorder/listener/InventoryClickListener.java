package de.cerus.noteblockrecorder.listener;

import de.cerus.noteblockrecorder.config.impl.MessageConfig;
import de.cerus.noteblockrecorder.config.impl.PluginConfig;
import de.cerus.noteblockrecorder.inventory.SongManageInventoryHolder;
import de.cerus.noteblockrecorder.song.Song;
import de.cerus.noteblockrecorder.song.SongController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClickListener implements Listener {

    private SongController songController;
    private PluginConfig pluginConfig;
    private MessageConfig messageConfig;

    public InventoryClickListener(SongController songController, PluginConfig pluginConfig, MessageConfig messageConfig) {
        this.songController = songController;
        this.pluginConfig = pluginConfig;
        this.messageConfig = messageConfig;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }

        if (!(inventory.getHolder() instanceof SongManageInventoryHolder)) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        if(!player.hasPermission(pluginConfig.permissionManageSongs)) {
            return;
        }

        SongManageInventoryHolder holder = (SongManageInventoryHolder) inventory.getHolder();
        Song song = holder.getSong();

        if (event.getSlot() == 4) {
            songController.deleteSongAsync(song, o ->
                    player.sendMessage(messageConfig.messageInventoryManageSongDeleted));
            player.closeInventory();
        }
    }

}
