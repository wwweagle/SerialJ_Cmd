/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialj;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 *
 * @author Xiaoxing
 */
public class PortReader {

    private SerialPort serialPort;
    final private DataParser dp;
    final private String portName;
    private UI.LogUpdator updator;

    public PortReader(String s) {
        this.portName = s;
        dp = new DataParser();
    }

    public boolean setFileToPath(String pathToFile) {
        return dp.setPathToFile(pathToFile);
    }

    public void setUpdater(UI.LogUpdator u) {
        dp.setUpdater(u);
        this.updator = u;
    }

    public boolean start() {

        (new Thread(dp, "data")).start();
        try {
            serialPort = new SerialPort(portName);
            serialPort.openPort();//Open port
//            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            serialPort.setParams(SerialPort.BAUDRATE_9600, 8, 1, 0);//Set params
            int mask = SerialPort.MASK_RXCHAR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
            return true;
        } catch (SerialPortException ex) {
            updator.updateString(ex.toString());
            return false;
        }
    }

    public void stop() {
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            updator.updateString(ex.toString());
        }
        dp.stop();
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
                updator.updateString(ex.toString());
            }

        }
    }

}
