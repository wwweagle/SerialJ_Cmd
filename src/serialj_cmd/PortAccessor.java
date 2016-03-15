/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialj_cmd;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 *
 * @author Xiaoxing
 */
public class PortAccessor implements PortInterface {

    final private SerialPort serialPort;
    final private String portName;
    private UI.LogUpdator updator;
    private DataParser dp;

    public PortAccessor(String s) {
        this.portName = s;
        serialPort = new SerialPort(s);
    }

    @Override
    public boolean start(UI.LogUpdator u) {
        try {
            this.updator = u;
            serialPort.openPort();//Open port
            dp = new DataParser();
            dp.setUpdater(u);
            (new Thread(dp, "data")).start();
            serialPort.setParams(SerialPort.BAUDRATE_9600, 8, 1, 0);//Set params
            int mask = SerialPort.MASK_RXCHAR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
            return true;

        } catch (SerialPortException ex) {
            updator.updateString(ex.toString() + "\r\n");
            return false;
        }
    }

    @Override
    public void stop() {
        try {
            if (serialPort.isOpened()) {
                serialPort.closePort();
                dp.stop();
            }
        } catch (SerialPortException ex) {
            updator.updateString(ex.toString() + "\r\n");
        }
    }

    @Override
    public boolean writeByte(byte b) {
        try {
            return serialPort.writeByte(b);
        } catch (SerialPortException e) {
            updator.updateString(e.toString() + "\r\n");
            return false;
        }
    }

    class SerialPortReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            try {
                int[] bytes = serialPort.readIntArray();
                if (bytes != null) {
                    dp.put(bytes);
                }
            } catch (SerialPortException ex) {
                updator.updateString(ex.toString() + "\r\n");
            }

        }
    }

}
