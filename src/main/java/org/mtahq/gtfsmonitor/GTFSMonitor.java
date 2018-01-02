/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.gtfsmonitor;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.mtahq.gtfsmonitor.gui.FeedStatus;
import org.mtahq.gtfsmonitor.gui.FeedStatusListener;
import org.mtahq.gtfsmonitor.gui.FeedSummary;

/**
 *
 * @author mnilsen
 */
public class GTFSMonitor {
    private static final GTFSMonitor instance = new GTFSMonitor();
    
    private final long pollingPeriod = Utils.getAppProperties().getLong(AppProperty.GTFS_POLLING_PERIOD);
    private ScheduledExecutorService executor = null;
    private final String urlPattern = Utils.getAppProperties().get(AppProperty.BASE_GTFS_URL_PATTERN);

    private Map<GtfsFeed, String> feedUrls = new HashMap<>();

    private DateFormat longFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
    private DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.LONG);

    private Long lastReading = null;
    private Long lastTimestamp = null;
    private List<FeedStatus> feedData = null;
    private Map<String,FeedSummary> feedSummaries = new HashMap<>();
    private final List<FeedStatusListener> listeners = new ArrayList<>();

    private GTFSMonitor() {
        this.buildMap();
    }

    public static GTFSMonitor getInstance() {
        return instance;
    }

    private void buildMap() {
        for (GtfsFeed feed : GtfsFeed.values()) {
            String authKey = feed.getAuthKey().equals(AuthKey.NYCT)
                    ? Utils.getAppProperties().get(AppProperty.GTFS_AUTHORIZATION_KEY_NYCT)
                    : Utils.getAppProperties().get(AppProperty.GTFS_AUTHORIZATION_KEY_MNR);
            String urlStr = String.format(this.urlPattern, authKey, feed.getFeedId());
            this.feedUrls.put(feed, urlStr);
            Log.getLog().info(String.format("Added URL for %s: %s", feed, urlStr));
        }
    }
    
    public void addFeedStatusListener(FeedStatusListener l)
    {
        Log.getLog().info("Adding FeedStatusListener " + l.toString());
        this.listeners.add(l);
    }
    
    public void removeFeedStatusListener(FeedStatusListener l)
    {
        this.listeners.remove(l);
    }

    public void start() {
        Log.getLog().info(String.format("Starting GTFS Monitor for %s feeds", this.feedUrls.size()));

        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.executor.scheduleAtFixedRate(
                new MonitorTask(), 0, pollingPeriod, TimeUnit.MILLISECONDS);

    }

    public void stop() {
        Log.getLog().info(String.format("Stopping GTFS Monitor"));
        if (this.executor != null && !this.executor.isTerminated()) {
            this.executor.shutdownNow();
        }
    }

    private void retrieveData() {
        StringBuilder sb = new StringBuilder();
        this.feedData = new ArrayList<>();
        sb.append("\nFeed\t\tTime\t\tAge\tSize\tUpdates\n");
        for (GtfsFeed feed : GtfsFeed.values()) {
            sb.append(readProtobufData(feed));
        }
        sb.append("-----------------------------------------------------");
        Log.getLog().info(sb.toString());
        Log.getLog().log(Level.INFO, "Event Feed count {0}", this.feedData.size());
        Collections.sort(this.feedData);
        this.listeners.forEach((l) -> {
            l.updateListData(this.feedData);
        });
    }

    private String readProtobufData(GtfsFeed feed) {
        String res = "No retrieval performed";
        FeedStatus fs = null;
        Log.getLog().info(String.format("Retrieving GTFS data for feed %s",feed));
        URL dataUrl = null;
        try {
            String urlStr = this.feedUrls.get(feed);
            try {
                
                dataUrl = new URL(urlStr);
            } catch (MalformedURLException ex) {
                String msg = String.format("%s: Malformed URL '%s'", feed,dataUrl);
                Log.getLog().log(Level.SEVERE, msg, ex);
                fs = new FeedStatus(feed,msg);
                this.feedData.add(fs);
                return "Malformed data URL: " + urlStr + "\n";
            }
            InputStream is = dataUrl.openStream();
            GtfsRealtime.FeedMessage fm = GtfsRealtime.FeedMessage.parseFrom(is);
            this.lastReading = System.currentTimeMillis();

            long posix = fm.getHeader().getTimestamp();
            this.lastTimestamp = posix * 1000;
            Date ts = new Date(this.lastTimestamp);
            long size = fm.getSerializedSize();
            int entityCount = fm.getEntityCount();
            float age = this.getAge(this.lastTimestamp);
            String msg = String.format("%s\t%s\t%s\t%s\t%s\n",feed, timeFormat.format(ts), age, size, entityCount);
            //Log.getLog().info(msg);
            res = msg;
            fs = new FeedStatus(feed,this.lastTimestamp,entityCount,size,age);
            this.feedData.add(fs);
            
//            FeedSummary summ = this.generateFeedSummary(fm,feed.name());
//            Log.getLog().info(summ.toString());
            
        } catch (IOException ex) {
            String msg = String.format("%s - Feed retrieval error: %s\n", feed, ex.getLocalizedMessage());
            Log.getLog().severe(msg);
            Log.getErrorLog().log(Level.SEVERE, "Feed retrieval error", ex);
            fs = new FeedStatus(feed,ex.getLocalizedMessage());
            this.feedData.add(fs);
            return msg;
        }
        
        return res;
    }
    
    private FeedSummary generateFeedSummary(GtfsRealtime.FeedMessage fm,String feedId) {
        List<FeedEntity> list = fm.getEntityList();
        FeedSummary summ = new FeedSummary(feedId);
        list.stream().filter((fe) -> (fe.hasTripUpdate())).forEachOrdered((fe) -> {
            summ.addTripUpdate(fe.getTripUpdate());
        });
        return summ;
    }

    private float getAge(long timestamp) {
        return (System.currentTimeMillis() - timestamp) / 1000.0f;
    }

    public class MonitorTask implements Runnable {

        @Override
        public void run() {
            try {
                retrieveData();
            } catch (Exception ex) {
                Log.getLog().log(Level.SEVERE, "Unhandled exception in monitor thread", ex);
            }
        }

    }
}
