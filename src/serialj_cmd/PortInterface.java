/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialj_cmd;

/**
 *
 * @author Libra
 */
public interface PortInterface {

    public boolean start(UI.LogUpdator u);

    public void stop();
    
    public boolean writeByte(byte b);
    
}
