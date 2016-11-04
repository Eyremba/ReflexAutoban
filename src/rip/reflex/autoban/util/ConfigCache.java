package rip.reflex.autoban.util;

import rip.reflex.api.EnumCheckType;

import java.util.*;

public class ConfigCache {

    public static final HashMap<EnumCheckType, Integer> thresholds = new HashMap<>();

    public static boolean LOG_BANS;

    public static String LOG_DIRECTORY;

    public static int MODE, KICKS_INVALIDATE_DELAY, KICKS_TO_ACT;

    public static List<String> actions;

}