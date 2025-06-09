package io.github.bluenova.potionMayham.commands;

import io.github.bluenova.potionMayham.PotionMayham;
import io.github.bluenova.potionMayham.commands.subcomands.GameStart;
import io.github.bluenova.potionMayham.commands.subcomands.GameStop;
import io.github.bluenova.potionMayham.commands.subcomands.PotionSkip;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> commandGroups = new HashMap<>();

    public MainCommand() {
        registerCommands();
    }

    private void registerCommands() {
        commandGroups.put("start", new GameStart());
        commandGroups.put("stop", new GameStop());
        commandGroups.put("skip", new PotionSkip());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("start", "stop", "skip"); // Example commands
        }
        return null; // Change this based on your tab completion logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("Usage: /potionMayham <subcommand>");
            return true;
        }

        String subCommandKey = strings[0].toLowerCase();
        SubCommand subCommand = commandGroups.get(subCommandKey);
        if (subCommand != null) {
            PotionMayham.instance.getLogger().info("Executing subcommand: " + subCommandKey);
            return subCommand.execute(commandSender, strings);
        } else {
            commandSender.sendMessage("Unknown subcommand: " + subCommandKey);
            return false;
        }
    }
}
