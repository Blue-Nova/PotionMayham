package io.github.bluenova.potionMayham.game.UI;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionManager;
import io.github.bluenova.potionMayham.game.PotionProfile;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.time.Duration;
import java.util.HashMap;

public class UIManager {

    private final PotionManager potionManager;
    private static HashMap<Player, BossBar> playerBossBarMap = new HashMap<>();

    public UIManager(PotionManager potionManager) {
        this.potionManager = potionManager;
    }

    public void updateUI(Player player, PotionProfile profile, boolean isNewEffect) {

        // Check if the player is in the boss bar map
        if (!playerBossBarMap.containsKey(player)) {
            PotionMayham.instance.getLogger().info("Player " + player.getName() + " is not in the boss bar map.");
            return;
        }

        // if new effect, remove old boss bar and create a new one
        if (isNewEffect) {
            BossBar oldBossBar = playerBossBarMap.get(player);
            if (oldBossBar != null) {
                player.hideBossBar(oldBossBar);
            }
            playerBossBarMap.put(player, createBossBar(profile, player));
            player.showBossBar(playerBossBarMap.get(player));

            Scoreboard scoreboard = profile.getScoreboard();
            player.setScoreboard(scoreboard);

            Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(1000), Duration.ofMillis(750));
            if (profile.profileId.equalsIgnoreCase("true_mayham")) {
                times = Title.Times.times(Duration.ofMillis(700), Duration.ofMillis(1500), Duration.ofMillis(1000));
            }
            Title title = Title.title(
                    profile.getName(),
                    profile.getDescription(),
                    times
            );
            player.showTitle(title);

        }else{
            BossBar bossBar = playerBossBarMap.get(player);
            bossBar.progress(potionManager.getDurationBossBar(player));
        }
    }

    private BossBar createBossBar(PotionProfile profile, Player player) {
        return BossBar.bossBar(
            profile.getName(),
            potionManager.getDurationBossBar(player),
            profile.getMainColor(),
            BossBar.Overlay.PROGRESS
        );
    }

    public void addPlayerToBossBar(Player player) {
        if (playerBossBarMap.containsKey(player)) {
            return;
        }
        playerBossBarMap.put(player, null);
    }

    public void removePlayerFromBossBar(Player sender) {
        if (playerBossBarMap.containsKey(sender)) {
            BossBar bossBar = playerBossBarMap.get(sender);
            if (bossBar != null) {
                sender.hideBossBar(bossBar);
            }
            playerBossBarMap.remove(sender);
        } else {
            PotionMayham.instance.getLogger().info("Player " + sender.getName() + " is not in the boss bar map.");
        }
    }

    public void removeUI(Player player) {
        if (playerBossBarMap.containsKey(player)) {
            BossBar bossBar = playerBossBarMap.get(player);
            if (bossBar != null) {
                player.hideBossBar(bossBar);
            }
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            playerBossBarMap.remove(player);
        } else {
            PotionMayham.instance.getLogger().info("Player " + player.getName() + " is not in the boss bar map.");
        }
    }
}
