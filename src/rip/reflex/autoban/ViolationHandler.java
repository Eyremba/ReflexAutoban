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
        if (e.isCancelled())
            return;

        /* Amount of violations is updating async after VL event,
        therefore we need to wait some unless it's up-to-date */
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            final Player p = e.getPlayer();
            final Haxor hax = Haxor.get(p);

            final EnumCheckType check = e.getCheckType();

            if (e.actualVl() >= thresholds.get(check)) {
                hax.addKick();
                ReflexAPI.setViolations(p, check, 0);

                if (hax.kicks() >= KICKS_TO_ACT) {
                    for (final String cmd : actions)
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor
                            .translateAlternateColorCodes('&', cmd
                            .replace("%Player%", p.getName())));

                    ReflexAutoban.bannedPlayers.put(p, check);
                    hax.resetKicks();
                }
            }
        }, 1);
    }

    public static JavaPlugin asPlugin() {
        return plugin;
    }

}
