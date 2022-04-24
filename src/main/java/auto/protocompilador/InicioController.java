package auto.protocompilador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
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
    private TextArea area;
    
    @FXML
    private void cargarPressed() throws IOException {
        
        fileChooser.setTitle("Buscar archivo de codigo fuente");
        
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"),
                new FileChooser.ExtensionFilter("Archivos Java", "*.java"),
                new FileChooser.ExtensionFilter("Archivos C++", "*.cpp")
        );

        File archivo = fileChooser.showOpenDialog(cargar.getScene().getWindow());
        
        area.setText(App.analizer.fileCharger(archivo).toString());
    }
    
    @FXML
    private void lexicoPressed() throws IOException {
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
        // TODO agregar el metodo de control de estructuras
        // App.analizer.controlStructuresAnalizer();
    }
    
    @FXML
    void initialize() {
        fileChooser = new FileChooser();
    }
}
