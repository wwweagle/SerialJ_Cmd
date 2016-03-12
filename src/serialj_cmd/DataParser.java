/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialj_cmd;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Xiaoxing
 */
public class DataParser implements Runnable {

    private boolean stop;
    final private BlockingQueue<int[]> q;
    final private ArrayList<int[]> eventList;
    private int[] currEvent;
    private int currIdx;
    final private int[] errorEvent = {0x0, 0xFFFF, 0xFFFF, 0xFFFF, 0xFFFF};
    private int lastWriteTime;
    private UI.LogUpdator updater;

    public DataParser() {
        this.lastWriteTime = 0;
        this.stop = false;
        this.q = new LinkedBlockingQueue<>();
        this.eventList = new ArrayList<>();
        this.currEvent = new int[5];
        this.currIdx = 0;

    }

    public void setUpdater(UI.LogUpdator updater) {
        this.updater = updater;
    }

    private int getIntTime() {
        long timeL = System.currentTimeMillis() & 0x7FFFFFFF;
        return (int) timeL;
    }

    private void process(int[] e) {
        for (int i : e) {
            if (currIdx > 4) {
                currEvent = new int[5];
                currIdx = 1;
            }
            switch (i) {
                case 0x55:
                    currEvent = new int[5];
                    currEvent[1] = 0x55;
                    currIdx = 1;
                    break;
                case 0xAA:
                    if (currIdx == 4) {
                        currEvent[4] = 0xAA;
                        updateEvents(currEvent);
                    } else {
                        updateEvents(errorEvent);
                        currIdx = 4;
                    }

                    break;
                case 0x00:
                    if (currIdx == 1) {
                        currEvent = new int[5];
                        currEvent[1] = 0x00;
                    } else {
                        currEvent[currIdx] = 0x00;
                    }
                    break;
                case 0x03:
                    if (currIdx == 4) {
                        currEvent[4] = 0x03;
                        updateEvents(currEvent);
                    } else {
                        currEvent[currIdx] = 0x03;
                    }
                    break;
                default:
                    currEvent[currIdx] = i;
            }
            currIdx++;
        }

    }

    private void updateEvents(int[] event) {
        event[0] = getIntTime();
        eventList.add(event);
        updater.updateEvent(event);

        if (event[0] > lastWriteTime + 60000) {
            lastWriteTime = event[0];
        }
    }

    public void put(int[] in) {

        try {
            q.put(in);
        } catch (InterruptedException ex) {
            updater.updateString(ex.toString());
        }
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                int[] e = q.take();
                process(e);

            } catch (InterruptedException ex) {
                Logger.getLogger(DataParser.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }


    public void stop() {
        stop = true;
    }

}
