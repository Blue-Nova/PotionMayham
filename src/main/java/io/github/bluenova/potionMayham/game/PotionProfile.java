package io.github.bluenova.potionMayham.game;

import io.github.bluenova.potionMayham.PotionMayham;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class PotionProfile implements Listener {

    public String profileId;
    private Component name;
    private Component description;

    private ArrayList<Component> activeDescription;
    private ArrayList<Component> passiveDescription;

    private Scoreboard scoreboard;

    protected Player assignedPlayer;

    private int duration;
    private final int amplifier;

    protected int cooldown;
    private boolean isOnCooldown;
    private boolean isOnCustomCooldown;

    private final BossBar.Color bossColor;

    public PotionProfile(BossBar.Color bossColor , int duration, int amplifier, int cooldown) {
        this.bossColor = bossColor;
        this.duration = (int)(duration * 0.4F );
        this.amplifier = amplifier - 1;
        this.cooldown = cooldown;
        this.isOnCooldown = false;
        this.isOnCustomCooldown = false;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public @NotNull Component getName() {
        return name;
    }

    public Component getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    protected int getAmplifier() {
        return amplifier;
    }

    protected boolean isOnCooldown() {
        return isOnCooldown || isOnCustomCooldown;
    }

    private BukkitTask cooldownTask;
    protected void startCooldown() {
        this.isOnCooldown = true;
        if (cooldownTask != null) {
            cooldownTask.cancel();
        }
        cooldownTask = Bukkit.getScheduler().runTaskLater(PotionMayham.instance, () -> {
            if (this.isOnCooldown) {
                this.isOnCooldown = false;
            }
        }, cooldown * 20L); // Convert seconds to ticks
    }

    private BukkitTask customCooldownTask;
    protected void startCooldown(int CustomCooldown) {
        this.isOnCustomCooldown = true;
        if (customCooldownTask != null) {
            customCooldownTask.cancel();
        }
        customCooldownTask = Bukkit.getScheduler().runTaskLater(PotionMayham.instance, () -> {
            if (this.isOnCustomCooldown) {
                this.isOnCustomCooldown = false;
            }
        }, CustomCooldown * 20L); // Convert seconds to ticks
    }

    protected abstract void onFirstApply();

    protected abstract void applyEffect();

    protected abstract void passiveEffect();

    protected abstract void removeEffect();

    protected void buildScoreboard() {
        Scoreboard scoreboard = getScoreboard();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);

        Objective title = scoreboard.registerNewObjective(
                "PotionEffect",
                Criteria.create("profile_name"),
                getName()
        );

        title.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score subTitleDivider = title.getScore("divider");
        subTitleDivider.customName(MiniMessage.miniMessage().deserialize("<gray>-------"));
        subTitleDivider.numberFormat(NumberFormat.fixed(
                MiniMessage.miniMessage().deserialize("<gray>-------")));
        subTitleDivider.setScore(16);

        ArrayList<Component> activeDescription = getActiveDescription();

        for (int i = 0; i < activeDescription.size(); i++) {
            Score TitleAD = title.getScore("AD" + i);
            TitleAD.customName(Component.text(""));
            TitleAD.numberFormat(NumberFormat.fixed(activeDescription.get(i)));
            TitleAD.setScore(14 - i);
        }
    }

    protected void endPotionEffect() {
        assignedPlayer = null;
    }

    public PotionProfile setAssignedPlayer(Player player) {
        this.assignedPlayer = player;
        return this;
    }

    public BossBar.Color getMainColor() {
        return bossColor;
    }

    protected void setName(Component name) {
        this.name = name;
    }

    public void setDescription(Component description) {
        this.description = description;
    }

    public ArrayList<Component> getActiveDescription() {
        return activeDescription;
    }

    public void setActiveDescription(ArrayList<Component> activeDescription) {
        this.activeDescription = activeDescription;
    }

    public ArrayList<Component> getPassiveDescription() {
        return passiveDescription;
    }

    public void setPassiveDescription(ArrayList<Component> passiveDescription) {
        this.passiveDescription = passiveDescription;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    protected abstract PotionProfile clonePotionProfile();
}
