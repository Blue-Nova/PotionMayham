package io.github.bluenova.potionMayham.commands.subcomands;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.commands.SubCommand;
import io.github.bluenova.potionMayham.game.PotionManager;
import io.github.bluenova.potionMayham.game.UI.UIManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GameStart implements SubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // 1. Check if sender is a Player
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }
        Player player = (Player) sender;
        PotionManager potionManager = PotionMayham.instance.getPotionManager();
        UIManager uiManager = PotionMayham.instance.getUIManager();

        potionManager.addInDiddyList(player);
        uiManager.addPlayerToBossBar(player);
        PotionMayham.instance.getLogger().info("Added " + player.getName() + " to the Diddy list.");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList(); // Change this based on your tab completion logic
    }

    @Override
    public String getUsage() {
        return "/potionMayham start";
    }

    @Override
    public String getPermission() {
        return "potionmayham.start";
    }
}
