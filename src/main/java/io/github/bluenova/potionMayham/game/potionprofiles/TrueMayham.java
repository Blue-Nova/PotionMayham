package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TrueMayham extends PotionProfile {

    private final String mainColor = "<#8E25AB>";
    private final String secondaryColor = "<#22FF30>";

    private final HashMap<PotionEffectType,BukkitTask> activeEffects = new HashMap<>();

    private final ArrayList<PotionEffectType> allPotionEffects = new ArrayList<>(Registry.EFFECT.stream().toList());

    public TrueMayham() {
        super(BossBar.Color.PURPLE, (60)*25, 1, 30);
        profileId = "true_mayham";
        setName(MiniMessage.miniMessage().deserialize(secondaryColor+"<b><shadow:black>Absolute "+mainColor+"Mayham"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"You'll regret <black><b>everything</b>"+secondaryColor+"..."));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Every "+secondaryColor+"35 seconds"+mainColor+", you get"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"a "+secondaryColor+"<b>random potion</b> "+mainColor+"effect")); // potion emoji
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"for "+secondaryColor+"<b>20</b>"+mainColor+" seconds."));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Every now and then"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"the "+secondaryColor+"potion reapplies."));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"This effect loops"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(secondaryColor+"Forever!"));

        setActiveDescription(activeDescription);

        applyBlackListedEffects();

        buildScoreboard();
    }

    private void applyBlackListedEffects() {
        allPotionEffects.remove(PotionEffectType.WITHER);
        allPotionEffects.remove(PotionEffectType.WIND_CHARGED);
        allPotionEffects.remove(PotionEffectType.UNLUCK);
        allPotionEffects.remove(PotionEffectType.HERO_OF_THE_VILLAGE);
        allPotionEffects.remove(PotionEffectType.INSTANT_DAMAGE);
        allPotionEffects.remove(PotionEffectType.INSTANT_HEALTH);
        allPotionEffects.remove(PotionEffectType.OOZING);
        allPotionEffects.remove(PotionEffectType.BAD_OMEN);
        allPotionEffects.remove(PotionEffectType.RAID_OMEN);
        allPotionEffects.remove(PotionEffectType.TRIAL_OMEN);
        allPotionEffects.remove(PotionEffectType.WEAVING);
        allPotionEffects.remove(PotionEffectType.INFESTED);
    }

    @Override
    protected void onFirstApply() {
        startCooldown(6);
        var wrapper = new Object(){ int countdown = 50; };
        Bukkit.getScheduler().runTaskTimer(PotionMayham.instance, (task) -> {
            if (assignedPlayer == null) {
                task.cancel();
                return;
            }
            switch (wrapper.countdown) {
                case 50, 20:
                    assignedPlayer.playSound(assignedPlayer, Sound.BLOCK_BELL_USE, 1.5F, 0.8F);
                    break;
                case 45, 15:
                    assignedPlayer.playSound(assignedPlayer, Sound.BLOCK_BELL_USE, 1.5F, 0.3F);
                    break;
                case 0:
                    task.cancel();
                    break;
            }
            wrapper.countdown--;
        }, 0L, 2L);
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
        if (allPotionEffects.isEmpty()) {
            this.startCooldown();
            return;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(allPotionEffects.size());
        PotionEffectType randomEffect = allPotionEffects.get(randomIndex);
        allPotionEffects.remove(randomIndex);

        applyMayhamEffect(randomEffect);
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.BLOCK_BELL_USE, 0.9F, 0.3F);
        this.startCooldown();
    }

    @Override
    protected void passiveEffect() {
    }

    private void applyMayhamEffect(PotionEffectType randomEffect) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(PotionMayham.instance, () -> {
            assignedPlayer.addPotionEffect(new PotionEffect(randomEffect, 25*20, 0));
            assignedPlayer.sendActionBar(MiniMessage.miniMessage().deserialize(mainColor+"<b>Mayham!"));
        }, 0, (45)*20);

        activeEffects.put(randomEffect, task);
    }

    @Override
    protected void removeEffect() {
        if (assignedPlayer == null) {
            return;
        }
        for (PotionEffectType effect : activeEffects.keySet()) {
            assignedPlayer.removePotionEffect(effect);
            activeEffects.get(effect).cancel();
        }
        assignedPlayer = null;
        PotionMayham.instance.unregisterProfile(this);
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new TrueMayham();
    }
}
