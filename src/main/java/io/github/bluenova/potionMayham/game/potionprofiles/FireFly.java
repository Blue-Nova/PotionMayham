package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class FireFly extends PotionProfile {

    private final String mainColor = "<#11F0F0>";
    private final String secondaryColor = "<#F0FFFF>";

    public FireFly() {
        super(BossBar.Color.GREEN,(60)*5, 3, 0); // 5 seconds cooldown
        profileId = "fire_fly";
        setName(MiniMessage.miniMessage().deserialize(secondaryColor+"Fire "+mainColor+"Fly"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"Leave it to the "+mainColor+"Pros!"));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"You are "+secondaryColor+"Fast and nimble."));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Your "+secondaryColor+"max health"+mainColor+" is "+secondaryColor+"7 ♡"));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Bow damage is "+secondaryColor+"tripled."));

        setActiveDescription(activeDescription);

        buildScoreboard();
    }

    @Override
    protected void onFirstApply(){

        assignedPlayer.playSound(assignedPlayer.getLocation(), org.bukkit.Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.8F, 1F);

        NamespacedKey pm_max_health_key = NamespacedKey.fromString("potionmayham:max_health");
        AttributeInstance maxHealth = assignedPlayer.getAttribute(Attribute.MAX_HEALTH);
        AttributeModifier maxHealthModifier = new AttributeModifier(pm_max_health_key, -6, AttributeModifier.Operation.ADD_NUMBER);
        assert maxHealth != null;
        if (maxHealth.getModifier(pm_max_health_key) == null){
            maxHealth.addModifier(maxHealthModifier);
        }
    }

    @Override
    protected void applyEffect() {
        // check if on cooldown
        if (isOnCooldown()) {
            return;
        }
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, getAmplifier()));
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, getAmplifier()-1));
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 30, Math.min(getAmplifier(), 1)));
        this.startCooldown();
    }

    @Override
    protected void passiveEffect() {

    }

    @EventHandler
    public void onRangedAttack(EntityDamageByEntityEvent event) {
        PotionMayham.instance.getLogger().info("Event Trigger");
        if (assignedPlayer == null) {
            return;
        }
        PotionMayham.instance.getLogger().info("Starting check");
        if (event.getEntity() instanceof Projectile) { // if entity is projectile (not arrow)
            PotionMayham.instance.getLogger().info("Entity is projectile");
            if (event.getDamager() instanceof org.bukkit.entity.Arrow arrow) { // if damager (projectile) is an arrow
                if (arrow.getShooter() instanceof org.bukkit.entity.Player shooter) { // if shooter is a player
                    if (shooter.equals(assignedPlayer)) { // if that player is the assigned player
                        event.setDamage(event.getDamage() * 3);
                    }
                }
            }
        }
    }

    @Override
    protected void removeEffect() {
        AttributeInstance maxHealth = assignedPlayer.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            try {
                maxHealth.removeModifier(NamespacedKey.fromString("potionmayham:max_health"));
            } catch (IllegalArgumentException e) {
                PotionMayham.instance.getLogger().info("Max health modifier not found, ignoring...");
            }
        }
        assignedPlayer.removePotionEffect(PotionEffectType.SPEED);
        assignedPlayer.removePotionEffect(PotionEffectType.WEAKNESS);
        assignedPlayer.removePotionEffect(PotionEffectType.JUMP_BOOST);
        assignedPlayer = null;
        PotionMayham.instance.unregisterProfile(this);
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new FireFly();
    }
}
