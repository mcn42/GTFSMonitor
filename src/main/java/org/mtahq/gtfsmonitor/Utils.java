/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.gtfsmonitor;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author mnilsen
 */
public class Utils {

    private static AppPropertyManager appProperties = null;
    private static final Timer appTimer = new Timer("AppTimer");
    private static String appDirectory = "";
    
    public static void config(String configPath)
    {
        appProperties = new AppPropertyManager(configPath + "/" + "monitor.properties");
        appDirectory = configPath;
    }

    public static String getAppDirectory() {
        return appDirectory;
    }

    public static AppPropertyManager getAppProperties() {
        return appProperties;
    }
    
    public static int millisToMinutes(long millis)
    {
        return (int)TimeUnit.MILLISECONDS.toMinutes(millis);
    }

    public static Timer getAppTimer() {
        return appTimer;
    }  
    
}
