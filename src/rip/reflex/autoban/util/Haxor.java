package rip.reflex.autoban.util;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class Haxor {

    private static final HashMap<Player, Haxor> haxors = new HashMap<>();

    private static int kicks;

    public static Haxor get(final Player player) {
        if (!haxors.containsKey(player))
            haxors.put(player, new Haxor());

        return haxors.get(player);
    }

    public void addKick() {
        kicks += 1;
    }

    public void resetKicks() {
        kicks = 0;
    }

    public int kicks() {
        return kicks;
    }

}
