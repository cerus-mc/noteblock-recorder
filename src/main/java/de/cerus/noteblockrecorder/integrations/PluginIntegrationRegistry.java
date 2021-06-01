package de.cerus.noteblockrecorder.integrations;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PluginIntegrationRegistry {

    private static final ImmutableList<PluginIntegration> INTEGRATIONS = ImmutableList.of(

    );

    private PluginIntegrationRegistry() {
    }

    public static boolean isAllowed(Player player, Location location) {
        return INTEGRATIONS.stream().allMatch(pluginIntegration -> pluginIntegration.isAllowed(player, location));
    }

}
