package de.cerus.noteblockrecorder.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InventoryItem {

    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> actionListener;

    public InventoryItem(ItemStack itemStack, Consumer<InventoryClickEvent> actionListener) {
        this.itemStack = itemStack;
        this.actionListener = actionListener;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Consumer<InventoryClickEvent> getActionListener() {
        return actionListener;
    }
}
