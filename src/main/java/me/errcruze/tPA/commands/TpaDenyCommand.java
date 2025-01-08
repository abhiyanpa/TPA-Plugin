package me.errcruze.tPA.commands;

import me.errcruze.tPA.managers.TpaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaDenyCommand implements CommandExecutor {
    private final TpaManager tpaManager;

    public TpaDenyCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (!sender.hasPermission("tpasystem.use")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        tpaManager.denyRequest((Player) sender);
        return true;
    }
}