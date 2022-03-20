package auto.protocompilador;

import java.util.HashMap;

public class Token {
    private final String token;
    private final HashMap<String, Integer> lexemas;
    
    Token(String tokenType) {
        token = tokenType;
        lexemas = new HashMap<>();
    }

    public String getToken() {
        return token;
    }

    public HashMap<String, Integer> getLexemas() {
        return lexemas;
    }
    
    public void addLexema(String lexema) {
        if(lexemas.containsKey(lexema)) lexemas.replace(lexema, (lexemas.get(lexema)+1));
	    else lexemas.put(lexema, 1);
    }
}
