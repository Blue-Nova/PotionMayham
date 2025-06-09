package io.github.bluenova.potionMayham.game.potionprofiles;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.game.PotionManager;
import io.github.bluenova.potionMayham.game.PotionProfile;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.PlayerInventory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;

public class ExplosiveSneeze extends PotionProfile {

    private final String mainColor = "<#FF4E22>";
    private final String secondaryColor = "<#FFB588>";

    private ArrayList<Fireball> fireballs = new ArrayList<>();

    private final Random random = new Random();

    public ExplosiveSneeze() {
        super(BossBar.Color.PURPLE, (60)*3, 1, 0);
        profileId = "explosive_sneeze";
        setName(MiniMessage.miniMessage().deserialize(mainColor+"Explosive "+secondaryColor+"Sneeze"));
        setDescription(MiniMessage.miniMessage().deserialize("<i>"+secondaryColor+"Guys, I think I'm sick..."));

        ArrayList<Component> activeDescription = new ArrayList<>();

        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"Every now and then"));
        activeDescription.add(MiniMessage.miniMessage().deserialize(mainColor+"you "+secondaryColor+"sneeze a fireball!"));


        setActiveDescription(activeDescription);

        buildScoreboard();
    }

    @Override
    protected void onFirstApply() {
        assignedPlayer.playSound(assignedPlayer.getLocation(), Sound.ENTITY_GHAST_WARN, 1F, 1.5F);
    }

    @Override
    protected void applyEffect() {
        // check if on cooldown
        if (isOnCooldown()) {
            return;
        }
        shootFireBall();

        this.startCooldown((int)(8+(random.nextDouble() * 30)));
    }

    private void shootFireBall() {
        var wrapper = new Object(){ int countdown = 4; };
        Bukkit.getScheduler().runTaskTimer(PotionMayham.instance, (task) ->
        {
            if (assignedPlayer == null) {
                task.cancel();
                return;
            }
            switch (wrapper.countdown) {
                case 1:
                    Title.Times times = Title.Times.times(
                            Duration.ofMillis(250),
                            Duration.ofMillis(800),
                            Duration.ofMillis(500)
                    );
                    Title title = Title.title(
                            MiniMessage.miniMessage().deserialize(mainColor+"Achoo!"),
                            Component.text(""),
                            times
                    );
                    assignedPlayer.showTitle(title);
                    PotionManager.playSoundInWorld(assignedPlayer.getLocation(), Sound.ENTITY_BREEZE_SHOOT, 1F, 1.4F);
                    Fireball fireball = assignedPlayer.getWorld().spawn(assignedPlayer.getLocation().add(0,1,0), Fireball.class);
                    fireballs.add(fireball);
                    task.cancel();
                    return;
                case 3:
                    assignedPlayer.sendActionBar(MiniMessage.miniMessage().deserialize(mainColor+"Ahh!"));
                    PotionManager.playSoundInWorld(assignedPlayer.getLocation(), Sound.ENTITY_HORSE_BREATHE, 2F, 2F);
                    break;
                case 4:
                    PotionManager.playSoundInWorld(assignedPlayer.getLocation(), Sound.ENTITY_HORSE_BREATHE, 1F, 1.5F);
                    assignedPlayer.sendActionBar(MiniMessage.miniMessage().deserialize(secondaryColor+"ah..."));
                    break;
            }wrapper.countdown--;
            }, 0, 21);
    }

    @Override
    protected void passiveEffect() {

    }

    @EventHandler
    public void onFireBallCollide(ProjectileHitEvent event) {
        if (assignedPlayer == null) {
            return;
        }
        if (fireballs.contains(event.getEntity())) {
            Fireball fireball = fireballs.get(fireballs.indexOf(event.getEntity()));
            event.setCancelled(true);
            fireball.getWorld().createExplosion(fireball.getLocation(), 3F, true, true);
            fireball.remove();
            fireballs.remove(fireball);
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
        return new ExplosiveSneeze();
    }
}
