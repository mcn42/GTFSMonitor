/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.gtfsmonitor;

import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mnilsen
 */
public class Main {
    private static GTFSMonitor gm = null;
    private static String configPath = ".";
    
    public static void main(String[] args)
    {
//        if(args.length != 1)
//        {
//            printMsg();
//            System.exit(-1);
//        }
//        GtfsFeed feed = GtfsFeed.getGtfsFeedForId(args[0]);
//        if(feed == null)
//        {
//            System.out.println(String.format("No Feed was found for ID %s", args[0]));
//            printMsg();
//            System.exit(-1);
//        }
        
        Locale.setDefault(new Locale("en", "US"));
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        Logger.getLogger(Main.class.getCanonicalName()).log(Level.INFO, "Setting Time Zone: {0}", TimeZone.getDefault().getDisplayName());
        
        Utils.config(configPath);
        gm = new GTFSMonitor();
        gm.start();
    }
    
    public static void printMsg()
    {
        System.out.println("One numeric parameter (Feed ID) must be supplied:");
        for(GtfsFeed f: GtfsFeed.values())
        {
            System.out.println(String.format("\t%s -\t%s",f.getFeedId(), f.toString()));
            
        }
    }
    
    static
    {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
    }

    private static class ShutdownThread extends Thread {
        
        public ShutdownThread() {
        }

        @Override
        public void run() {
            System.out.println("Shutting down GTFS Monitor...");
            if(gm != null) gm.stop();
        }
    }
    
}
