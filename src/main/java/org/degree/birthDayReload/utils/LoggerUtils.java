package org.degree.birthDayReload.utils;

import org.degree.birthDayReload.BirthDayReload;

public class LoggerUtils {
    public static void log(String message) {
        BirthDayReload.getInstance().getLogger().info(message);
    }

    public static void logWarning(String message) {
        BirthDayReload.getInstance().getLogger().warning(message);
    }

    public static void logSevere(String message) {
        BirthDayReload.getInstance().getLogger().severe(message);
    }
}

