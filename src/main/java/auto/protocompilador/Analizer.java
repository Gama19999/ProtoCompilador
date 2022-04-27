package auto.protocompilador;

import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase que realiza la lectura del codigo fuente cargado ademas de realizar:<br>
 * <br> Analisis Lexico: {tokenLexe}
 * <br> Analisis Sintactico: {controlStructuresAnalizer}
 * <br> Analisis Semantico
 */
public class Analizer {
    private final Token numero;
    private final Token palabraReservada;
    private final Token operador;
    private final Token delimitador;
    private final Token agrupador;
    private final Token simbolo;
    private final Token identificador;
    private final Token blanksOrUnknown;
    private final ArrayList<String> reservedWords;

    // CODIGO
    private String codigo;
    private final ArrayList<String> lineasSinComentarios;
    private final ArrayList<String> codigoEnLineas;

    // PILA DE AGRUPADORES
    private final Stack<Character> groupers = new Stack<>();
    
    /**
     * Inicializa atributos Token y clase Analizer
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
        
        lineasSinComentarios = new ArrayList<>();
        codigoEnLineas = new ArrayList<>();
    }
    
    /**
     * Carga el archivo y lo lee para convertirlo en String ademas de guardarlo para su lectura
     * @param archivo Archivo seleccionado para abrir
     * @return Un objeto ObservableList con el codigo fuente por lineas
     * @throws FileNotFoundException Si es que el arquivo no se encontro
     * @throws IOException Excepxion al leer el archivo
     */
    public ObservableList<ObservableList<String>> fileCharger(File archivo) throws FileNotFoundException, IOException {
        codigo = null;
        lineasSinComentarios.clear();
        codigoEnLineas.clear();

        StringBuilder texto = new StringBuilder();
        ObservableList<ObservableList<String>> lineasCodigo = FXCollections.observableArrayList(); // OBSArrL

        lineasCodigo.add(FXCollections.observableArrayList()); // OBSArrL como FILA Encabezados
        lineasCodigo.get(0).add("#");
        lineasCodigo.get(0).add("CÓDIGO FUENTE");
        
        if (archivo != null) {
            FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);
        
            String s;
            while (true) {
                s = br.readLine();
                if (s == null) break;
                texto.append(s).append("\n");

                codigoEnLineas.add(s);

                // Agregando datos
                lineasCodigo.add(FXCollections.observableArrayList()); // Nueva fila DATOS

                var indice = lineasCodigo.size()-1;
                lineasCodigo.get(indice).add(String.valueOf(indice)); // Numero de fila
                lineasCodigo.get(indice).add(s); // Contenido de la fila
            }
            
            fr.close();
        }
        
