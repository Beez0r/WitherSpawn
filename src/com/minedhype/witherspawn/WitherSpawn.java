package com.minedhype.witherspawn;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import com.minedhype.witherspawn.MetricsLite;

public class WitherSpawn extends JavaPlugin {
	private boolean witherEnabled;
	public List<String> disabled_worlds;
	public boolean checkPluginEnabled;
	public boolean notifyConsole;
	public boolean notifyMessages;
	public boolean noSeaLevel;
	public boolean noWitherDamage;
	public boolean noWitherExplosion;
	public boolean noWitherEffect;
	public boolean playerMessages;
	public boolean spawnedConsole;
	public boolean spawnedNotify;
	public int killRadius;
	public int bypassRadius;
	public int denyRadius;
	public int maxWithers;
	public int radius;
	public static FileConfiguration config;
	File configFile;

	@Override
	public void onEnable() {
		this.createConfig();
		getServer().getPluginManager().registerEvents(new Events(), this);
		Objects.requireNonNull(getCommand("witherspawn")).setExecutor(new Commands());
		Bukkit.getServer().getScheduler().runTaskAsynchronously(WitherSpawn.getPlugin(WitherSpawn.class), this::getAllWithers);
		witherConfigVars();
		MetricsLite metrics = new MetricsLite(this, 9431);
		new UpdateChecker(this, 82618).getVersion(version -> {
			if(!this.getDescription().getVersion().equalsIgnoreCase(version))
				getServer().getConsoleSender().sendMessage(ChatColor.RED + "[WitherSpawn] There is a new update available! - https://www.spigotmc.org/resources/wither-spawn.82618/");
		});
	}

	public void createConfig() {
		this.configFile = new File(getDataFolder(), "config.yml");
		if(!this.configFile.exists())
			this.saveDefaultConfig();

		config = new YamlConfiguration();

		try {
			config.load(this.configFile);
			String ver = config.getDouble("configVersion")+"";
			switch(ver) {
				case "1.0":
					config.set("locationMessage","&6Location XYZ: ");
					config.set("pluginDisabled","&6WitherSpawn plugin is &cDISABLED&6!");
					config.set("pluginEnabled","&6WitherSpawn plugin is &aENABLED&6!");
					config.set("witherDisabled","&6Wither spawning has been &cDISABLED&6!");
					config.set("witherEnabled","&6Wither spawning has been &aENABLED&6!");
					config.set("witherPreventedAboveSeaLevel","&6Wither spawn prevented above sea level at: ");
					config.set("witherPreventDisabled","&6Wither spawn prevented because max Withers is 0 at: ");
					config.set("witherPreventedLocation","&6Wither spawn prevented because disabled at: ");
					config.set("witherPreventedMaxLimit","&6Wither spawn prevented due to max limit at: ");
					config.set("witherPreventedWorld","&6Wither spawn prevented in disabled world: ");
					config.set("witherRemoved","&6Total Withers removed: ");
				case "1.1":
					config.set("denyRadius", 5);
					config.set("killRadius", 25);
					config.set("consoleNoKillRadius","&cCannot kill Withers in radius of console!");
					config.set("invalidRadius","&cWither kill radius must be an integer greater than 0!");
					config.set("noWitherRadius","&cNo Withers found to kill within radius of");
					config.set("radiusException","&cYou did not enter a valid kill radius integer!");
					config.set("witherPreventedPermission","&6Wither spawn prevented due to deny permission at: ");
					config.set("witherRadiusRemoved","&6total Withers have been removed within radius of ");
				case "1.2":
					config.set("sendConsoleMessages", true);
					config.set("sendSpawnMessagesConsole", true);
					config.set("sendSpawnMessagesNotify", true);
					config.set("noWitherDamage", false);
					config.set("noWitherExplosion", false);
					config.set("noWitherEffect", false);
					config.set("configReloaded","&aWitherSpawn configuration file has been reloaded!");
					config.set("witherSpawned","&6Wither has been spawned at: ");
				case "1.3":
					config.set("initialToggleEnabled", true);
					config.set("configVersion", 1.4);
					config.save(configFile);
				case "1.4":
					break;
			}
		} catch(IOException | InvalidConfigurationException e) { Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[WitherSpawn] Invalid config.yml file! Please delete file if it exists to regenerate!"); }
	}

	public boolean checkPluginEnabled() { return checkPluginEnabled; }

	public boolean checkWitherToggle() { return witherEnabled; }

	private void getAllWithers() {
		for(World world : Bukkit.getWorlds())
			for(Entity entity : world.getEntities())
				if(entity.getType().equals(EntityType.WITHER))
					Events.spawnedWithers.put(entity, entity.getUniqueId());
	}

	public static WitherSpawn getPlugin() {
		return WitherSpawn.getPlugin(WitherSpawn.class);
	}

	public void setWitherToggle(Boolean setToggle) { witherEnabled = setToggle; }

	public void witherReloadConfig() {
		super.reloadConfig();
		saveDefaultConfig();
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		witherConfigVars();
	}

	public void witherConfigVars() {
		disabled_worlds = config.getStringList("disabled_worlds");
		checkPluginEnabled = config.getBoolean("enable");
		witherEnabled = config.getBoolean("initialToggleEnabled");
		killRadius = config.getInt("killRadius");
		notifyConsole = config.getBoolean("sendConsoleMessages");
		notifyMessages = config.getBoolean("sendNotifyMessages");
		noSeaLevel = config.getBoolean("noWithersAboveSeaLevelOverworld");
		noWitherDamage = config.getBoolean("noWitherDamage");
		noWitherExplosion = config.getBoolean("noWitherExplosion");
		noWitherEffect = config.getBoolean("noWitherEffect");
		playerMessages = config.getBoolean("sendPlayerMessages");
		spawnedConsole = config.getBoolean("sendSpawnMessagesConsole");
		spawnedNotify = config.getBoolean("sendSpawnMessagesNotify");
		bypassRadius = config.getInt("bypassRadius");
		denyRadius = config.getInt("denyRadius");
		maxWithers = config.getInt("maxWithers");
		radius = config.getInt("radius");
	}
}
