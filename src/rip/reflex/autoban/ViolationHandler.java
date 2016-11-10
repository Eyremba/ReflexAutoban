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
            final int actualVl = e.actualVl();

            if (((check.equals(EnumCheckType.COMBAT_KILLAURANPC)) || (check.equals(EnumCheckType.COMBAT_FASTSWITCH))
                    || (check.equals(EnumCheckType.NET_PINGSPOOF))) && (actualVl >= thresholds.get(check))) {
                this.followActions(p, check);
                return;
            }

            /* Some checks are too accurate to make
            them wait, we use instant-ban for them */
            if (actualVl >= thresholds.get(check)) {
                hax.addKick();

                if (hax.kicks() >= KICKS_TO_ACT)
                    this.followActions(p, check);
            }
        }, 1);
    }

    private void followActions(final Player p, final EnumCheckType check) {
        for (final String cmd : actions)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor
                .translateAlternateColorCodes('&', cmd
                .replace("%Player%", p.getName())));

        ReflexAutoban.bannedPlayers.put(p, check);
        ReflexAPI.setViolations(p, check, 0);

        Haxor.get(p).resetKicks();
    }

    public static JavaPlugin asPlugin() {
        return plugin;
    }

}
