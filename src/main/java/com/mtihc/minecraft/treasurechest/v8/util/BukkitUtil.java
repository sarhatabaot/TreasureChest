package com.mtihc.minecraft.treasurechest.v8.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.mtihc.minecraft.treasurechest.v8.util.commands.CommandException;

public final class BukkitUtil {
	//TODO Error here, see T.I. mail
	public static OfflinePlayer findOfflinePlayer(String playerName) throws CommandException {
		OfflinePlayer p;
		//has played before
		p = hasPlayedBefore(playerName);
		/*
		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		for (OfflinePlayer offlinePlayer : offlinePlayers) {
			if (offlinePlayer.getName().equalsIgnoreCase(playerName)) {
				if (p != null) {
					throw new CommandException("Found multiple players named \"" + playerName + "\". Try using a UUID instead.");
				}
				p = offlinePlayer;
			}
		}*/
		//hasn't played before
		if (p == null || !p.hasPlayedBefore()) {
			p = getOfflinePlayerFromString(playerName);
		}
		/*
		if (p == null || !p.hasPlayedBefore()) {
			try {
				//UUID puid = p.getUniqueId();
				UUID uuid = UUID.fromString(playerName);
				p = Bukkit.getOfflinePlayer(uuid);
			} catch (IllegalArgumentException e) {
				throw new CommandException("Invalid UUID format \""+playerName+"\"");
			}
		}*/
		if (p == null || !p.hasPlayedBefore()) {
			throw new CommandException ("Player \""+playerName+"\" does not exist.");
		}
		return p;
	}
	private static OfflinePlayer getOfflinePlayerFromString(String playerName) throws CommandException{
			try {
				UUID uuid = UUID.fromString(playerName);
				return Bukkit.getOfflinePlayer(uuid);
			} catch (IllegalArgumentException e) {
				throw new CommandException("Invalid UUID format \""+playerName+"\"");
			}
	}

	private static OfflinePlayer hasPlayedBefore(String playerName) throws CommandException{
		OfflinePlayer p = null;
		//TODO: can be done using Bukkit.getOfflinePlayer(UUID);
		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		for (OfflinePlayer offlinePlayer : offlinePlayers) {
			if (offlinePlayer.getName().equalsIgnoreCase(playerName)) {
				if (p != null) {
					throw new CommandException("Found multiple players named \"" + playerName + "\". Try using a UUID instead.");
				}
				p = offlinePlayer;
			}
		}
		return p;
	}
}
