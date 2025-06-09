package io.github.bluenova.potionMayham.events;

import io.github.bluenova.potionMayham.PotionMayham;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoin implements Listener {
    // This class will handle player death events
    // You can add methods to handle specific events related to player death

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        NamespacedKey pm_max_health_key = NamespacedKey.fromString("potionmayham:max_health");
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);

        if (attribute.getModifier(pm_max_health_key) != null) {
            // Add a custom attribute modifier to the player
            attribute.removeModifier(pm_max_health_key);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        NamespacedKey pm_max_health_key = NamespacedKey.fromString("potionmayham:max_health");
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);

        if (attribute.getModifier(pm_max_health_key) != null) {
            // Add a custom attribute modifier to the player
            attribute.removeModifier(pm_max_health_key);
        }

        PotionMayham.potionManager.removeFromDiddyList(player);

    }
}
