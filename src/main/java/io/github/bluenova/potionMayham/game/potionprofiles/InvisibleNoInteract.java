package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class InvisibleNoInteract extends PotionProfile {

    private final String mainColor = "<#FEFFFF>";
    private final String secondaryColor = "<#7984C2>";

    public InvisibleNoInteract() {
        super(BossBar.Color.WHITE,(60)*3, 1, 0); // 5 seconds cooldown
        profileId = "invisible_no_interact";
        setName(MiniMessage.miniMessage().deserialize(secondaryColor+"Who "+mainColor+"Asked?"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"Take a Chill Pill, Bro..."));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"You are "+secondaryColor+"Invisible."));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"You cannot "+secondaryColor+"Interact at all."));
        setActiveDescription(activeDescription);

        buildScoreboard();
    }

    @Override
    protected void onFirstApply() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
        assignedPlayer.getWorld().getEntities().forEach(entity -> {
            if (entity instanceof Mob){
                Mob mob = (Mob) entity;
                if (mob.getTarget() != null && mob.getTarget().equals(assignedPlayer)) {
                    mob.setTarget(null);
                }
            }
        });
    }

    @Override
    protected void applyEffect() {
        // check if on cooldown
        if (isOnCooldown()) {
            return;
        }
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30, getAmplifier()));
        //this.startCooldown();
    }

    @Override
    protected void passiveEffect() {
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (assignedPlayer == null) {
            return;
        }
        if (event.getPlayer().equals(assignedPlayer)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event){
        if (assignedPlayer == null) {
            return;
        }
        if (event.getTarget() == assignedPlayer) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (assignedPlayer == null) {
            return;
        }
        if (event.getDamager().equals(assignedPlayer)) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void removeEffect() {
        if (assignedPlayer == null) {
            return;
        }
        assignedPlayer.removePotionEffect(PotionEffectType.INVISIBILITY);
        endPotionEffect();
        PotionMayham.instance.unregisterProfile(this);
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new InvisibleNoInteract();
    }
}
