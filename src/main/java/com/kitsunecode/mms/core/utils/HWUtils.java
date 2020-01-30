package com.kitsunecode.mms.core.utils;

import com.kitsunecode.mms.core.entities.CommandExecutor;
import com.kitsunecode.mms.core.entities.CommandOutput;

import java.util.Collections;

public class HWUtils {

    private static final CommandExecutor EXECUTOR = new CommandExecutor();

    private static int getBatteryPercentageWindows() {
        CommandOutput output = EXECUTOR.executeCommand("powershell",
                Collections.singletonList("((gwmi win32_battery)|% e*g)"), null, null);
        if (output.hasException()) {
            return -1;
        }
        return Integer.parseInt(output.getStdout().trim());
    }

    // Not tested
    private static int getBatteryPercentageLinux() {
        CommandOutput output = EXECUTOR.executeCommand("cat",
                Collections.singletonList("/sys/class/power_supply/BAT1/capacity"), null, null);
        if (output.hasException()) {
            return -1;
        }
        return Integer.parseInt(output.getStdout().trim());
    }

    public static int getBatteryPercentage() {
        return Util.isWindows() ? getBatteryPercentageWindows() : getBatteryPercentageLinux();
    }

}
