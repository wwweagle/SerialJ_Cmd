/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialj_cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Libra
 */
public class ScriptExecutor implements Runnable {

    final private PortInterface p;
    final private ScheduledExecutorService ses;
    final private File f;
    final private Object waitLock = new Object();
    private boolean waiting;
    private boolean stop = false;

    public ScriptExecutor(File f, PortInterface p) {
        this.p = p;
        ses = new ScheduledThreadPoolExecutor(1);
        this.f = f;
    }

    @Override
    public void run() {
        if ((!f.exists()) || f.isDirectory() || f.length() == 0 || f.length() > 32767) {
            return;
        }
        StringBuilder sum = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String s;
            while ((s = br.readLine()) != null) {
                sum.append(s.trim());
            }
        } catch (IOException eio) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, eio);
        }
        try {

            p.writeByte((byte) 0x2a);
            ses.schedule(new WakeMe(), 2, TimeUnit.SECONDS);
            waitMe();
            for (int i = 0; i < sum.length() && !stop; i++) {

                switch (sum.charAt(i)) {
                    case '0':
                        p.writeByte((byte) 0x30);
                        break;
                    case '1':
                        p.writeByte((byte) 0x31);
                        break;
                    case '2':
                        p.writeByte((byte) 0x32);
                        break;
                    case '3':
                        p.writeByte((byte) 0x33);
                        break;
                    case '4':
                        p.writeByte((byte) 0x34);
                        break;
                    case '5':
                        p.writeByte((byte) 0x35);
                        break;
                    case '6':
                        p.writeByte((byte) 0x36);
                        break;
                    case '7':
                        p.writeByte((byte) 0x37);
                        break;
                    case '8':
                        p.writeByte((byte) 0x38);
                        break;
                    case '9':
                        p.writeByte((byte) 0x39);
                        break;
                    case 'r':
                    case 'R':
                        p.writeByte((byte) 0x2a);
                        ses.schedule(new WakeMe(), 2000, TimeUnit.MILLISECONDS);
                        waitMe();
                        break;
                    case 's':
                    case 'S':
                        ses.schedule(new WakeMe(), 1000, TimeUnit.MILLISECONDS);
                        waitMe();
                        break;
                    case 'm':
                    case 'M':
                        ses.schedule(new WakeMe(), 60, TimeUnit.SECONDS);
                        waitMe();
                        break;
                    case 'h':
                    case 'H':
                        ses.schedule(new WakeMe(), 60, TimeUnit.MINUTES);
                        waitMe();
                        break;

                }
                ses.schedule(new WakeMe(), 1000, TimeUnit.MILLISECONDS);
                waitMe();

            }
        } catch (Exception e) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void stop() {
        this.stop = true;
        (new WakeMe()).run();
    }

    private void waitMe() {
        waiting = true;
        synchronized (waitLock) {
            while (waiting) {
                try {
                    waitLock.wait();
                } catch (InterruptedException e) {
                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    class WakeMe implements Runnable {

        @Override
        public void run() {
            waiting = false;
            synchronized (waitLock) {
                waitLock.notify();
            }
        }
    }
}