        codigo = new String(texto);
        quitarComments();
        return lineasCodigo;
    }

    /**
     * Metodo que analiza el codigo fuente cargado y elimina los comentarios de este<p>
     * Parte del analisis LEXICO
     * </p>
     */
    private void quitarComments() {
        List<String> lineas = codigo.lines().toList();
	    StringBuilder linTemp;
        
        int posI, posF;
        
        for (var i = 0; i < lineas.size(); ++i) {
	    
	        linTemp = new StringBuilder();

            if (lineas.get(i).contains("//")) {
                posI = lineas.get(i).indexOf("//");
                lineasSinComentarios.add(lineas.get(i).substring(0, posI));
            } else if (lineas.get(i).contains("/*")) {
                posI = lineas.get(i).indexOf("/*");
		        linTemp.append(lineas.get(i).substring(0, posI));

                while (!lineas.get(i).contains("*/")) ++i;
                
                posF = lineas.get(i).indexOf("*/");
                linTemp.append(lineas.get(i).substring(posF+2, lineas.get(i).length()));
		        lineasSinComentarios.add(linTemp.toString());
            } else if (lineas.get(i).contains("*/")) {
                posF = lineas.get(i).indexOf("*/");
		        linTemp.append("\n").append(lineas.get(i).substring(posF+2, lineas.get(i).length()));
                lineasSinComentarios.add(linTemp.toString());
            } else {
                lineasSinComentarios.add(lineas.get(i));
            }
        }

        /* Quita espacios en blanco
        for (var x : lineasSinComentarios) {
            lineasSinEspaciosComentarios.add(x.replaceAll("\\s*", ""));
        }*/
    }
    
    /**
     * Analiza el codigo fuente y lo esquematiza por tokens<p>ANÁLISIS SINTÁCTICO</p>
     */
    public void tokenLexe() {
        System.out.println(lineasSinComentarios.size()); // Numero de lienas de codigo
        
        String num = "(\\d+)|(\\d+\".\"\\d+)";
        String operators = "([\\*+-/%==<><=>=\\[]||&&!!=.]+)";
        String groupers = "([{}()]+)";
        String delimeter = "([;,:]+)";
        String blanks = "(\\s*)";
        String specialChar = "([°¬#\\?¡¿'@]+)";
        String identifier = "(([a-zA-Z]+)[$_\\d]*)";
        
        ArrayList<Pattern> patterns = new ArrayList<>(Arrays.asList(
                Pattern.compile(num),           // 0
                Pattern.compile(operators),     // 1
                Pattern.compile(groupers),      // 2
                Pattern.compile(delimeter),     // 3
                Pattern.compile(blanks),        // 4 WhiteLines
                Pattern.compile(specialChar),   // 5
                Pattern.compile(identifier)     // 6
                
        ));
        
        for (var pat = 0; pat < patterns.size(); ++pat) { // Tipo de TOKEN
            
            for (var row = 0; row < lineasSinComentarios.size(); ++row) { // FILA de codigo a revisar
                Matcher mat = patterns.get(pat).matcher(lineasSinComentarios.get(row));
                
                while (!mat.hitEnd()) { // POSICIONES disponibles aun de la linea a revisar
                    switch (pat) {
                        case 0 -> {
                            if (mat.find()) {
                                numero.addLexema(mat.group());
                            }
                        }
                        case 1 -> {
                            if (mat.find()) {
                                operador.addLexema(mat.group());
                            }
                        }
                        case 2 -> {
                            if (mat.find()) {
                                agrupador.addLexema(mat.group());
                            }
                        }
                        case 3 -> {
                            if (mat.find()) {
                                delimitador.addLexema(mat.group());
                            }
                        }
                        case 4 -> {
                            if (mat.find()) {
                                blanksOrUnknown.addLexema(mat.group());
                            }
                        }
                        case 5 -> {
                            if (mat.find()) {
                                simbolo.addLexema(mat.group());
                            }
                        }
                        case 6 -> {
                            if (mat.find()) {
                                if (reservedWords.contains(mat.group())) {
                                    palabraReservada.addLexema(mat.group(0));
                                } else {
                                    identificador.addLexema(mat.group(0));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Analiza el codigo fuente y verifica la integridad de las estructuras de control<p>"if","else","for","while","switch","do"</p>
     * @return ArrayList de cadenas con los posibles errores en el codigo y la linea de estos
     */
    public ArrayList<String> controlStructuresAnalizer() {
        ArrayList<String> control = new ArrayList<>(Arrays.asList("if","for","while","switch"));
        Stack<String> pila = new Stack<>();
        ArrayList<String> errores = new ArrayList<>();

        var palabra = "";
        int[] lineaPalabra = {0};

        outter:
        for (var fila : lineasSinComentarios) {
            for (var keyword : control) { // Comprueba una PALABRA DE CONTROL
                if (palabra.equals("")) {
                    if (fila.contains(keyword)) {
                        pila.push(keyword); // La AGREGA a la pila
                        palabra = keyword;
                        System.out.println("Push: "+keyword);
                        codigoEnLineas.forEach(l -> {
                            if (l.contains(fila)) lineaPalabra[0] = codigoEnLineas.indexOf(l)+1;
                        });
                        break;
                    }
                } /*else {
                    var x = controlStructuresAnalizer();
                    errores.addAll(x);
                }*/
            }

            switch (palabra) {
                case "do" -> {
                    for (var pos = 0; pos < fila.length(); ++pos) {
                        if (Lists.charactersOf(fila).get(pos) == '{') {
                            pila.push("{");
                            System.out.println("Push: {");
                        } else if (Lists.charactersOf(fila).get(pos) == '}') {
                            if (pila.peek().equals("{")) {
                                pila.pop();
                                System.out.println("Pop: }");
                                if (fila.contains("} while (")) continue outter;
                                else {
                                    errores.add("Error: 'while faltante' en la línea -> " + lineaPalabra[0]);
                                }
                            } else {
                                var error = new StringBuilder();
                                error.append("Error: '{' en la línea -> ");
                                codigoEnLineas.forEach(l -> {
                                    if (l.contains(fila)) error.append(codigoEnLineas.indexOf(l)+1);
                                });
                                errores.add(error.toString());
                            }
                        }
                    }
                }
                case "else" -> {

                }
                case "if","for","while","switch" -> {
                    for (var pos = 0; pos < fila.length(); ++pos) {
                        if (Lists.charactersOf(fila).get(pos) == '(') {
                            pila.push("(");
                            System.out.println("Push: (");
                        }
                        else if (Lists.charactersOf(fila).get(pos) == ')') {
                            if (pila.peek().equals("(")) {
                                pila.pop();
                                System.out.println("Pop: )");
                            } else {
                                var error = new StringBuilder();
                                error.append("Error: '(' en la línea -> ");
                                codigoEnLineas.forEach(l -> {
                                    if (l.contains(fila)) error.append(codigoEnLineas.indexOf(l)+1);
                                });
                                errores.add(error.toString());
                            }
                        } else if (Lists.charactersOf(fila).get(pos) == '{') {
                            pila.push("{");
                            System.out.println("Push: {");
                        }
                        else if (Lists.charactersOf(fila).get(pos) == '}') {
                            if (pila.peek().equals("{")) {
                                pila.pop();
                                System.out.println("Pop: }");
                            } else {
                                var error = new StringBuilder();
                                error.append("Error: '{' en la línea -> ");
                                codigoEnLineas.forEach(l -> {
                                    if (l.contains(fila)) error.append(codigoEnLineas.indexOf(l)+1);
                                });
                                errores.add(error.toString());
                            }
                        }
                    }
                }
                default -> System.out.println("termino un bloque");
            }

            if (!pila.isEmpty()) {
                if (pila.peek().equals(palabra)) {
                    pila.pop();
                    palabra = "";
                    System.out.println("Pop: "+palabra);
                } else if (pila.peek().equals("{")) {
                    continue outter;
                } else {
                    errores.add("Error: '" + pila.peek() + "' en la línea -> " + lineaPalabra[0]);
                }
            }

        }

        return errores;
    }

    /**
     * Verifica que las LLAVES o PARENTESIS sean CORRESPONDIENTES
     * @param index Caracter en un indice especifico de la linea
     * @param row Entero que representa la linea de codigo que se lee
     * @return Cadena o NULL con el posible error
     */
    private String indexGrouper(char index, int row) {
        var error = "Error: '" + index + "' en la línea -> ";

        // Verifica si HAY una LLAVE ó PARENTESIS de ABRIR
        if (index == '{' || index == '(') {
            groupers.push(index); // Agrega el caracter a la pila
        } else if (index == '}' || index == ')') { // Verifica si HAY una LLAVE ó PARENTESIS de CERRAR
            if (groupers.isEmpty()) { // Verifica la pila vacia que es ERROR
                return error + row;
            } else {
                char topGroupers = groupers.peek(); // Obtiene el agrupador MAS RECIENTE de la pila
                // Compara que sean CORRESPONDIENTES
                if ((index == '}' && topGroupers == '{') || (index == ')' && topGroupers == '(')) {
                    groupers.pop(); // Elimina ese agrupador de la pila
                } else {
                    return error + row;
                }
            }
        }
        return null;
    }

    /**
     * Analiza el codigo fuente y verifica la integridad de las estructuras de control<p>"if","else","for","while","switch","do"</p>
     * @return ArrayList de cadenas con los posibles errores en el codigo y la linea de estos
     */
    public ArrayList<String> controlSTAnalyzer() {
        ArrayList<String> errors = new ArrayList<>();

        for (var row : lineasSinComentarios) {
            for (var index : Lists.charactersOf(row)) {
                var res = indexGrouper(index, codigoEnLineas.indexOf(row));
                if (!Objects.isNull(res)) {
                    errors.add(res);
                }
            }

            /*
            // Verifica si es una ESTRUCTURA de CONTROL (if, for, while, switch)
            if (row.contains("if")) {
                if (!(row.charAt(row.indexOf("if")+2) == '(')) errors.add("Error: '(' en la línea -> " + lineasSinComentarios.indexOf(row));
            } else if (row.contains("for")) {
                if (!(row.charAt(row.indexOf("for")+3) == '(')) errors.add("Error: '(' en la línea -> " + lineasSinComentarios.indexOf(row));
            } else if (row.contains("while")) {
                if (!(row.charAt(row.indexOf("while")+5) == '(')) errors.add("Error: '(' en la línea -> " + lineasSinComentarios.indexOf(row));
            } else if (row.contains("switch")) {
                if (!(row.charAt(row.indexOf("switch")+6) == '(')) errors.add("Error: '(' en la línea -> " + lineasSinComentarios.indexOf(row));
            } else if (row.contains("do")) {
                if (!(row.charAt(row.indexOf("do")+2) == '{')) errors.add("Error: '{' en la línea -> " + lineasSinComentarios.indexOf(row));
            } else if (row.contains("else")) {
                // if (!(row.charAt(row.indexOf("if")+2) == '(')) errors.add("Error: '(' en la línea -> " + lineasSinComentarios.indexOf(row));
            }*/
        }

        return errors;
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
