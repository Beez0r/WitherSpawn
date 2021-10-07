package com.minedhype.witherspawn;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import com.minedhype.witherspawn.MetricsLite;

public class WitherSpawn extends JavaPlugin {
	private boolean witherEnabled;
	public List<String> disabled_worlds;
	public List<String> death_reasons;
	public boolean checkPluginEnabled;
	public boolean deathDrop;
	public boolean deathXP;
	public boolean notifyConsole;
	public boolean notifyMessages;
	public boolean noSeaLevel;
	public boolean noWitherDamage;
	public boolean noWitherExplosion;
	public boolean noWitherEffect;
	public boolean playerMessages;
	public boolean preventAnvilDamage;
	public boolean scanForPreviousWithers;
	public boolean spawnedConsole;
	public boolean spawnedNotify;
	public double armor;
	public double armorToughness;
	public double attackDamage;
	public double followRange;
	public double knockbackResistance;
	public double maxHealth;
	public double movementSpeed;
	public int killRadius;
	public int bypassRadius;
	public int denyRadius;
	public int maxWithers;
	public int radius;
	public int scanTimeWithers;
	public int yMin;
	public int yMax;
	private static Connection connection = null;
	private static String chainConnect;
	public static FileConfiguration config;
	File configFile;

	@Override
	public void onEnable() {
		chainConnect = "jdbc:sqlite:"+getDataFolder().getAbsolutePath()+"/data.db";
		this.createConfig();
		try {
			connection = DriverManager.getConnection(chainConnect);
			this.createTables();
		} catch(Exception e) { e.printStackTrace(); }
		getServer().getPluginManager().registerEvents(new Events(), this);
		Objects.requireNonNull(getCommand("witherspawn")).setExecutor(new Commands());
		witherConfigVars();
		Bukkit.getServer().getScheduler().runTask(WitherSpawn.getPlugin(WitherSpawn.class), Events::loadData);
		if(scanForPreviousWithers) {
			int scanTime;
			try {
				scanTime = scanTimeWithers*60*20;
			} catch(Exception e) { scanTime = 6000; }
			if(scanTime > 1000)
				Bukkit.getServer().getScheduler().runTaskTimer(WitherSpawn.getPlugin(WitherSpawn.class), Events::scanForWithers, 100, scanTime);
		}
		MetricsLite metrics = new MetricsLite(this, 9431);
		new UpdateChecker(this, 82618).getVersion(version -> {
			if(!this.getDescription().getVersion().equalsIgnoreCase(version))
				getServer().getConsoleSender().sendMessage(ChatColor.RED + "[WitherSpawn] There is a new update available! - https://www.spigotmc.org/resources/wither-spawn.82618/");
		});
	}

	@Override
	public void onDisable() {
		if(connection != null) {
			try {
				connection.close();
			} catch(SQLException e) { e.printStackTrace(); }
		}
	}

	private void createTables() {
		PreparedStatement[] stmts = new PreparedStatement[] {};
		try {
			stmts = new PreparedStatement[] {
					connection.prepareStatement("CREATE TABLE IF NOT EXISTS entityData(uuid varchar(64) UNIQUE, location varchar(64), time varchar(64));"),
			};
		} catch(Exception e) { e.printStackTrace(); }
		for(PreparedStatement stmt : stmts) {
			try {
				stmt.execute();
				stmt.close();
			} catch(Exception e) { e.printStackTrace(); }
		}
	}

	public static Connection getConnection() {
		checkConnection();
		return connection;
	}

	public static void checkConnection() {
		try {
			if(connection == null || connection.isClosed() || !connection.isValid(0))
				connection = DriverManager.getConnection(chainConnect);
		} catch(Exception e) { e.printStackTrace(); }
	}

	public void createConfig() {
		this.configFile = new File(getDataFolder(), "config.yml");
		if(!this.configFile.exists())
			this.saveDefaultConfig();
		config = new YamlConfiguration();
		try {
			config.load(this.configFile);
			String ver = config.getString("configVersion");
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
				case "1.4":
					config.set("scanForPreviousWithers", true);
					config.set("scanTimeWithers", 5);
					config.set("witherMaxHealth", 300.0);
					config.set("witherArmor", 4.0);
					config.set("witherArmorToughness", 0.0);
					config.set("witherAttackDamage", 2.0);
					config.set("witherFollowRange", 40.0);
					config.set("witherKnockbackResistance", 0.0);
					config.set("witherMovementSpeed", 0.6);
					config.set("witherFound", "&6Total Withers found: ");
				case "1.5":
					config.set("witherAnvil", false);
					config.set("witherDropCancel", false);
					config.set("witherXPCancel", false);
					List<String> deathReasons = Arrays.asList("SUFFOCATION", "DROWNING");
					config.set("death_reasons", deathReasons);
					config.set("witherYCordMin", 0);
					config.set("witherYCordMan", 0);
					config.set("witherYLevel", "&cWither spawning at this Y-Coordinate has been disabled!");
					config.set("witherYLevelPrevented", "&6Wither spawn prevented due to Y-Coordinate at: ");
					config.set("configVersion", 1.6);
					config.save(configFile);
				case "1.6":
					break;
			}
		} catch(IOException | InvalidConfigurationException e) { Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[WitherSpawn] Invalid config.yml file! Please delete file if it exists to regenerate!"); }
	}

	public boolean checkPluginEnabled() { return checkPluginEnabled; }
	public boolean checkWitherToggle() { return witherEnabled; }
	public static WitherSpawn getPlugin() { return WitherSpawn.getPlugin(WitherSpawn.class); }
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
		scanForPreviousWithers = config.getBoolean("scanForPreviousWithers");
		scanTimeWithers = config.getInt("scanTimeWithers");
		armor = config.getDouble("witherArmor");
		armorToughness = config.getDouble("witherArmorToughness");
		attackDamage = config.getDouble("witherAttackDamage");
		followRange = config.getDouble("witherFollowRange");
		knockbackResistance = config.getDouble("witherKnockbackResistance");
		maxHealth = config.getDouble("witherMaxHealth");
		movementSpeed = config.getDouble("witherMovementSpeed");
		deathDrop = config.getBoolean("witherDropCancel");
		deathXP = config.getBoolean("witherXPCancel");
		death_reasons = config.getStringList("death_reasons");
		preventAnvilDamage = config.getBoolean("witherAnvil");
		yMin = config.getInt("witherYCordMin");
		yMax = config.getInt("witherYCordMax");
	}
}
