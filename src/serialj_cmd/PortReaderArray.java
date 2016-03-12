/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialj_cmd;

import java.util.ArrayList;

/**
 *
 * @author Libra
 */
public class PortReaderArray implements PortInterface {

    private PortReader[] array;
    final String[] nameArray;

    public PortReaderArray(String[] nameArray) {
        this.nameArray = nameArray;
    }

    @Override
    public boolean start(UI.LogUpdator u) {
        boolean allStart = true;
        ArrayList<PortReader> pList = new ArrayList<>();
        for (String s : this.nameArray) {
            PortReader r = new PortReader(s);
            if (r.start(u)) {
                pList.add(r);
            } else {
                allStart = false;
            }
        }
        array = pList.toArray(new PortReader[pList.size()]);
        return allStart;
    }

    @Override
    public void stop() {
        for (PortReader r : array) {
            r.stop();
        }
    }

    @Override
    public boolean writeByte(byte b) {
        boolean allWrite=true;
        for (PortReader r : array) {
            if (!r.writeByte(b)) {
                allWrite=false;
            }
        }
        return allWrite;
    }

}
