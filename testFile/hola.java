package dot.com;

import java.util.Scanner;

public class Google {
    private final Scanner s = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Ingresa tu nombre");
        String name = s.nextLine();
        System.out.println(name);
        
        ArrayList singleVars = new ArrayList<>();
        ArrayList data = new ArrayList<>();

        for (String[] v : singleVars) {
            data.add(FXCollections.observableArrayList()); // Agrega una tupla

            int index = data.size() - 1;
            data.get(index).add(v[0]);  // Agrega el IDENTIFICADOR
            data.get(index).add(v[1]);  // Agrega el TIPO de variable
            data.get(index).add(v[2]);  // Agrega la linea del 1° USO
            data.get(index).add(v[3]);  // Agrega la CANTIDAD de veces
            data.get(index).add(v[4]);  // Agrega si la variable es declarada CORRETAMENTE
        }
    }
}

