package io.github.bluenova.potionMayham.events;

import io.github.bluenova.potionMayham.PotionMayham;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class EventManager {

    public static void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), PotionMayham.instance);
        Bukkit.getPluginManager().registerEvents(new OnEnterEnd(), PotionMayham.instance);
    }

    public static void unregisterEvents() {
        HandlerList.unregisterAll(PotionMayham.instance);
    }
}
