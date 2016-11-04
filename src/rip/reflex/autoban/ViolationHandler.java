package rip.reflex.autoban;

import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import org.bukkit.event.Listener;

import org.bukkit.plugin.java.JavaPlugin;

import rip.reflex.api.*;
import rip.reflex.autoban.util.Haxor;

import static rip.reflex.autoban.util.ConfigCache.*;

public class ViolationHandler implements Listener {

    private static JavaPlugin plugin;

    ViolationHandler(final JavaPlugin p) {
        Bukkit.getPluginManager().registerEvents(this, p);
        plugin = p;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onReflexViolation(final PlayerReflexViolationEvent e) {
        final Player p = e.getPlayer();
        final Haxor hax = Haxor.get(p);

        final EnumCheckType check = e.getCheckType();
        final int vl = ReflexAPI.getViolations(p, check);

        if (vl >= thresholds.get(check)) {
            hax.addKick();
            ReflexAPI.setViolations(p, check, 0);

            if (hax.kicks() >= KICKS_TO_ACT) {
                // Most of VL events are async, syncing to avoid 'async kick' error
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    for (final String cmd : actions)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor
                                .translateAlternateColorCodes('&', cmd
                                .replace("%Player%", p.getName())));
                }, 1);

                ReflexAutoban.bannedPlayers.put(p, check);
                hax.resetKicks();
            }
        }
    }

    public static JavaPlugin asPlugin() {
        return plugin;
    }

}
