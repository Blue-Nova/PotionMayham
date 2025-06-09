package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class MoleMiner extends PotionProfile {

    private final String mainColor = "<#FAB54D>";
    private final String secondaryColor = "<#FFFFF0>";

    public MoleMiner() {
        super(BossBar.Color.YELLOW,(60)*10, 2, 0); // 5 seconds cooldown
        profileId = "mole_miner";
        setName(MiniMessage.miniMessage().deserialize(secondaryColor+"Mole"+mainColor+"cular "+secondaryColor+"Miner"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"I must dig a big sick dug dig a big dig!"));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Above "+secondaryColor+"y level <b>60</b>"+mainColor+", get"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"blindness and slowness"));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Below "+secondaryColor+"y level <b>60</b>"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"get haste, night vision"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"instead."));

        setActiveDescription(activeDescription);

        buildScoreboard();
    }

    @Override
    protected void onFirstApply() {
        var wrapper = new Object(){ int countdown = 4; };
        Bukkit.getScheduler().runTaskTimer(PotionMayham.instance, (task) -> {
            if (assignedPlayer == null) {
                task.cancel();
                return;
            }
            if (wrapper.countdown > 0) {
                assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.BLOCK_GRASS_HIT, 1F, 1F);
                wrapper.countdown--;
            }else{
                assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.BLOCK_GRASS_BREAK, 1F, 1F);
                task.cancel();
            }

        }, 0L, 4L);
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
        //NamespacedKey pm_max_health_key = NamespacedKey.fromString("potionmayham:max_health");
        //AttributeInstance maxHealth = assignedPlayer.getAttribute(Attribute.MAX_HEALTH);
        //AttributeModifier maxHealthModifier = new AttributeModifier(pm_max_health_key, -6, AttributeModifier.Operation.ADD_NUMBER);
        //assert maxHealth != null;
        //if (maxHealth.getModifier(pm_max_health_key) == null){
        //    maxHealth.addModifier(maxHealthModifier);
        //}
        if (assignedPlayer.getLocation().getY() < 60) {
            passiveEffect();
            return;
        }
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 42, getAmplifier()));
        this.startCooldown();
    }

    @Override
    protected void passiveEffect() {
        if (assignedPlayer == null) {
            return;
        }
        assignedPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
        assignedPlayer.removePotionEffect(PotionEffectType.SLOWNESS);
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 22, getAmplifier()+1));
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, getAmplifier()));
    }

    @Override
    protected void removeEffect() {
        if (assignedPlayer == null) {
            return;
        }
        assignedPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
        assignedPlayer.removePotionEffect(PotionEffectType.SLOWNESS);
        assignedPlayer.removePotionEffect(PotionEffectType.HASTE);
        assignedPlayer.removePotionEffect(PotionEffectType.NIGHT_VISION);
        assignedPlayer = null;
        PotionMayham.instance.unregisterProfile(this);
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new MoleMiner();
    }
}
