package io.github.bluenova.potionMayham.commands.subcomands;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.commands.SubCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PotionSkip implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (PotionMayham.instance.getPotionManager().isPlayerInGame(player)) {
                PotionMayham.instance.getPotionManager().skipPlayer(player);
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(sender + " has skipped their potion effect!"));
                return true;
            }
        } else {
            sender.sendMessage("This command can only be run by a player.");
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public String getUsage() {
        return "/pm skip";
    }

    @Override
    public String getPermission() {
        return "";
    }
}
