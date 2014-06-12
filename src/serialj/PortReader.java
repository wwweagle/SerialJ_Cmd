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

    public PortReader(String s) {
        this.portName = s;
        dp = new DataParser();
    }

    public void setFileToPath(String pathToFile) {
        dp.setPathToFile(pathToFile);
    }

    public void setUpdater(UI.LogUpdator u) {
        dp.setUpdater(u);
    }

    public void start() {

        (new Thread(dp, "data")).start();
        try {
            serialPort = new SerialPort(portName);
            serialPort.openPort();//Open port
            serialPort.setParams(9600, 8, 1, 0);//Set params
            int mask = SerialPort.MASK_RXCHAR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
        } catch (SerialPortException ex) {
            System.out.println(ex.toString());
        }
    }

    public void stop() {
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            System.out.println(ex.toString());
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
                System.out.println(ex);
            }

        }
    }

}
