package de.cerus.noteblockrecorder.integrations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface PluginIntegration {

    boolean isAllowed(Player player, Location location);

}
