package com.minedhype.witherspawn;

public enum Permission {
	WS_ADMIN("witherspawn.admin"),
	WS_BYPASS("witherspawn.bypass"),
	WS_DENY("witherspawn.deny"),
	WS_NOTIFY("witherspawn.notify");
	private final String perm;

	Permission(String perms) {
		this.perm = perms;
	}

	@Override
	public String toString() {
		return perm;
	}
}
