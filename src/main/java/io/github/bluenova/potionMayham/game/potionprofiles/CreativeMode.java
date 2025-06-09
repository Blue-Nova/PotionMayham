package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class CreativeMode extends PotionProfile {

    private final String mainColor = "<#22FF4E>";
    private final String secondaryColor = "<#88FFB5>";

    public CreativeMode() {
        super(BossBar.Color.GREEN, 60*2, 0, 20);
        profileId = "creative_mode";
        setName(MiniMessage.miniMessage().deserialize(mainColor+"Creative Mode"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"Just don't do anything..."));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Every 20 seconds you"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"get "+secondaryColor+"creative mode  "));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Doing "+secondaryColor+"<i> literally anything</i>"+mainColor+" will"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"put you back in survival mode"));

        setActiveDescription(activeDescription);

        buildScoreboard();
    }

    @Override
    protected void onFirstApply() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.ENTITY_CAT_PURREOW, 1F, 1F);
    }

    @Override
    protected void applyEffect() {
        // check if on cooldown
        if (isOnCooldown()) {
            return;
        }
        assignedPlayer.setGameMode(GameMode.CREATIVE);
        this.startCooldown();
    }

    @Override
    protected void passiveEffect() {
        if (assignedPlayer == null) {
            return;
        }
        if (assignedPlayer.getGameMode() == GameMode.SURVIVAL) {
            return;
        }
        assignedPlayer.setGameMode(GameMode.SURVIVAL);
        assignedPlayer.sendActionBar(MiniMessage.miniMessage().deserialize("<gray><i>You are no longer AFK..."));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == assignedPlayer) {
            passiveEffect();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() == assignedPlayer ) {
            passiveEffect();
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        if (event.getWhoClicked() == assignedPlayer) {
            if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(true);
            }
            passiveEffect();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() == assignedPlayer) {
            if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(true);
            }
            passiveEffect();
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() == assignedPlayer) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(true);
            }
            passiveEffect();
        }
    }

    @EventHandler
    public void playerEvent(PlayerInputEvent event) {
        if (event.getPlayer() == assignedPlayer) {
            passiveEffect();
        }
    }

    @EventHandler
    public void onPlayerCreativeEvent(InventoryCreativeEvent event) {
        if (event.getWhoClicked() == assignedPlayer) {
            if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(true);
            }
            passiveEffect();
        }
    }

    @EventHandler
    public void onPlayerThrowItem(PlayerDropItemEvent event) {
        if (event.getPlayer() == assignedPlayer) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(true);
                passiveEffect();
            }
        }
    }

    @Override
    protected void removeEffect() {
        assignedPlayer.setGameMode(GameMode.SURVIVAL);
        endPotionEffect();
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new CreativeMode();
    }
}
