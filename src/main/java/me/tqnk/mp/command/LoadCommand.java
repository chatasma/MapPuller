package me.tqnk.mp.command;

import me.tqnk.mp.MapPuller;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LoadCommand extends CommandModel {
    public LoadCommand() {
        super(new ArrayList<String>() {{
            add("downloadmaps");
        }});
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player target = (Player) sender;
        if(!target.hasPermission("mappuller.download")) {
            target.sendMessage(ChatColor.RED + "No permission");
            return;
        }
        if(MapPuller.get().getScavenger() == null) {
            target.sendMessage(ChatColor.RED + "Could not download maps. Is the configuration configured correctly?");
            return;
        }
        if (args.length == 0) {
            target.sendMessage(ChatColor.GREEN + "Attempting to download maps. " + ChatColor.YELLOW + "Monitor progress/errors in logs");
            target.sendMessage(ChatColor.GREEN + "This usually takes between 30 seconds to 2 minutes");
            new Thread(() -> MapPuller.get().getScavenger().loadMaps()).start();
            return;
        }
        String path = String.join(" ", args);
        target.sendMessage(ChatColor.GREEN + "Attempting to download file(s) \"" + path + "\". " + ChatColor.YELLOW + "Monitor progress/errors in logs");
        new Thread(() -> {
            try {
                MapPuller.get().getScavenger().loadFile(path);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
        }).start();
    }
}
