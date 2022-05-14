package auto.protocompilador;

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

import java.util.ArrayList;
import java.util.HashMap;

public class TablaSemController {

    private ObservableList<ObservableList<String>> data;

    @FXML
    private TableView<ObservableList<StringProperty>> tabla;

    @FXML
    private Button tablaOn;

    @FXML
    private void tablaOnPressed() {
        Token t3 = App.analizer.getIdentificador();
        ArrayList<String[]> vars = App.analizer.getVariables();
        ArrayList<String[]> singleVars = new ArrayList<>();
        HashMap<String, Integer> hm = t3.getLexemas();

        for (var lexema : hm.keySet()) {
            vars.forEach(p -> {
                if (p[0].equals(lexema)) {
                    p[3] = String.valueOf(hm.get(lexema));
                }
            });
        }

        vars.forEach(v -> {
            if (!v[0].equals("") || !v[2].equals("")) {
                if (!v[1].equals("System")) singleVars.add(v);
            }
        });

        for (var v : singleVars) {
            data.add(FXCollections.observableArrayList()); // Agrega una tupla

            int index = data.size() - 1;
            data.get(index).add(v[0]);  // Agrega el IDENTIFICADOR
            data.get(index).add(v[1]);  // Agrega el TIPO de variable
            data.get(index).add(v[2]);  // Agrega la linea del 1° USO
            data.get(index).add(v[3]);  // Agrega la CANTIDAD de veces
            data.get(index).add(v[4]);  // Agrega si la variable es declarada CORRETAMENTE
        }

        fillTable(data);
    }

    private void fillTable(ObservableList<ObservableList<String>> info) {
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Crear y agregar columnas a la tabla
        ObservableList<String> names = info.remove(0);
        for (var i = 0; i < names.size(); ++i) {
            int indice = i;
            TableColumn<ObservableList<StringProperty>, String> col = new TableColumn<>(names.get(i));
            col.setCellValueFactory(celda -> celda.getValue().get(indice));
            col.setEditable(false);
            col.setStyle("-fx-alignment: center; -fx-font-weight: bold;");
            tabla.getColumns().add(col);
        }

        // Agregar los datos a la tabla
        for (var fila : info) {
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
        data.get(0).add("VARIABLE");
        data.get(0).add("TIPO");
        data.get(0).add("1° USO");
        data.get(0).add("CANTIDAD");
        data.get(0).add("DECLARACIÓN");

        var label = new Label("Presione botón \"Mostrar datos\"");
        label.setStyle("-fx-font-family: \"Comic Sans MS\"; -fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: white;");
        tabla.setPlaceholder(label);
        tabla.setBorder(Border.EMPTY);
    }

}