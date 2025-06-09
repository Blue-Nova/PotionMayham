package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class ResistanceNoAttack extends PotionProfile {

    private final String mainColor = "<#22DA30>";
    private final String secondaryColor = "<#AAFFAA>";

    public ResistanceNoAttack() {
        super(BossBar.Color.RED,(60)*7, 3, 0); // 5 seconds cooldown
        profileId = "resistance_no_attack";
        setName(MiniMessage.miniMessage().deserialize(secondaryColor+"Turtle "+mainColor+"Shelled"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"Please stop, I'll cry..."));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"You get resistance 3."));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(secondaryColor+"You can't attack."));
        setActiveDescription(activeDescription);

        buildScoreboard();
    }

    @Override
    protected void onFirstApply() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.ENTITY_TURTLE_AMBIENT_LAND, 1F, 1F);
    }

    @Override
    protected void applyEffect() {
        // check if on cooldown
        if (isOnCooldown()) {
            return;
        }
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30, getAmplifier()));
        this.startCooldown();
    }

    @Override
    protected void passiveEffect() {

    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (assignedPlayer == null) {
            return;
        }
        if (event.getDamager().equals(assignedPlayer)) {
            event.setCancelled(true);
            assignedPlayer.sendActionBar(MiniMessage.miniMessage().deserialize(secondaryColor + "You pet them instead..."));
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
                        assignedPlayer.sendActionBar(MiniMessage.miniMessage().deserialize(secondaryColor + "You feel bad about that..."));
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
        assignedPlayer.removePotionEffect(PotionEffectType.RESISTANCE);
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new ResistanceNoAttack();
    }
}
