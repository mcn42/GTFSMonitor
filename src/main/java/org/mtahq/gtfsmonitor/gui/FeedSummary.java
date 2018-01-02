/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mtahq.gtfsmonitor.gui;

import com.google.transit.realtime.GtfsRealtime.TripDescriptor;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor.ScheduleRelationship;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.mtahq.gtfsmonitor.Log;

/**
 *
 * @author michaeln
 */
public class FeedSummary {

    private String feedId;
    private final Set<String> uniqueStops = new TreeSet<>();
    private final Set<String> uniqueRoutes = new TreeSet<>();
    private int tripUpdateCount = 0;
    private int delayCount = 0;
    private int delaySum = 0;
    private float delayAverage = 0.0f;
    private int direction0Count = 0;
    private int direction1Count = 0;
    private int scheduledCount = 0;

    public FeedSummary(String feedId) {
        this.feedId = feedId;
    }

    public void addTripUpdate(TripUpdate tu) {
        TripDescriptor trip = tu.getTrip();
        this.tripUpdateCount++;
        if (trip.hasRouteId()) {
            this.uniqueRoutes.add(trip.getRouteId());
        }
        if(trip.hasScheduleRelationship()) {
            ScheduleRelationship sr = trip.getScheduleRelationship();
            Log.getLog().info("sr=" + sr.toString());
        }
        
//        if(trip.hasDirectionId()) {
//            
//            if(trip.getDirectionId() == 0) {
//                this.direction0Count++; 
//            } else this.direction1Count++;
//        }
        
//        if (trip.getDirectionId() == 0) {
//            this.direction0Count++;
//        } else {
//            this.direction1Count++;
//        }
//        if (trip.hasScheduleRelationship()) {
//            this.scheduledCount++;
//        }
        List<StopTimeUpdate> list = tu.getStopTimeUpdateList();
        for (StopTimeUpdate stu : list) {
            this.uniqueStops.add(stu.getStopId());
            
            if (stu.hasArrival() && stu.getArrival().hasDelay()) {
                this.delayCount++;
                this.delaySum += stu.getArrival().getDelay();
                this.delayAverage = this.delaySum / (float) this.delayCount;
            }
        }
    }

    public String getFeedId() {
        return feedId;
    }

    public float getDelayAverage() {
        return delayAverage;
    }

    public Set<String> getUniqueStops() {
        return uniqueStops;
    }

    public Set<String> getUniqueRoutes() {
        return uniqueRoutes;
    }

    public int getTripUpdateCount() {
        return tripUpdateCount;
    }

    public int getDelayCount() {
        return delayCount;
    }

    public int getDirection0Count() {
        return direction0Count;
    }

    public int getDirection1Count() {
        return direction1Count;
    }

    public int getScheduledCount() {
        return scheduledCount;
    }

    @Override
    public String toString() {
        return "FeedSummary{" + "feedId=" + feedId + ", uniqueStops=" + Integer.toString(uniqueStops.size()) + ", uniqueRoutes=" + Integer.toString(uniqueRoutes.size()) + ", tripUpdateCount=" + tripUpdateCount + ", delayCount=" + delayCount + ", delaySum=" + delaySum + ", delayAverage=" + delayAverage + ", direction0Count=" + direction0Count + ", direction1Count=" + direction1Count + ", scheduledCount=" + scheduledCount + '}';
    }

}
