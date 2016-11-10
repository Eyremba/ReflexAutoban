package rip.reflex.autoban;

import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;

import rip.reflex.api.EnumCheckType;
import rip.reflex.api.ReflexAPI;

import rip.reflex.autoban.util.FileHelper;
import rip.reflex.autoban.util.KicksInvalidate;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;

import static rip.reflex.autoban.util.ConfigCache.*;

@SuppressWarnings ("All")
public class ReflexAutoban extends JavaPlugin {

    public static final HashMap<Player, EnumCheckType> bannedPlayers = new HashMap<>();

    public static FileWriter writer;

    @Override
    public void onEnable() {
        this.getLogger().info("Found Reflex version " + ReflexAPI.reflexVersion() + ".");
        this.saveDefaultConfig();

        new ViolationHandler(this);

        // Cache config shit
        thresholds.put(EnumCheckType.COMBAT_FRONTENTITY, num("Thresholds.FrontEntity"));
        thresholds.put(EnumCheckType.COMBAT_KILLAURACOMBINED, num("Thresholds.KillauraCombined"));
        thresholds.put(EnumCheckType.COMBAT_DIRECTION, num("Thresholds.Direction"));
        thresholds.put(EnumCheckType.COMBAT_FIGHTSPEED, num("Thresholds.FightSpeed"));
        thresholds.put(EnumCheckType.COMBAT_REACH, num("Thresholds.Reach"));
        thresholds.put(EnumCheckType.COMBAT_FASTSWITCH, num("Thresholds.FastSwitch"));
        thresholds.put(EnumCheckType.COMBAT_KILLAURANPC, num("Thresholds.NPC"));
        thresholds.put(EnumCheckType.COMBAT_CRITICALS, num("Thresholds.Criticals"));
        thresholds.put(EnumCheckType.MOVEMENT_VELOCITY, num("Thresholds.Velocity"));
        thresholds.put(EnumCheckType.NET_PINGSPOOF, num("Thresholds.PingSpoof"));

        MODE = num("Mode");
        KICKS_INVALIDATE_DELAY = num("KicksInvalidateDelay");

        KICKS_TO_ACT = num("KicksToAct");

        LOG_BANS = this.getConfig().getBoolean("LogBans");
        LOG_DIRECTORY = this.getConfig().getString("LogDirectory");

        actions = this.getConfig().getStringList("Actions");

        // Path to the file the data should be saved into
        if (LOG_BANS) {
            final String path =
                LOG_DIRECTORY
                + new SimpleDateFormat(
                    "YYYY.MM.dd"
                ).format(
                    new Date(
                        System.currentTimeMillis()
                    )
                ).replace(
                    ".", "-"
                ) + ".log"
            ;

            final File file = new File(path);

            // Creating file and directory if not exists yet
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();

                writer = new FileWriter(file, true);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }

            FileHelper.write(writer, "\n\n\n------------------------------------------------------------------------------\n * ReflexAutoban has been enabled.\n\n");
        }

        new KicksInvalidate();
    }

    @Override
    public void onDisable() {
        KicksInvalidate.invalidate();
        FileHelper.write(writer, "\n\n\n------------------------------------------------------------------------------\n * ReflexAutoban has been disabled.\n\n");
    }

    private int num(final String path) {
        return this.getConfig().getInt(path);
    }

}
