package net.frostyservices.frostyboxes.commands;

import net.frostyservices.frostyboxes.Main;
import net.frostyservices.frostyboxes.util.FBPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    public static final String COMMAND_NAME = "fb";
    private final Main instance;

    public MainCommand(Main instance) {
        this.instance = instance;
        instance.getCommand(COMMAND_NAME).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 || args[0].equalsIgnoreCase("help") || !sender.hasPermission(FBPermission.ADMIN.toString())) {
            sendPluginInfo(sender);
        }
        return false;
    }

    private void sendPluginInfo(CommandSender sender) {
        String prefix = instance.getBSBConfig().getPrefix().get();
        sender.sendMessage(prefix + ChatColor.AQUA + "This server is running " + ChatColor.YELLOW + "FrostyBoxes v" + instance.getDescription().getVersion() + ChatColor.AQUA + ".");
    }
}
