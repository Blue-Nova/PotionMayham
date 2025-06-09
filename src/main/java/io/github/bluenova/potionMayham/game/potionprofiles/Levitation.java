package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionProfile;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;

public class Levitation extends PotionProfile {

    private final String mainColor = "<#73FFFF>";
    private final String secondaryColor = "<#E5FFFF>";

    public Levitation() {
        super(BossBar.Color.WHITE,(60)*2, 4, 5); // 5 seconds cooldown
        profileId = "levitation";
        setName(MiniMessage.miniMessage().deserialize(secondaryColor+"Occasional "+mainColor+"Levitational"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"Just don't look down"));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize("Every "+mainColor+"<b>5</b><reset> seconds you get"));
        activeDescription.add(Component.text(" "));
        activeDescription.add(MiniMessage.miniMessage().deserialize("levitation for 2 seconds"));

        setActiveDescription(activeDescription);

        buildScoreboard();

    }

    @Override
    protected void onFirstApply() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
    }

    @Override
    protected void applyEffect() {
        // check if on cooldown
        if (isOnCooldown()) {
            return;
        }
        assignedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 30, getAmplifier()));
        this.startCooldown();
    }

    @Override
    protected void passiveEffect() {
    }

    @Override
    protected void removeEffect() {
        if (assignedPlayer == null) {
            return;
        }
        assignedPlayer.removePotionEffect(PotionEffectType.LEVITATION);
        assignedPlayer = null;
        PotionMayham.instance.unregisterProfile(this);
    }

    @Override
    protected PotionProfile clonePotionProfile() {
        return new Levitation();
    }
}
