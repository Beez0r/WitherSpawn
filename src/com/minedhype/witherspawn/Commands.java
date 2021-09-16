package com.minedhype.witherspawn;

import java.sql.PreparedStatement;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class Commands implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(Permission.WS_ADMIN.toString())) {
			sender.sendMessage(Messages.NO_PERMISSION.toString());
			return false;
		}
		if(args.length == 0)
			listSubCmd(sender, label);
		else if(args[0].equalsIgnoreCase("reload"))
			reloadConfigFile(sender);
		else if(!WitherSpawn.getPlugin().checkPluginEnabled()) {
			sender.sendMessage(Messages.PLUGIN_DISABLED.toString());
			return false;
		}
		else if(args[0].equalsIgnoreCase("toggle"))
			toggleWither(sender);
		else if(args[0].equalsIgnoreCase("killall"))
			killAll(sender);
		else if(args[0].equalsIgnoreCase("list"))
			Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> listWithers(sender));
		else if(args[0].equalsIgnoreCase("radiuskill") && args.length == 1)
			radiusKill(sender, null);
		else if(args[0].equalsIgnoreCase("radiuskill") && args.length >= 2)
			radiusKill(sender, args[1]);
		else
		 	listSubCmd(sender, label);
		return true;
	}

	public void killAll(CommandSender sender) {
		int witherKillCount = 0;
		for(World world : Bukkit.getWorlds())
			for(Entity entity : world.getEntities())
				if(entity.getType().equals(EntityType.WITHER)) {
					Bukkit.getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> removeWitherInfo(entity.getUniqueId()));
					entity.remove();
					witherKillCount++;
				}
		sender.sendMessage(Messages.WITHER_REMOVED.toString() + ChatColor.RED + witherKillCount);
	}
	
	private void listSubCmd(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.GOLD + "WitherSpawn Commands:");
		sender.sendMessage(ChatColor.GRAY + "/" + label + " list");
		sender.sendMessage(ChatColor.GRAY + "/" + label + " killall");
		sender.sendMessage(ChatColor.GRAY + "/" + label + " radiuskill");
		sender.sendMessage(ChatColor.GRAY + "/" + label + " radiuskill #");
		sender.sendMessage(ChatColor.GRAY + "/" + label + " reload");
		sender.sendMessage(ChatColor.GRAY + "/" + label + " toggle");
	}

	private void listWithers(CommandSender sender) {
		sender.sendMessage(Messages.WITHER_LIST.toString());
		if(Events.witherList.size() < 1)
			sender.sendMessage(Messages.NO_WITHER_FOUND.toString());
		else {
			Events.witherList.forEach((k,v) -> v.forEach((location, time) -> {
				int blockX = location.getBlockX();
				int blockY = location.getBlockY();
				int blockZ = location.getBlockZ();
				String world;
				if(location.getWorld().getName() != null)
					world = location.getWorld().getName();
				else
					world = "unknown world";
				sender.sendMessage(ChatColor.GREEN + k.toString() + " " + Messages.LOCATION_MESSAGE + ChatColor.GREEN + blockX + ChatColor.GOLD + " / " + ChatColor.GREEN + blockY + ChatColor.GOLD + " / " + ChatColor.GREEN + blockZ + ChatColor.GOLD + " in " + ChatColor.GREEN + world + ChatColor.GOLD + " (" + time + ")");
			}));
			sender.sendMessage(Messages.WITHER_FOUND.toString() + ChatColor.GREEN + Events.witherList.size());
		}
	}

	private void radiusKill(CommandSender sender, String arg1) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.CONSOLE_CANNOT_KILLRADIUS.toString());
			return;
		}
		int argRadius = -1;
		if(arg1 != null) {
			try {
				argRadius = Integer.parseInt(arg1);
				if(argRadius <= 0) {
					sender.sendMessage((Messages.RADIUS_INVALID.toString()));
					return;
				}
			} catch (final NumberFormatException e) {
				sender.sendMessage(Messages.RADIUS_EXCEPTION.toString());
				return;
			}
		}
		if(argRadius == -1)
			argRadius = WitherSpawn.getPlugin().killRadius;
		int finalRadius = 0;
		int witherKillCount = 0;
		Player player = (Player) sender;
		World world = player.getWorld();
		for(Entity entity : world.getEntities())
			if(entity.getType().equals(EntityType.WITHER))
				if(arg1 == null && player.getWorld().equals(entity.getWorld()) && entity.getLocation().distance(player.getLocation()) <= WitherSpawn.getPlugin().killRadius) {
					Bukkit.getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> removeWitherInfo(entity.getUniqueId()));
					entity.remove();
					witherKillCount++;
					finalRadius = WitherSpawn.getPlugin().killRadius;
				} else if(arg1 != null && player.getWorld().equals(entity.getWorld()) && entity.getLocation().distance(player.getLocation()) <= argRadius) {
					Bukkit.getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> removeWitherInfo(entity.getUniqueId()));
					entity.remove();
					witherKillCount++;
					finalRadius = argRadius;
				}
		if(witherKillCount > 0)
			sender.sendMessage(ChatColor.RED + String.valueOf(witherKillCount) + " " + Messages.WITHER_RADIUS_REMOVED + ChatColor.GREEN + finalRadius);
		else if(arg1 == null)
			sender.sendMessage(Messages.NO_WITHER_RADIUS + " " + ChatColor.GREEN + WitherSpawn.getPlugin().killRadius);
		else
			sender.sendMessage(Messages.NO_WITHER_RADIUS + " " + ChatColor.GREEN + argRadius);
	}
	private void reloadConfigFile(CommandSender sender) {
		WitherSpawn.getPlugin().witherReloadConfig();
		sender.sendMessage(Messages.CONFIG_RELOADED.toString());
	}

	private void toggleWither(CommandSender sender) {
		if(WitherSpawn.getPlugin().checkWitherToggle()) {
			WitherSpawn.getPlugin().setWitherToggle(false);
			sender.sendMessage(Messages.WITHER_DISABLED.toString());
		} else {
			WitherSpawn.getPlugin().setWitherToggle(true);
			sender.sendMessage(Messages.WITHER_ENABLED.toString());
		}
	}

	private void removeWitherInfo(UUID entityUUID) {
			PreparedStatement stmt1 = null;
			try {
				stmt1 = WitherSpawn.getConnection().prepareStatement("DELETE FROM entityData WHERE uuid = ?;");
				stmt1.setString(1, entityUUID.toString());
				stmt1.execute();
				Events.witherList.remove(entityUUID);
			} catch (Exception e) { e.printStackTrace(); }
			finally {
				try {
					if(stmt1 != null)
						stmt1.close();
				} catch (Exception e) { e.printStackTrace(); }
			}
	}
}
