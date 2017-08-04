/*
 * Copyright Metropolitan Transportation Authority NY
 * All Rights Reserved
 */
package org.mtahq.gtfsmonitor.gui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author mnilsen
 */
public class Throbber {

    private final JLabel label;
    private long updatePeriod = 80L;
    private float minAlpha = 0.2f;
    private float maxAlpha = 1.0f;
    private float alphaStep = 0.02f;
    private float current = maxAlpha;
    private final ImageIcon ii = new ImageIcon(this.getClass().getResource("/blue_ball.png"));
    private AlphaImageIcon icon;
    private boolean increment = false;
    private ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

    public Throbber(JLabel label) {
        this.label = label;

        this.icon = new AlphaImageIcon(ii, this.maxAlpha);
        this.label.setText("");
        this.label.setIcon(icon);
    }

    public void start() {
        this.exec = Executors.newSingleThreadScheduledExecutor();
        this.exec.scheduleAtFixedRate(new Runner(), 0L,this.updatePeriod, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        this.exec.shutdownNow();

        SwingUtilities.invokeLater(() -> {
            increment = false;
            icon = new AlphaImageIcon(ii, maxAlpha);
            label.setIcon(icon);
            label.invalidate();
        });

    }

    private void iterate() {
        float step = this.increment ? this.alphaStep : -this.alphaStep;
        this.current += step;
        if (this.current > this.maxAlpha) {
            this.increment = false;
            this.current = this.maxAlpha;
        } else if (this.current < this.minAlpha) {
            this.increment = true;
            this.current = this.minAlpha;
        }

        //System.out.println("Setting Alpha to " + this.current);
        this.icon = new AlphaImageIcon(ii, this.current);
        this.label.setIcon(icon);
        this.label.invalidate();
    }

    class Runner implements Runnable {

        @Override
        public void run() {
            iterate();
//            try {
//                SwingUtilities.invokeLater(() -> {
//                    iterate();
//                });
//            } catch (Exception ex) {
//                Logger.getLogger(Throbber.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

    }

    public long getUpdatePeriod() {
        return updatePeriod;
    }

    public void setUpdatePeriod(long updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    public float getMinAlpha() {
        return minAlpha;
    }

    public void setMinAlpha(float minAlpha) {
        this.minAlpha = minAlpha;
    }

    public float getMaxAlpha() {
        return maxAlpha;
    }

    public void setMaxAlpha(float maxAlpha) {
        this.maxAlpha = maxAlpha;
    }

    public float getAlphaStep() {
        return alphaStep;
    }

    public void setAlphaStep(float alphaStep) {
        this.alphaStep = alphaStep;
    }

}
