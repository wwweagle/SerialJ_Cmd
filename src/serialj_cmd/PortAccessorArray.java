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
public class PortAccessorArray implements PortInterface {

    private PortAccessor[] array;
    final String[] nameArray;

    public PortAccessorArray(String[] nameArray) {
        this.nameArray = nameArray;
    }

    @Override
    public boolean start(UI.LogUpdator u) {
        boolean allStart = true;
        ArrayList<PortAccessor> pList = new ArrayList<>();
        for (String s : this.nameArray) {
            PortAccessor r = new PortAccessor(s);
            
            if (r.start(u)) {
                pList.add(r);
            } else {
                allStart = false;
            }
        }
        array = pList.toArray(new PortAccessor[pList.size()]);
        return allStart;
    }

    @Override
    public void stop() {
        for (PortAccessor r : array) {
            r.stop();
        }
    }

    @Override
    public boolean writeByte(byte b) {
        boolean allWrite=true;
        for (PortAccessor r : array) {
            if (!r.writeByte(b)) {
                allWrite=false;
            }
        }
        return allWrite;
    }

}
