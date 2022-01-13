/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg.rinelfi.factory;

import mg.rinelfi.jiosocket.client.SocketClient;

/**
 *
 * @author rinelfi
 */
public class SocketFactory {
    private static final SocketFactory factory;
    private SocketClient socket;
    
    static {
        factory = new SocketFactory();
    }
    
    private SocketFactory() {
        this.socket = new SocketClient("localhost", 2046);
    }
    
    public static SocketFactory getInstance() {
        return SocketFactory.factory;
    }

    public SocketClient getConnection() {
        return socket;
    }
    
}
