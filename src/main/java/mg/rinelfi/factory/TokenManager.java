/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg.rinelfi.factory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rinelfi
 */
public class TokenManager {
    private static final TokenManager factory;
    private Map<String, String> tokens;
    static {
        factory = new TokenManager();
    }
    
    private TokenManager() {
        this.tokens = new HashMap<>();
    }
    
    public static TokenManager getInstance() {
        return TokenManager.factory;
    }
    
    public Map<String, String> getTokens() {
        return this.tokens;
    }
}
