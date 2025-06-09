package io.github.bluenova.potionMayham.commands.subcomands;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GameStop implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        PotionMayham.instance.getPotionManager().removeFromDiddyList((Player)sender);
        PotionMayham.instance.getUIManager().removePlayerFromBossBar((Player)sender);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getPermission() {
        return "potionmayham.stop";
    }
}
