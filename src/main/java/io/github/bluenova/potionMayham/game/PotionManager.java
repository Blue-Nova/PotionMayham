package io.github.bluenova.potionMayham.game;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.potionprofiles.*;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PotionManager {

    private Map<Player, PotionProfile> diddlyList;
    private Map<Player, Integer> diddyDurations;

    private BukkitRunnable potionTask;

    private ArrayList<PotionProfile> allProfiles;

    private ArrayList<Player> playersInMayham = new ArrayList<>();

    //private Map<Player, ArrayList<PotionProfile>> recentProfiles = new HashMap<>();

    public PotionManager() {
        this.diddlyList = new HashMap<>();
        this.diddyDurations = new HashMap<>();

        // Register all potion profiles
        allProfiles = new ArrayList<>();

        allProfiles.add(new CreativeMode());
        allProfiles.add(new SpeedAndStrength());
        allProfiles.add(new Levitation());
        allProfiles.add(new Depression());
        allProfiles.add(new WitherLust());
        allProfiles.add(new MoleMiner());
        allProfiles.add(new ResistanceNoAttack());
        allProfiles.add(new InvisibleNoInteract());
        allProfiles.add(new ExplosiveSneeze());
        allProfiles.add(new FireFly());

        //allProfiles.add(new TrueMayham());

        potionTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, PotionProfile> entry : diddlyList.entrySet()) {
                    Player player = entry.getKey();
                    PotionProfile profile = entry.getValue();
                    // check if each player has their potion effect
                    if (profile != null) {
                        boolean profileHasPlayer = profile.assignedPlayer != null;
                        if (profileHasPlayer && profile.assignedPlayer.equals(player)) {
                            profile.applyEffect();
                            PotionMayham.instance.getUIManager().updateUI(player, profile, false);
                        }
                    }else{
                        resetEffect(player);
                    }
                    if (!tickDuration(player)){
                        resetEffect(player);
                    }
                }
                //logAll();
            }
        };

        potionTask.runTaskTimer(PotionMayham.instance, 0, 20); // Run every second (20 ticks)
    }

    private void logAll() {
        for (Map.Entry<Player, PotionProfile> entry : diddlyList.entrySet()) {
            Player player = entry.getKey();
            PotionProfile effect = entry.getValue();
            int duration = diddyDurations.get(player);
            PotionMayham.instance.getLogger().info("Player: " + player.getName() + ", Effect: " + effect.getName() + ", Duration: " + duration);
        }
    }

    private void resetEffect(Player player) {
        if (diddlyList.containsKey(player)) {
            PotionProfile effect = diddlyList.get(player);
            if (effect != null && effect.assignedPlayer.equals(player)) {
                effect.removeEffect();
                PotionMayham.instance.unregisterProfile(effect);
            }
            effect = rollPotionEffect(player).clonePotionProfile();
            effect.setAssignedPlayer(player);
            PotionMayham.instance.registerProfile(effect);
            effect.onFirstApply();
            diddyDurations.put(player, effect.getDuration());
            diddlyList.put(player, effect);
        }
        PotionMayham.instance.getUIManager().updateUI(player, diddlyList.get(player), true);
    }

    private boolean tickDuration(Player player) {
        if (diddyDurations.containsKey(player)) {
            int duration = diddyDurations.get(player);
            if (duration > 0) {
                diddyDurations.put(player, duration - 1);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void addInDiddyList(Player player) {
        if (!diddlyList.containsKey(player)) {
            diddlyList.put(player, null);
            diddyDurations.put(player, 0);
        }
    }

    public void removeFromDiddyList(Player player) {
        if (diddlyList.containsKey(player)) {
            PotionProfile effect = diddlyList.get(player);
            if (effect != null) {
                effect.removeEffect();
            }
            diddlyList.remove(player);
            diddyDurations.remove(player);
            PotionMayham.instance.getUIManager().removeUI(player);
        }
    }

    public PotionProfile rollPotionEffect(Player player) {
        int randomIndex = (int) (Math.random() * allProfiles.size());
        PotionProfile oldProfile = getCurrentProfile(player);
        if (oldProfile != null) {
            if (oldProfile.profileId.equalsIgnoreCase("true_mayham")) {
                return oldProfile.clonePotionProfile();
            }
        }
        if (allProfiles.size() == 1) {
            return allProfiles.get(0);
        }
        if (oldProfile == null) {
            return allProfiles.get(randomIndex);
        }
        while (oldProfile.profileId.equalsIgnoreCase(allProfiles.get(randomIndex).profileId)) {
            PotionMayham.instance.getLogger().info("Rolling again: " + allProfiles.get(randomIndex).profileId);
            randomIndex = (int) (Math.random() * allProfiles.size());
        }
        return allProfiles.get(randomIndex);
    }

    private PotionProfile getCurrentProfile(Player player) {
        if (diddlyList.containsKey(player)) {
            return diddlyList.get(player);
        }
        return null;
    }


    public float getDurationBossBar(Player player) {
        int duration = 0;
        PotionProfile effect = null;
        if (diddyDurations.containsKey(player)) {
            duration = diddyDurations.get(player);
        }
        if (diddlyList.containsKey(player)) {
            effect = diddlyList.get(player);
        }
        if (effect == null) {
            return 0;
        }
        return (float) duration / effect.getDuration();
    }

    public static void playSoundInWorld(Location location, Sound sound, float volume, float pitch) {
        if (location.getWorld() != null) {
            location.getWorld().playSound(location, sound, volume, pitch);
        }
    }

    public boolean isPlayerInGame(Player player) {
        return diddlyList.containsKey(player);
    }

    public void skipPlayer(Player player) {
        if (diddlyList.containsKey(player)) {
            resetEffect(player);
        }
    }

    public void applyTrueMayham(Player player) {
        // remove the current effect
        if (diddlyList.containsKey(player)) {
            PotionProfile effect = diddlyList.get(player);
            if (effect != null) {
                // if current effect is TrueMayham, return
                if (effect.profileId.equalsIgnoreCase("true_mayham")) {
                    return;
                }
                effect.removeEffect();
            }
        }

        // apply the TrueMayham effect
        PotionProfile effect = new TrueMayham();
        effect.setAssignedPlayer(player);
        PotionMayham.instance.registerProfile(effect);
        effect.onFirstApply();
        diddyDurations.put(player, effect.getDuration());
        diddlyList.put(player, effect);
        PotionMayham.instance.getUIManager().updateUI(player, diddlyList.get(player), true);
    }
}
