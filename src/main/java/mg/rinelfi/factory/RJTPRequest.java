/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg.rinelfi.factory;

import mg.rinelfi.jiosocket.client.PseudoWebClient;
import org.json.JSONObject;

/**
 *
 * @author rinelfi
 */
public class RJTPRequest {
    private static final RJTPRequest factory;
    
    static {
        factory = new RJTPRequest();
    }
    
    private RJTPRequest() {}
    
    public static RJTPRequest getInstance() {
        return RJTPRequest.factory;
    }

    public PseudoWebClient openConnection() {
        return new PseudoWebClient().setHost("localhost").setPort(2045).setData(new JSONObject());
    }
}
