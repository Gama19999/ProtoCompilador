package auto.protocompilador;

import java.io.*;
import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class InicioController {
    private FileChooser fileChooser;

    @FXML
    private Button cargar;
    @FXML
    private Button lexico;
    @FXML
    private Button sintactico;
    @FXML
    private Button semantico;
    @FXML
    private Button guardar;

    @FXML
    private TextArea textErrores;

    @FXML
    private TableView<ObservableList<StringProperty>> tabla;
    
    @FXML
    private void cargarPressed() throws IOException {
        textErrores.setVisible(false);
        fileChooser.setTitle("Buscar archivo de codigo fuente");
        
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos Java", "*.java"),
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File archivo = fileChooser.showOpenDialog(cargar.getScene().getWindow());
        
        var lineasCodigo = App.analizer.fileCharger(archivo);
        fillTable(lineasCodigo);
    }
    
    @FXML
    private void lexicoPressed() throws IOException {
        textErrores.setVisible(false);

        Stage stage = new Stage();
        Scene scene = new Scene(App.loadFXML("tablaLex"), 590, 600);

        stage.getIcons().add(new Image("https://cdn-icons-png.flaticon.com/512/1179/1179782.png"));
        stage.setTitle("Analizador Lexico");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void sintacticoPressed() {
        var errores = App.analizer.controlStructuresAnalizer();
        var cadenaErr = new StringBuilder();

        cadenaErr.append("    # ERRORES ENCONTRADOS #").append("\n\n");
        for (var i : errores) {
            cadenaErr.append("#").append(i).append("\n");
        }

        textErrores.setText(cadenaErr.toString());
        textErrores.setVisible(true);
    }

    @FXML
    private void semanticoPressed() {

    }

    @FXML
    private void guardarPressed() throws IOException {
        textErrores.setVisible(false);

        fileChooser.setTitle("Guardar archivo de código fuente");
        File modifiedFile = fileChooser.showSaveDialog(guardar.getScene().getWindow());

        if (modifiedFile != null) {
            FileWriter fw = new FileWriter(modifiedFile, false);
            BufferedWriter bw = new BufferedWriter(fw);

            for (var fila : tabla.getItems()) {
                bw.write((fila.get(1).getValue() + "\n"));
            }

            bw.close();
        }

        var lineasCodigo = App.analizer.fileCharger(modifiedFile);
        fillTable(lineasCodigo);
    }

    /**
     * Agrega la informacion a la tabla
     * @param infoToShow ObservableList que contien la informacion a mostrar
     */
    private void fillTable(ObservableList<ObservableList<String>> infoToShow) {
        tabla.getItems().clear();
        tabla.getColumns().clear();
        tabla.refresh();
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Crear y agregar columnas a la tabla
        ObservableList<String> encabezados = infoToShow.remove(0); // Encabezado
        for (var i = 0; i < encabezados.size(); ++i) {
            int indice = i;

            TableColumn<ObservableList<StringProperty>, String> col = new TableColumn<>(encabezados.get(i));
            col.setCellValueFactory(celda -> celda.getValue().get(indice));
            col.setCellFactory(TextFieldTableCell.forTableColumn());

            if (i == 0) {
                col.setStyle("-fx-font-weight: bold;");
                col.setPrefWidth(35);
                col.setEditable(false);
            } else {
                col.setStyle("-fx-alignment: center-left; -fx-text-alignment: left;");
                col.setPrefWidth(1000);
                col.setEditable(true);
            }
            col.setResizable(false);
            col.setReorderable(false);

            tabla.getColumns().add(col);
        }

        // Agregar los datos a la tabla
        for (var fila : infoToShow) {
            ObservableList<StringProperty> tuplas = FXCollections.observableArrayList();
            for (var cell : fila) {
                tuplas.add(new SimpleStringProperty(cell));
            }
            tabla.getItems().add(tuplas);
        }

        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Permite editar una celda de la tabla
     */
    private void setTableEditable() {
        tabla.setEditable(true);
        tabla.getSelectionModel().cellSelectionEnabledProperty().set(true);

        tabla.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                editFocusedCell();
            }
        });

        tabla.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                tabla.getSelectionModel().clearSelection();
            }
        });
    }

    /**
     * Puntualmente edita una celda de la tabla
     */
    private void editFocusedCell() {
        TablePosition<ObservableList<StringProperty>, String> focusedCell = tabla.focusModelProperty().get().focusedCellProperty().get();
        tabla.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }
    
    @FXML
    void initialize() {
        fileChooser = new FileChooser();

        var label = new Label("Cargue un archivo válido");
        label.setStyle("-fx-font-family: \"Comic Sans MS\"; -fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: white;");
        tabla.setPlaceholder(label);
        tabla.setBorder(Border.EMPTY);

        setTableEditable();

        textErrores.setVisible(false);
    }
}
