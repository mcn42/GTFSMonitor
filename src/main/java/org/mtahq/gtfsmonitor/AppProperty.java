/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mtahq.gtfsmonitor;

/**
 *
 * @author michaeln
 */
public enum AppProperty {
    BASE_GTFS_URL_PATTERN("http://datamine.mta.info/mta_esi.php?key=%s&feed_id=%s"),
    GTFS_POLLING_PERIOD("30000"),
    GTFS_AUTHORIZATION_KEY_NYCT("<Insert valid auth key here>"),
    GTFS_AUTHORIZATION_KEY_MNR(""),
    LOG_DIRECTORY("logs");
    
    private final String defaultValue;
    
    private AppProperty(String defaultVal)
    {
        this.defaultValue = defaultVal;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
