package me.tqnk.mp.command;

import me.tqnk.mp.MapPuller;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ConfigReloadCommand extends CommandModel {
    public ConfigReloadCommand() {
        super(new ArrayList<String>() {{
            add("reloadmappuller");
        }});
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player target = (Player) sender;
        if(!target.hasPermission("mappuller.reload")) {
            target.sendMessage(ChatColor.RED + "No permission");
            return;
        }
        target.sendMessage(ChatColor.YELLOW + "Reloaded config for MapPuller");
        MapPuller.get().reloadConfig();
        MapPuller.get().setScavenger(MapPuller.createScavengerFromConfig(MapPuller.get().getConfig().getConfigurationSection("source")));
    }
}
