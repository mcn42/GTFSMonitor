/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.gtfsmonitor;

/**
 *
 * @author mnilsen
 */
public enum GtfsFeed {
    NYCT_A_DIV("1",AuthKey.NYCT),
    NYCT_C_LINE("26",AuthKey.NYCT),
    NYCT_NQRW_LINE("16",AuthKey.NYCT),
    NYCT_BD_LINE("16",AuthKey.NYCT),
    NYCT_L_LINE("2",AuthKey.NYCT),
    NYCT_SI_LINE("11",AuthKey.NYCT);
    
    private String feedId;
    private AuthKey authKey;
    
    private GtfsFeed(String feedId,AuthKey ak)
    {
        this.feedId = feedId;
        this.authKey = ak;
    }

    public String getFeedId() {
        return feedId;
    }

    public AuthKey getAuthKey() {
        return authKey;
    }
    
    public static GtfsFeed getGtfsFeedForId(String id)
    {
        GtfsFeed feed = null;
        for(GtfsFeed f:GtfsFeed.values())
        {
            if(f.getFeedId().equals(id))
            {
                feed = f;
                break;
            }
        }
        return feed;
    }
}
