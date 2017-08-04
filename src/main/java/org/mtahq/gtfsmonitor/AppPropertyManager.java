/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mtahq.gtfsmonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michaeln
 */
public class AppPropertyManager {

    private final Map<AppProperty, String> props = new HashMap<>();
    private final String filePath;

    public AppPropertyManager(String filePath) {
        this.filePath = filePath;
        this.loadProperties();
        //this.saveProperties();
    }

    public String get(AppProperty propId) {
        String s = this.props.get(propId);
        if (s == null) {
            s = propId.getDefaultValue();
        }
        return s;
    }
    
    public Integer getInt(AppProperty propId)
    {
        String s = this.props.get(propId);
        if (s == null) {
            s = propId.getDefaultValue();
        }
        int i = Integer.parseInt(s);
        return i;
    }
    
    public Long getLong(AppProperty propId)
    {
        String s = this.props.get(propId);
        if (s == null) {
            s = propId.getDefaultValue();
        }
        long i = Long.parseLong(s);
        return i;
    }
    
    public boolean getBoolean(AppProperty propId)
    {
        String s = this.props.get(propId);
        if (s == null) {
            s = propId.getDefaultValue();
        }
        boolean i = Boolean.valueOf(s);
        return i;
    }

    public final void loadProperties() {
        boolean missing = false;
        Properties p = new Properties();
        this.props.clear();
        try {
            p.load(new FileReader(new File(this.filePath)));
            for (AppProperty ap : AppProperty.values()) {
                String val = p.getProperty(ap.toString());
                if (val == null) {
                    val = ap.getDefaultValue();
                    missing = true;
                    Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, String.format("Could not load property '%s', defaulting to '%s'", ap, val));
                }
                this.props.put(ap, val);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, String.format("Could not find properties file to load at '%s'", this.filePath));
            this.saveProperties();
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, String.format("Could not load properties file at '%s'", this.filePath), ex);
        }
        if(missing) this.saveProperties();
    }

    public void saveProperties() {
        Properties p = new Properties();
        for (AppProperty ap : AppProperty.values()) {
            String val = this.props.get(ap);
            if (val == null) {
                val = ap.getDefaultValue();
                Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, String.format("Could not find property '%s' for saving, defaulting to '%s'", ap, val));
            }
            p.put(ap.toString(), val);
        }
        try {
            String cmt = String.format("Properties saved at %s", new Date());
            p.store(new FileWriter(new File(this.filePath)), cmt);
            Logger.getLogger(this.getClass().getCanonicalName()).info(String.format("Properties saved to %s", this.filePath));
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, String.format("Could not save properties file at '%s'", this.filePath), ex);
        }
    }
}
