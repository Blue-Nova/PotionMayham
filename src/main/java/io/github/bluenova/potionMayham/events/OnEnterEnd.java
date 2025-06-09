package io.github.bluenova.potionMayham.events;

import io.github.bluenova.potionMayham.PotionMayham;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class OnEnterEnd implements Listener {

    @EventHandler
    public void onEnterEnd(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World enteredWorld = event.getPlayer().getWorld();
        if (enteredWorld.getEnvironment().equals(World.Environment.THE_END)) {
            if (PotionMayham.instance.getPotionManager().isPlayerInGame(player)) {
                PotionMayham.instance.getPotionManager().applyTrueMayham(player);
            }
        }
    }

}
