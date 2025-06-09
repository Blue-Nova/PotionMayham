package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class SpeedAndStrength extends PotionProfile {

    private final String mainColor = "<#FA5740>";
    private final String secondaryColor = "<#E8E8E8>";

    public SpeedAndStrength() {
        super(BossBar.Color.RED, (60)*8, 2, 0);
        profileId = "speed_and_strength";
        setName(MiniMessage.miniMessage().deserialize(secondaryColor+"Glass "+mainColor+"Cannon"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"Cover your heels"));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"You are "+secondaryColor+"Fast"+mainColor+" and"+secondaryColor+" strong."));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"You "+secondaryColor+"cannot use bows."));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Taking "+secondaryColor+"3 ♡"+mainColor+" or more"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+" damage at once, "+secondaryColor+"<b>Explode."));

        setActiveDescription(activeDescription);

        buildScoreboard();

    }

    @Override
    protected void onFirstApply() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8F, 1F);
    }

    @Override
    protected void applyEffect() {
        // check if on cooldown
        if (isOnCooldown()) {
            return;
        }
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21, getAmplifier()+1));
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 21, getAmplifier()));
        this.startCooldown();
    }

    @Override
    protected void passiveEffect() {
        Bukkit.getScheduler().runTaskLater(PotionMayham.instance, () -> {
            assignedPlayer.setHealth(0);
            assignedPlayer.getWorld().createExplosion(assignedPlayer.getLocation(), 0.5F, false, true);
        }, 0);
    }

    @EventHandler
    public void onDamageTaken(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.equals(assignedPlayer))
                return;
            if (event.getFinalDamage() >= 6) {
                passiveEffect();
            }
        }
    }

    @EventHandler
    public void onPlayerArrowHit(EntityDamageByEntityEvent event){
        if (assignedPlayer == null) {
            return;
        }
        if (event.getEntity() instanceof Projectile) { // if entity is projectile (not arrow)
            if (event.getDamager() instanceof org.bukkit.entity.Arrow arrow) { // if damager (projectile) is an arrow
                if (arrow.getShooter() instanceof org.bukkit.entity.Player shooter) { // if shooter is a player
                    if (shooter.equals(assignedPlayer)) { // if that player is the assigned player
                        event.setCancelled(true);
                    }
                }
            }
        }
    }


    @Override
    protected void removeEffect() {
        if (assignedPlayer == null) {
            return;
        }
        assignedPlayer.removePotionEffect(PotionEffectType.SPEED);
        assignedPlayer.removePotionEffect(PotionEffectType.STRENGTH);
        assignedPlayer = null;
        PotionMayham.instance.unregisterProfile(this);
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new SpeedAndStrength();
    }
}
