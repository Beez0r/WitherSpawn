package com.minedhype.witherspawn;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Events implements Listener {
	public static final ConcurrentHashMap<Entity, UUID> spawnedWithers = new ConcurrentHashMap<>();

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER) && WitherSpawn.getPlugin().checkPluginEnabled()) {
			for(Player player:Bukkit.getServer().getOnlinePlayers()) {
				if(player.hasPermission(Permission.WS_BYPASS.toString()) && player.getWorld().equals(event.getLocation().getWorld()) && event.getLocation().distance(player.getLocation()) <= WitherSpawn.getPlugin().bypassRadius) {
					Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> {
						spawnedWithers.put(event.getEntity(), event.getEntity().getUniqueId());
					if(WitherSpawn.getPlugin().spawnedConsole)
						Bukkit.getConsoleSender().sendMessage(Messages.WITHER_SPAWNED.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName());
					if(WitherSpawn.getPlugin().spawnedNotify)
						Bukkit.broadcast(Messages.WITHER_SPAWNED.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName(), Permission.WS_NOTIFY.toString());
					});
					return;
				}
				if(!player.isOp())
					if(player.hasPermission(Permission.WS_DENY.toString()) && player.getWorld().equals(event.getLocation().getWorld()) && event.getLocation().distance(player.getLocation()) <= WitherSpawn.getPlugin().denyRadius) {
						event.setCancelled(true);
						Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> {
						if(WitherSpawn.getPlugin().notifyMessages)
							Bukkit.broadcast(Messages.WITHER_PREVENTED_PERMISSION.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName(), Permission.WS_NOTIFY.toString());
						if(WitherSpawn.getPlugin().notifyConsole)
							Bukkit.getConsoleSender().sendMessage(Messages.WITHER_PREVENTED_PERMISSION.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName());
						if(WitherSpawn.getPlugin().playerMessages)
							player.sendMessage(Messages.WITHER_DISABLED.toString());
						});
						return;
				}
			}

			if(!WitherSpawn.getPlugin().checkWitherToggle()) {
				event.setCancelled(true);
				Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> {
					if(WitherSpawn.getPlugin().notifyMessages)
						Bukkit.broadcast(Messages.WITHER_PREVENTED_LOCATION.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName(), Permission.WS_NOTIFY.toString());
					if(WitherSpawn.getPlugin().notifyConsole)
						Bukkit.getConsoleSender().sendMessage(Messages.WITHER_PREVENTED_LOCATION.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName());
					for(Player player : Bukkit.getServer().getOnlinePlayers())
						if(WitherSpawn.getPlugin().playerMessages && player.getWorld().equals(event.getLocation().getWorld()) && event.getLocation().distance(player.getLocation()) <= WitherSpawn.getPlugin().radius)
							player.sendMessage(Messages.WITHER_DISABLED.toString());
				});
				return;
			}

			for(String checkWorld : WitherSpawn.getPlugin().disabled_worlds) {
				if(checkWorld.equals(Objects.requireNonNull(event.getLocation().getWorld()).getName())) {
					event.setCancelled(true);
					Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> {
						if(WitherSpawn.getPlugin().notifyMessages)
							Bukkit.broadcast(Messages.WITHER_PREVENTED_WORLD.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName(), Permission.WS_NOTIFY.toString());
						if(WitherSpawn.getPlugin().notifyConsole)
							Bukkit.getConsoleSender().sendMessage(Messages.WITHER_PREVENTED_WORLD.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName());
						for(Player player : Bukkit.getServer().getOnlinePlayers())
							if(WitherSpawn.getPlugin().playerMessages && player.getWorld().equals(event.getLocation().getWorld()) && event.getLocation().distance(player.getLocation()) <= WitherSpawn.getPlugin().radius)
								player.sendMessage(Messages.WITHER_DISABLED_WORLD.toString());
					});
					return;
				}
			}

			if(WitherSpawn.getPlugin().noSeaLevel) {
				if(Objects.requireNonNull(event.getLocation().getWorld()).getName().equalsIgnoreCase("world") && event.getLocation().getBlockY() >= 62) {
					event.setCancelled(true);
					Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> {
						if(WitherSpawn.getPlugin().notifyMessages)
							Bukkit.broadcast(Messages.WITHER_PREVENTED_ABOVE_SEA_LEVEL.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName(), Permission.WS_NOTIFY.toString());
						if(WitherSpawn.getPlugin().notifyConsole)
							Bukkit.getConsoleSender().sendMessage(Messages.WITHER_PREVENTED_ABOVE_SEA_LEVEL.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName());
						for(Player player : Bukkit.getServer().getOnlinePlayers())
							if(WitherSpawn.getPlugin().playerMessages && player.getWorld().equals(event.getLocation().getWorld()) && event.getLocation().distance(player.getLocation()) <= WitherSpawn.getPlugin().radius)
								player.sendMessage(Messages.WITHER_DISABLED_ABOVE_SEALEVEL.toString());
					});
					return;
				}
			}

			if(spawnedWithers.size() >= WitherSpawn.getPlugin().maxWithers) {
				event.setCancelled(true);
				Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> {
					if(WitherSpawn.getPlugin().notifyMessages)
						if(WitherSpawn.getPlugin().maxWithers > 0)
							Bukkit.broadcast(Messages.WITHER_PREVENTED_MAX_LIMIT.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName(), Permission.WS_NOTIFY.toString());
						else
							Bukkit.broadcast(Messages.WITHER_PREVENTED_DISABLED.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName(), Permission.WS_NOTIFY.toString());
					if(WitherSpawn.getPlugin().notifyConsole)
						if(WitherSpawn.getPlugin().maxWithers > 0)
							Bukkit.getConsoleSender().sendMessage(Messages.WITHER_PREVENTED_MAX_LIMIT.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName());
						else
							Bukkit.getConsoleSender().sendMessage(Messages.WITHER_PREVENTED_DISABLED.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName());
					for(Player player : Bukkit.getServer().getOnlinePlayers())
						if(WitherSpawn.getPlugin().playerMessages && player.getWorld().equals(event.getLocation().getWorld()) && event.getLocation().distance(player.getLocation()) <= WitherSpawn.getPlugin().radius)
							if(WitherSpawn.getPlugin().maxWithers <= 0)
								player.sendMessage(Messages.WITHER_DISABLED.toString());
							else
								player.sendMessage(Messages.WITHER_MAX_LIMIT_REACHED.toString());
				});
				return;
			}

			Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> {
				spawnedWithers.put(event.getEntity(), event.getEntity().getUniqueId());
				if(WitherSpawn.getPlugin().spawnedConsole)
					Bukkit.getConsoleSender().sendMessage(Messages.WITHER_SPAWNED.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName());
				if(WitherSpawn.getPlugin().spawnedNotify)
					Bukkit.broadcast(Messages.WITHER_SPAWNED.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName(), Permission.WS_NOTIFY.toString());
			});
		}
	}

	@EventHandler
	public void WitherDamage(EntityChangeBlockEvent event) {
		if(event.getEntityType().equals(EntityType.WITHER) && WitherSpawn.getPlugin().noWitherDamage)
			event.setCancelled(true);
	}

	@EventHandler
	public void WitherEffect(EntityDamageEvent event) {
		if(event.getCause().equals(DamageCause.WITHER) && WitherSpawn.getPlugin().noWitherEffect)
			event.setCancelled(true);
	}

	@EventHandler
	public void WitherExplosion(EntityExplodeEvent event) {
		if(event.getEntityType().equals(EntityType.WITHER)) {
			if(WitherSpawn.getPlugin().noWitherDamage)
				event.blockList().clear();
			if(WitherSpawn.getPlugin().noWitherExplosion)
				event.setCancelled(true);
		}

		if(event.getEntityType().equals(EntityType.WITHER_SKULL)) {
			if(WitherSpawn.getPlugin().noWitherDamage)
				event.blockList().clear();
			if(WitherSpawn.getPlugin().noWitherExplosion)
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void WitherExplode(ExplosionPrimeEvent event) {
		if((event.getEntityType().equals(EntityType.WITHER) || event.getEntityType().equals(EntityType.WITHER_SKULL)) && WitherSpawn.getPlugin().noWitherExplosion)
			event.setCancelled(true);
	}

	@EventHandler
	public void onCreatureDeath(EntityDeathEvent event) {
		if(event.getEntity().getType().equals(EntityType.WITHER))
			if(spawnedWithers.remove(event.getEntity(), event.getEntity().getUniqueId())) ;
			else { spawnedWithers.remove(event.getEntity()); }
	}
}
