package auto.protocompilador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analizer {
    private String codigo;
    private final Token numero;
    private final Token palabraReservada;
    private final Token operador;
    private final Token delimitador;
    private final Token agrupador;
    private final Token simbolo;
    private final Token identificador;
    private final Token blanksOrUnknown;
    private final ArrayList<String> reservedWords;
    
    /**
     * Inicializa atributos Token
     */
    public Analizer() {
        numero = new Token("NUMERO");
        palabraReservada = new Token("PALABRA RESERVADA");
        operador = new Token("OPERADOR");
        delimitador = new Token("DELIMITADOR");
        agrupador = new Token("AGRUPADOR");
        simbolo = new Token("SIMBOLO");
        identificador = new Token("IDENTIFICADOR");
        blanksOrUnknown = new Token("BLANKS OR UNKNOWN");
        
        reservedWords = new ArrayList<>(Arrays.asList(
                "abstract","assert","boolean","break","byte","case","catch",
                "char","class","const","continue","default","do","double",
                "else","enum","extends","false","final","finally","float",
                "for","goto","if","implements","import","instanceof","int",
                "interface","long","native","new","null","package","private",
                "protected","public","return","short","static","strictfp",
                "super","switch","synchronized","this","throw","throws",
                "transient","true","try","void","volatile","while"
        ));
    }
    
    /**
     * Carga el archivo y lo lee para convertirlo en String
     * @param archivo Archivo seleccionado para abrir
     * @return Un objeto StringBuilder con el codigo fuente
     * @throws FileNotFoundException Si es que el arquivo no se encontro
     * @throws IOException Excepxion al leer el archivo
     */
    public StringBuilder fileCharger(File archivo) throws FileNotFoundException, IOException {
        StringBuilder texto = new StringBuilder();
        
        if (archivo != null) {
            FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);
        
            String s;
            while (true) {
                s = br.readLine();
                if (!(s != null)) break;
                texto.append(s).append("\n");
            }
            
            fr.close();
        }
        
        codigo = new String(texto);
        return texto;
    }
    
    /**
     * Analiza el codigo fuente y lo esquematiza por tokens
     */
    public void tokenLexe() {        
        List<String> lines = codigo.lines().toList();
        System.out.println(lines.size()); // Numero de lienas de codigo
        
        String num = "(\\d+)|(\\d+\".\"\\d+)";
        String operators = "([+-/%==<><=>=\\[]||&&!!=.\\*]+)";
        String groupers = "([{}()]+)";
        String delimeter = "([;,:]+)";
        String blanks = "(\\s*)";
        String specialChar = "([°¬#\\?¡¿'@]+)";
        String identifier = "(([a-zA-Z]+)[$_\\d]*)";
        
        ArrayList<Pattern> patterns = new ArrayList(Arrays.asList(
                Pattern.compile(num),           // 0
                Pattern.compile(operators),     // 1
                Pattern.compile(groupers),      // 2
                Pattern.compile(delimeter),     // 3
                Pattern.compile(blanks),        // 4 Comments, WhiteLines
                Pattern.compile(specialChar),   // 5
                Pattern.compile(identifier)     // 6
                
        ));
        
        topper:
        for (var pat = 0; pat < patterns.size(); ++pat) { // Tipo de TOKEN
            
            for (var row = 0; row < lines.size(); ++row) { // FILA de codigo a revisar
                Matcher mat = patterns.get(pat).matcher(lines.get(row));
                
                while (!mat.hitEnd()) { // POSICIONES disponibles aun de la linea a revisar
                    switch (pat) {
                        case 0 -> {
                            if (mat.find()) {
                                numero.addLexema(mat.group());
                                System.out.println("Numero: " + mat.group());
                            } break;
                        }
                        case 1 -> {
                            if (mat.find()) {
                                operador.addLexema(mat.group());
                                System.out.println("Operador: " + mat.group());
                            } break;
                        }
                        case 2 -> {
                            if (mat.find()) {
                                agrupador.addLexema(mat.group());
                                System.out.println("Agrupador: " + mat.group());
                            } break;
                        }
                        case 3 -> {
                            if (mat.find()) {
                                delimitador.addLexema(mat.group());
                                System.out.println("Delimitador: " + mat.group());
                            } break;
                        }
                        case 4 -> {
                            if (mat.find()) {
                                blanksOrUnknown.addLexema(mat.group());
                                System.out.println("Blanks Or Unknown: " + mat.group());
                            } break;
                        }
                        case 5 -> {
                            if (mat.find()) {
                                simbolo.addLexema(mat.group());
                                System.out.println("Simbolo: " + mat.group());
                            } break;
                        }
                        case 6 -> {
                            if (mat.find()) {
                                if (reservedWords.contains(mat.group())) {
                                    palabraReservada.addLexema(mat.group(0));
                                    System.out.println("P Reservada: " + mat.group(0));
                                    break;
                                } else {
                                    identificador.addLexema(mat.group(0));
                                    System.out.println("Identificador: " + mat.group(0));
                                    break;
                                }
                            } break;
                        }
                    }
                }
            }
        }
    }

    public Token getPalabraReservada() {
        return palabraReservada;
    }

    public Token getOperador() {
        return operador;
    }

    public Token getDelimitador() {
        return delimitador;
    }

    public Token getAgrupador() {
        return agrupador;
    }

    public Token getSimbolo() {
        return simbolo;
    }

    public Token getIdentificador() {
        return identificador;
    }

    public Token getBlanksOrUnknown() {
        return blanksOrUnknown;
    }
    
}
