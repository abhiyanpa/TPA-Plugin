package me.errcruze.tPA.commands;

import me.errcruze.tPA.managers.TpaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {
    private final TpaManager tpaManager;

    public TpaCommand(TpaManager tpaManager) {
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

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /tpa <player>");
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        if (target == player) {
            sender.sendMessage("§cYou cannot teleport to yourself!");
            return true;
        }

        tpaManager.sendTpaRequest(player, target);
        return true;
    }
}