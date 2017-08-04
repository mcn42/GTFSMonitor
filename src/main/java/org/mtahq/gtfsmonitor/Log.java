/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mtahq.gtfsmonitor;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author michaeln
 */
public class Log {
    private static final Logger log = Logger.getLogger("org.mtahq.gtfsmonitor");
    private static final Logger errorLog = Logger.getLogger("org.mtahq.gtfsmonitor.error");

    private Log() {
    }
    
    static
    {
        configure();
    }
    
    private static void configure()
    {
        log.setLevel(Level.ALL);
        try {
            String path = Utils.getAppProperties().get(AppProperty.LOG_DIRECTORY);
            File dir = new File(path);
            if(!dir.exists())
            {
                log.info(String.format("Creating Log directory at %s",dir.getCanonicalPath()));
                dir.mkdirs();
            }
            FileHandler fh = new FileHandler(path + "/gtfsmon_%g.log",100000,6,true);
            fh.setFormatter(new SimpleFormatter());      
            fh.setLevel(Level.ALL);
            log.addHandler(fh);
        } catch (IOException e) {
            log.log(Level.SEVERE,"Failed to add logging FileHandler",e);
        }
        
        errorLog.setLevel(Level.WARNING);
        errorLog.setUseParentHandlers(false);
        try {
            String path = Utils.getAppProperties().get(AppProperty.LOG_DIRECTORY);
            
            FileHandler fh = new FileHandler(path + "/gtfsmon_error_%g.log",100000,6,true);
            fh.setFormatter(new SimpleFormatter());      
            fh.setLevel(Level.ALL);
            errorLog.addHandler(fh);
        } catch (IOException e) {
            log.log(Level.SEVERE,"Failed to add error logging FileHandler",e);
        }
    }

    public static Logger getLog() {
        return log;
    }

    public static Logger getErrorLog() {
        return errorLog;
    }
    
    
}
