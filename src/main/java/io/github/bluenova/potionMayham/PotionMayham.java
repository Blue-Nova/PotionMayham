package io.github.bluenova.potionMayham;

import io.github.bluenova.potionMayham.commands.MainCommand;
import io.github.bluenova.potionMayham.events.EventManager;
import io.github.bluenova.potionMayham.game.PotionManager;
import io.github.bluenova.potionMayham.game.UI.UIManager;
import io.github.bluenova.potionMayham.game.PotionProfile;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public final class PotionMayham extends JavaPlugin {

    public static PotionMayham instance;

    public static MainCommand mainCommand;

    public static PotionManager potionManager;
    public static UIManager uiManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getLogger().info("PotionMayham has been enabled!");
        mainCommand = new MainCommand();
        Objects.requireNonNull(getCommand("pm")).setExecutor(mainCommand);
        Objects.requireNonNull(getCommand("pm")).setTabCompleter(mainCommand);

        EventManager.registerEvents();

        potionManager = new PotionManager();

        uiManager = new UIManager(potionManager);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public PotionManager getPotionManager() {
        return potionManager;
    }

    public UIManager getUIManager() {
        return uiManager;
    }

    public void registerProfile(PotionProfile profile) {
        Bukkit.getServer().getPluginManager().registerEvents(profile, this);
    }

    public void unregisterProfile(PotionProfile profile) {
        if (!HandlerList.getRegisteredListeners(this).isEmpty()) {
            instance.getLogger().info("Unregistering profile: " + profile.profileId);
            HandlerList.unregisterAll(profile);
        }
    }

}
