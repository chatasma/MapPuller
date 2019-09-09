package me.tqnk.mp.command;

import org.bukkit.command.CommandSender;

import java.util.List;

abstract class CommandModel {
    private List<String> aliases;

    CommandModel(List<String> aliases) {
        this.aliases = aliases;
    }

    public boolean isAlias(String command) {
        return aliases.contains(command.toLowerCase());
    }

    public abstract void onCommand(CommandSender sender, String[] args);
}
