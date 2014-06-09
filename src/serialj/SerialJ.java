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
public class SerialJ {

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
    // TODO code application logic here
//        SerialLib sl = new SerialLib();
//        String[] portNames = sl.getPortNames();
//        SerialPort sp = new SerialPort(portNames[0]);
//        try {
//            sp.openPort();
//            sp.setParams(9600, 8, 1, 0);
//            for (int i = 0; i < 10; i++) {
//                int count = sp.getInputBufferBytesCount();
//                System.out.println(count);
//                String s = sp.readHexString();
//                System.out.println(s);
//                Thread.sleep(500);
//            }
//            sp.closePort();
//        } catch (SerialPortException | InterruptedException e) {
//            System.out.println(e.toString());
//        }
//    }
    static SerialPort serialPort;

    public static void main(String[] args) {
        serialPort = new SerialPort("COM5");
        try {
            serialPort.openPort();//Open port
            serialPort.setParams(9600, 8, 1, 0);//Set params
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    /*
     * In this class must implement the method serialEvent, through it we learn about 
     * events that happened to our port. But we will not report on all events but only 
     * those that we put in the mask. In this case the arrival of the data and change the 
     * status lines CTS and DSR
     */
    static class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {//If data is available
                if (event.getEventValue() >= 4) {//Check bytes count in the input buffer
                    //Read data, if 10 bytes available 
                    try {
                        String s = serialPort.readHexString();
                        long milli=System.currentTimeMillis();
                        System.out.print(milli);
                        System.out.print(",");
                        System.out.println(s);
                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                    }
                }
            } else if (event.isCTS()) {//If CTS line has changed state
                if (event.getEventValue() == 1) {//If line is ON
                    System.out.println("CTS - ON");
                } else {
                    System.out.println("CTS - OFF");
                }
            } else if (event.isDSR()) {///If DSR line has changed state
                if (event.getEventValue() == 1) {//If line is ON
                    System.out.println("DSR - ON");
                } else {
                    System.out.println("DSR - OFF");
                }
            }
        }
    }

}
