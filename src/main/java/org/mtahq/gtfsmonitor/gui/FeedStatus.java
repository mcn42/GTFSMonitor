/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.gtfsmonitor.gui;

import java.util.Objects;
import org.mtahq.gtfsmonitor.GtfsFeed;

/**
 *
 * @author mnilsen
 */
public class FeedStatus implements Comparable<FeedStatus> {
    private GtfsFeed feed;
    private long timestamp = -1;
    private int updateCount = -1;
    private long size = -1;
    private float age = -1.0f;
    private String errorText = null;

    public FeedStatus(GtfsFeed feed, long timestamp, int updateCount, long size, float age) {
        this.feed = feed;
        this.timestamp = timestamp;
        this.updateCount = updateCount;
        this.size = size;
        this.age = age;
    }
    
    public FeedStatus(GtfsFeed feed,String errorText) {
        this.feed = feed;
        this.errorText = errorText;
    }

    public GtfsFeed getFeed() {
        return feed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public long getSize() {
        return size;
    }

    public float getAge() {
        return age;
    }

    public String getErrorText() {
        return errorText;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.feed);
        hash = 17 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FeedStatus other = (FeedStatus) obj;
        if (this.timestamp != other.timestamp) {
            return false;
        }
        if (this.feed != other.feed) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FeedStatus{" + "feed=" + feed + ", timestamp=" + timestamp + ", updateCount=" + updateCount + ", size=" + size + ", age=" + age + ", errorText=" + errorText + '}';
    }

    @Override
    public int compareTo(FeedStatus o) {
        return this.feed.toString().compareTo(o.getFeed().toString());
    }
    
    public FeedState getFeedState()
    {
        if(this.errorText != null) return FeedState.ERROR;
        if(this.age > 120.0f) return FeedState.LATE;
        return FeedState.OK;
    }
    
}
