package com.minedhype.witherspawn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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
	public static final ConcurrentHashMap<UUID, HashMap<Location, String>> witherList = new ConcurrentHashMap<>();

	@EventHandler
	public void onWitherSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER) && WitherSpawn.getPlugin().checkPluginEnabled()) {
			final LivingEntity ent = event.getEntity();
			for(Player player:Bukkit.getServer().getOnlinePlayers()) {
				if(player.hasPermission(Permission.WS_BYPASS.toString()) && player.getWorld().equals(event.getLocation().getWorld()) && event.getLocation().distance(player.getLocation()) <= WitherSpawn.getPlugin().bypassRadius) {
					Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> {
						addWitherInfo(ent.getLocation(), ent.getUniqueId());
					if(WitherSpawn.getPlugin().spawnedConsole)
						Bukkit.getConsoleSender().sendMessage(Messages.WITHER_SPAWNED.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName());
					if(WitherSpawn.getPlugin().spawnedNotify)
						Bukkit.broadcast(Messages.WITHER_SPAWNED.toString() + ChatColor.GREEN + event.getLocation().getBlockX() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockY() + ChatColor.GOLD + " / " + ChatColor.GREEN + event.getLocation().getBlockZ() + ChatColor.GOLD + " in " + ChatColor.GREEN + Objects.requireNonNull(event.getLocation().getWorld()).getName(), Permission.WS_NOTIFY.toString());
					});
					if(WitherSpawn.getPlugin().armor > 0.0 && WitherSpawn.getPlugin().armor != 4.0)
						ent.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(WitherSpawn.getPlugin().armor);
					if(WitherSpawn.getPlugin().armorToughness > 0.0)
						ent.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(WitherSpawn.getPlugin().armorToughness);
					if(WitherSpawn.getPlugin().attackDamage > 0.0 && WitherSpawn.getPlugin().attackDamage != 2.0)
						ent.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(WitherSpawn.getPlugin().attackDamage);
					if(WitherSpawn.getPlugin().followRange > 0.0 && WitherSpawn.getPlugin().followRange != 40.0)
						ent.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(WitherSpawn.getPlugin().followRange);
					if(WitherSpawn.getPlugin().knockbackResistance > 0.0)
						ent.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(WitherSpawn.getPlugin().knockbackResistance);
					if(WitherSpawn.getPlugin().movementSpeed > 0.0 && WitherSpawn.getPlugin().maxHealth != 300.0)
						ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(WitherSpawn.getPlugin().movementSpeed);
					if(WitherSpawn.getPlugin().movementSpeed > 0.0 && WitherSpawn.getPlugin().movementSpeed != 0.6)
						ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(WitherSpawn.getPlugin().movementSpeed);
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
			int currentAliveWithers = 0;
			for(World world : Bukkit.getWorlds())
				for(Entity entity : world.getEntities())
					if(entity.getType().equals(EntityType.WITHER))
						currentAliveWithers++;
			if(currentAliveWithers >= WitherSpawn.getPlugin().maxWithers) {
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
			if(WitherSpawn.getPlugin().armor > 0.0 && WitherSpawn.getPlugin().armor != 4.0)
				ent.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(WitherSpawn.getPlugin().armor);
			if(WitherSpawn.getPlugin().armorToughness > 0.0)
				ent.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(WitherSpawn.getPlugin().armorToughness);
			if(WitherSpawn.getPlugin().attackDamage > 0.0 && WitherSpawn.getPlugin().attackDamage != 2.0)
				ent.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(WitherSpawn.getPlugin().attackDamage);
			if(WitherSpawn.getPlugin().followRange > 0.0 && WitherSpawn.getPlugin().followRange != 40.0)
				ent.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(WitherSpawn.getPlugin().followRange);
			if(WitherSpawn.getPlugin().knockbackResistance > 0.0)
				ent.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(WitherSpawn.getPlugin().knockbackResistance);
			if(WitherSpawn.getPlugin().movementSpeed > 0.0 && WitherSpawn.getPlugin().maxHealth != 300.0)
				ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(WitherSpawn.getPlugin().movementSpeed);
			if(WitherSpawn.getPlugin().movementSpeed > 0.0 && WitherSpawn.getPlugin().movementSpeed != 0.6)
				ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(WitherSpawn.getPlugin().movementSpeed);
			Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> {
				addWitherInfo(ent.getLocation(), ent.getUniqueId());
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
	public void WitherDeath(EntityDeathEvent event) {
		if(event.getEntity().getType().equals(EntityType.WITHER))
			Bukkit.getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> removeWitherInfo(event.getEntity().getUniqueId()));
	}

	public static void loadData() {
		PreparedStatement loadData = null;
		try {
			loadData = WitherSpawn.getConnection().prepareStatement("SELECT uuid, location, time FROM entityData;");
			ResultSet dataStore = loadData.executeQuery();
			while(dataStore.next()) {
				UUID entityUUID = UUID.fromString(dataStore.getString(1));
				String[] locationRaw = dataStore.getString(2).split(";");
				int x = Integer.parseInt(locationRaw[0]);
				int y = Integer.parseInt(locationRaw[1]);
				int z = Integer.parseInt(locationRaw[2]);
				World world = Bukkit.getWorld(locationRaw[3]);
				if(world == null)
					continue;
				Location location = new Location(world, x, y, z);
				String timestamp = dataStore.getString(3);
				HashMap<Location, String> witherInfo = new HashMap<>();
				witherInfo.put(location, timestamp);
				witherList.put(entityUUID, witherInfo);
			}
		} catch(Exception e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[WitherSpawn] Failed to load database properly! Shutting down to prevent data corruption.");
			Bukkit.shutdown();
		} finally {
			try {
				if(loadData != null)
					loadData.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

	public static void addWitherInfo(Location location, UUID entityUUID) {
			final Calendar calendar = Calendar.getInstance();
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
			String timestamp = simpleDateFormat.format(calendar.getTime());
			PreparedStatement stmt = null;
			try {
				stmt = WitherSpawn.getConnection().prepareStatement("INSERT OR REPLACE INTO entityData (uuid, location, time) VALUES (?,?,?);");
				stmt.setString(1, entityUUID.toString());
				String locationRaw = location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ()+";"+ location.getWorld().getName();
				stmt.setString(2, locationRaw);
				stmt.setString(3, timestamp);
				stmt.executeUpdate();
				HashMap<Location, String> witherInfo = new HashMap<>();
				witherInfo.put(location, timestamp);
				witherList.put(entityUUID, witherInfo);
			} catch (Exception e) { e.printStackTrace(); }
			finally {
				try {
					if(stmt != null)
						stmt.close();
				} catch (Exception e) { e.printStackTrace(); }
			}
	}

	private void removeWitherInfo(UUID entityUUID) {
		PreparedStatement stmt1 = null;
		try {
			stmt1 = WitherSpawn.getConnection().prepareStatement("DELETE FROM entityData WHERE uuid = ?;");
			stmt1.setString(1, entityUUID.toString());
			stmt1.execute();
			witherList.remove(entityUUID);
		} catch (Exception e) { e.printStackTrace(); }
		finally {
			try {
				if(stmt1 != null)
					stmt1.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

	public static void scanForWithers() {
		for(World world : Bukkit.getWorlds())
			for(Entity entity : world.getEntities())
				if(entity.getType().equals(EntityType.WITHER))
					Bukkit.getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(), () -> addWitherInfo(entity.getLocation(), entity.getUniqueId()));
	}
}
