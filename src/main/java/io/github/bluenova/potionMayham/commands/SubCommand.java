package io.github.bluenova.potionMayham.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    boolean execute(CommandSender sender, String[] args);

    List<String> tabComplete(CommandSender sender, String[] args);

    String getUsage();

    String getPermission();

}
