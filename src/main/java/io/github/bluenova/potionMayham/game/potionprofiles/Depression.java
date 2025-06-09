package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Objects;

public class Depression extends PotionProfile {

    private final String mainColor = "<#A240B4>";
    private final String secondaryColor = "<#9F9E9F>";

    public Depression() {
        super(BossBar.Color.PURPLE, (60)*12, 1, 0);
        setName(MiniMessage.miniMessage().deserialize(mainColor+"Depression"));
        profileId = "depression";
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"No one cares..."));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"You have "+secondaryColor+"slowness and fatigue"));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(secondaryColor+"Eat cake"+mainColor+" to feel happy again :)"));
        setActiveDescription(activeDescription);

        buildScoreboard();
    }

    @Override
    protected void onFirstApply() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
    }

    @Override
    protected void applyEffect() {
        // check if on cooldown
        if (isOnCooldown()) {
            return;
        }
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 21, getAmplifier()));
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 21, getAmplifier()));
        this.startCooldown();
    }

    @Override
    protected void passiveEffect() {
        if (assignedPlayer == null) {
            return;
        }
        assignedPlayer.removePotionEffect(PotionEffectType.SLOWNESS);
        assignedPlayer.removePotionEffect(PotionEffectType.MINING_FATIGUE);
        assignedPlayer.sendActionBar(MiniMessage.miniMessage().deserialize("<#FF0055>YAY! You feel happy again!"));
        assignedPlayer.playSound(assignedPlayer, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        endPotionEffect();
    }

    @EventHandler
    public void onPlayerEatCake(PlayerInteractEvent event) {
        if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            if (Objects.requireNonNull(event.getClickedBlock()).getType() == org.bukkit.Material.CAKE) {
                passiveEffect();
            }
        }
    }

    @Override
    protected void removeEffect() {
        endPotionEffect();
        PotionMayham.instance.unregisterProfile(this);
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new Depression();
    }
}
