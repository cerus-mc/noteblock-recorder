package de.cerus.noteblockrecorder.inventory;

import de.cerus.noteblockrecorder.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class PaginatedInventory implements Listener {

    private boolean autoDispose;
    private List<InventoryItem> inventoryItems;
    private Inventory inventory;

    public PaginatedInventory(JavaPlugin plugin, boolean autoDispose, List<InventoryItem> inventoryItems) {
        this.autoDispose = autoDispose;
        this.inventoryItems = inventoryItems;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Inventory getInventory() {
        if (inventory != null) {
            return inventory;
        }

        List<InventoryItem> firstPage = inventoryItems.stream().limit(3 * 9).collect(Collectors.toList());
        Inventory inventory = Bukkit.createInventory(new PaginatedInventoryHolder(), 4 * 9);

        for (int i = 0; i < firstPage.size(); i++) {
            inventory.setItem(i, firstPage.get(i).getItemStack());
        }
        for (int i = 3 * 9; i < 4 * 9; i++) {
            inventory.setItem(i, new ItemBuilder(Material.IRON_BARS).withName(" ").build());
        }

        inventory.setItem(27, new ItemBuilder(Material.ARROW)
                .withName("§7<-")
                .build());
        inventory.setItem(31, new ItemBuilder(Material.PAPER)
                .withName("§ePage 1")
                .build());
        inventory.setItem(35, new ItemBuilder(Material.ARROW)
                .withName("§7->")
                .build());

        this.inventory = inventory;
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }

        if (!(inventory.getHolder() instanceof PaginatedInventoryHolder)) {
            return;
        }

        if (inventory != this.inventory) {
            return;
        }

        PaginatedInventoryHolder inventoryHolder = (PaginatedInventoryHolder) inventory.getHolder();

        event.setCancelled(true);

        if (event.getSlot() == 27) {
            if (inventoryHolder.getPage() <= 0) {
                return;
            }

            for (int i = 0; i < 3 * 9; i++) {
                inventory.clear(i);
            }

            int page = inventoryHolder.getPage() - 1;
            changePage(inventory, inventoryHolder, page);
            return;
        } else if (event.getSlot() == 35) {
            int maxPage = (inventoryItems.size() / (3 * 9)) + ((inventoryItems.size() % (3 * 9)) == 0 ? 0 : 1);
            if (inventoryHolder.getPage() >= maxPage - 1) {
                return;
            }

            for (int i = 0; i < 3 * 9; i++) {
                inventory.clear(i);
            }

            int page = inventoryHolder.getPage() + 1;
            changePage(inventory, inventoryHolder, page);
            return;
        }

        if (event.getSlot() > 27 && event.getSlot() < 35) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        int index = ((3 * 9) * inventoryHolder.getPage()) + event.getSlot();
        InventoryItem inventoryItem = inventoryItems.get(index);
        inventoryItem.getActionListener().accept(event);
    }

    private void changePage(Inventory inventory, PaginatedInventoryHolder inventoryHolder, int page) {
        for (int i = page * (3 * 9); i < (page * (3 * 9)) + (3 * 9); i++) {
            if (inventoryItems.size() <= i) {
                break;
            }

            inventory.setItem(i - (page * (3 * 9)), inventoryItems.get(i).getItemStack());
        }

        inventoryHolder.setPage(page);

        inventory.setItem(31, new ItemBuilder(Material.PAPER)
                .withName("§ePage " + (page + 1))
                .build());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof PaginatedInventoryHolder)) {
            return;
        }

        if (inventory != this.inventory) {
            return;
        }

        this.inventory = null;
        if (autoDispose) {
            dispose();
        }
    }

    public void dispose() {
        HandlerList.unregisterAll(this);
    }

}
