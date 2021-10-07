package com.minedhype.witherspawn;

import java.util.Objects;
import org.bukkit.ChatColor;

public enum Messages {
	CONFIG_RELOADED("configReloaded"),
	CONSOLE_CANNOT_KILLRADIUS("consoleNoKillRadius"),
	LOCATION_MESSAGE("locationMessage"),
	NO_PERMISSION("noPermission"),
	NO_WITHER_FOUND("noWitherFound"),
	NO_WITHER_RADIUS("noWitherRadius"),
	PLUGIN_DISABLED("pluginDisabled"),
	RADIUS_EXCEPTION("radiusException"),
	RADIUS_INVALID("invalidRadius"),
	WITHER_DISABLED("witherDisabled"),
	WITHER_DISABLED_ABOVE_SEALEVEL("disabledAboveSeaLevel"),
	WITHER_DISABLED_WORLD("disabledWorld"),
	WITHER_ENABLED("witherEnabled"),
	WITHER_LIST("witherList"),
	WITHER_MAX_LIMIT_REACHED("maxLimitReached"),
	WITHER_PREVENTED_ABOVE_SEA_LEVEL("witherPreventedAboveSeaLevel"),
	WITHER_PREVENTED_DISABLED("witherPreventDisabled"),
	WITHER_PREVENTED_LOCATION("witherPreventedLocation"),
	WITHER_PREVENTED_MAX_LIMIT("witherPreventedMaxLimit"),
	WITHER_PREVENTED_PERMISSION("witherPreventedPermission"),
	WITHER_PREVENTED_WORLD("witherPreventedWorld"),
	WITHER_RADIUS_REMOVED("witherRadiusRemoved"),
	WITHER_REMOVED("witherRemoved"),
	WITHER_SPAWNED("witherSpawned"),
	WITHER_FOUND("witherFound"),
	WITHER_YLEVEL("witherYLevel"),
	WITHER_YLEVEL_PREVENTED("witherYLevelPrevented");
	private final String msg;

	Messages(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		String messages = WitherSpawn.config.getString(msg);
		return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(messages));
	}
}
