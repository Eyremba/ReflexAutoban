package rip.reflex.autoban.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import rip.reflex.autoban.ReflexAutoban;
import rip.reflex.autoban.ViolationHandler;

import static rip.reflex.autoban.util.ConfigCache.*;

public class KicksInvalidate {

    public KicksInvalidate() {
        if (MODE == 0)
            return;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(ViolationHandler.asPlugin(),
                this::inv, 0, KICKS_INVALIDATE_DELAY * 1200);
    }

    public static void invalidate() {
        if (MODE == 1)
            return;

        new KicksInvalidate().inv();
    }

    private void inv() {
        for (final Player p : Bukkit.getOnlinePlayers())
            Haxor.get(p).resetKicks();

        if (LOG_BANS)
            for (final Player p : ReflexAutoban.bannedPlayers.keySet())
                FileHelper.write(ReflexAutoban.writer, p.getName() + "      |       "
                    + ReflexAutoban.bannedPlayers.get(p).toString() + "         (" + KICKS_TO_ACT + ")");

        ReflexAutoban.bannedPlayers.clear();
    }

}
