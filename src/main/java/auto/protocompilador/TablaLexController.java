package auto.protocompilador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Border;

public class TablaLexController {
    private ObservableList<ObservableList<String>> data;
    
    @FXML
    private Button tablaOn;
    
    @FXML
    private TableView<ObservableList<StringProperty>> tabla;
    
    @FXML
    private void tablaOnPressed() {
        App.analizer.tokenLexe(); // HACE EL ANALISIS LEXICO ---- AAAAAAAAAA! QUE FEOOOOOOO!
        Token t1 = App.analizer.getAgrupador();
        Token t2 = App.analizer.getDelimitador();
        Token t3 = App.analizer.getIdentificador();
        Token t4 = App.analizer.getOperador();
        Token t5 = App.analizer.getPalabraReservada();
        Token t6 = App.analizer.getSimbolo();
        List<Token> l = new ArrayList<>(Arrays.asList(t1,t2,t3,t4,t5,t6));
        
        System.out.println("CREANDO TABLA");
        HashMap<String, Integer> hm;

        for (var x = 0; x < 6; ++x) {
            hm = l.get(x).getLexemas(); // Obtiene el HashMap con los LEXEMAS de un tipo de TOKEN
            
            for (var p : hm.keySet()) {
                data.add(FXCollections.observableArrayList()); // Agrega una tupla
                
                int index = data.size() - 1;
                data.get(index).add(p);                     // Agrega el LEXEMA
                data.get(index).add(l.get(x).getToken());   // Agrega el tipo de TOKEN
                data.get(index).add(hm.get(p).toString());  // Agrega la CANTIDAD de veces
            }            
        }
        
        fillTable(data);
    }
    
    private void fillTable(ObservableList<ObservableList<String>> data) {
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        // Crear y agregar columnas a la tabla
        ObservableList<String> names = data.remove(0);
        for (var i = 0; i < names.size(); ++i) {
            int indice = i;
            TableColumn<ObservableList<StringProperty>, String> col = new TableColumn<>(names.get(i));
            col.setCellValueFactory(celda -> celda.getValue().get(indice));
            col.setEditable(false);
            col.setStyle("-fx-alignment: center; -fx-font-weight: bold;");
            tabla.getColumns().add(col);
        }
        
        // Agregar los datos a la tabla
        for (var fila : data) {
            ObservableList<StringProperty> tuplas = FXCollections.observableArrayList();
            for (var cell : fila) {
                tuplas.add(new SimpleStringProperty(cell));
            }
            tabla.getItems().add(tuplas);
        }
        
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    @FXML
    void initialize() {
        data = FXCollections.observableArrayList();
        
        data.add(FXCollections.observableArrayList());
        data.get(0).add("LEXEMA");
        data.get(0).add("TOKEN");
        data.get(0).add("CANTIDAD");

        var label = new Label("Presione botón \"Mostrar datos\"");
        label.setStyle("-fx-font-family: \"Comic Sans MS\"; -fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: white;");
        tabla.setPlaceholder(label);
        tabla.setBorder(Border.EMPTY);
    }
}
