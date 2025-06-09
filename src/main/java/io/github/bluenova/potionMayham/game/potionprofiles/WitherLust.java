package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionManager;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.ArrayList;

public class WitherLust extends PotionProfile {

    private final String mainColor = "<#ECECED>";
    private final String secondaryColor = "<#090A0D>";

    public WitherLust() {
        super(BossBar.Color.PURPLE, (60)*3, 2, 4);
        profileId = "wither_lust";
        setName(MiniMessage.miniMessage().deserialize(mainColor+"Wither "+secondaryColor+"Lust"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"Blood For <dark_red>Life"+secondaryColor+","+mainColor+" Life For <dark_red>Blood"));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor + "You lose "+secondaryColor+"<b>0.5 </b>♡"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor + "every "+secondaryColor+"<b>3</b>"+mainColor+" seconds"));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor + "At "+secondaryColor+"<b>0.5 </b>♡"+mainColor+" health, you"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor + "Get "+secondaryColor+"<b>slowness"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor + "and "+secondaryColor+"<b>weakness</b>"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor + "instead."));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor + "Only heal from "+secondaryColor+"<b>Killing<b>."));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor + "Killing "+secondaryColor+"<b>players</b>"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor + "gives <gold><b>insane buffs</b>!"));

        setActiveDescription(activeDescription);

        buildScoreboard();
    }

    @Override
    protected void onFirstApply() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.8F, 1.5F);
    }

    @Override
    protected void applyEffect() {
        // check if on cooldown
        if (isOnCooldown()) {
            return;
        }
        if (assignedPlayer == null) {
            return;
        }
        if (assignedPlayer.isDead()) {
            return;
        }
        if (assignedPlayer.getHealth() <= 1) {
            assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, getAmplifier()));
            assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 30, 0));
            return;
        }
        assignedPlayer.setHealth(Math.max(assignedPlayer.getHealth() - getAmplifier(), 0));
        PotionManager.playSoundInWorld(assignedPlayer.getLocation(), Sound.ENTITY_WITHER_SKELETON_HURT, 0.2F, 0.8F);
        this.startCooldown();
    }

    @Override
    protected void passiveEffect() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.2F, 0.8F);
        assignedPlayer.sendActionBar(MiniMessage.miniMessage().deserialize(secondaryColor+"More! More! "+mainColor+"<b>More!"));
        assignedPlayer.setHealth(Math.min(assignedPlayer.getHealth() + 4, 20));
        this.startCooldown(25);
    }

    private void upgradedPassiveEffect() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.2F, 0.8F);

        Title.Times times = Title.Times.times(Duration.ofMillis(200), Duration.ofMillis(100), Duration.ofMillis(1000));

        Title title = Title.title(
                MiniMessage.miniMessage().deserialize(mainColor+"More More!"),
                MiniMessage.miniMessage().deserialize(secondaryColor+"<dark_red>More More!"),
                times
        );
        assignedPlayer.showTitle(title);

        assignedPlayer.setHealth(assignedPlayer.getAttribute(Attribute.MAX_HEALTH).getValue());
        // untracked effects. no disable when profile is removed
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, getAmplifier()));
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 400, getAmplifier()));
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 400, 0));
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, getAmplifier()));
        this.startCooldown(60);
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (assignedPlayer == null) {
            return;
        }
        if (event.getEntity().getKiller() == assignedPlayer) {
            if (event.getEntity() instanceof org.bukkit.entity.Player) {
                upgradedPassiveEffect();
            }else {
                passiveEffect();
            }
        }
    }

    @EventHandler
    public void onPlayerHeal(EntityRegainHealthEvent event) {
        if (assignedPlayer == null) {
            return;
        }
        if (event.getEntity() instanceof org.bukkit.entity.Player player) {
            if (player.equals(assignedPlayer)) {
                event.setAmount(0);
            }
        }
    }


    @Override
    protected void removeEffect() {
        if (assignedPlayer == null) {
            return;
        }
        assignedPlayer = null;
        PotionMayham.instance.unregisterProfile(this);
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new WitherLust();
    }
}
