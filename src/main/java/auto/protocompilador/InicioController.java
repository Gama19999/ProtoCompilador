package auto.protocompilador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class InicioController {
    private FileChooser fileChooser;
    private File archivo;
    
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
    private void cargarPressed() throws FileNotFoundException, IOException {
        
        fileChooser.setTitle("Buscar archivo de codigo fuente");
        
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"),
                new FileChooser.ExtensionFilter("Archivos Java", "*.java"),
                new FileChooser.ExtensionFilter("Archivos C++", "*.cpp")
        );
        
        archivo = fileChooser.showOpenDialog(cargar.getScene().getWindow());
        
        area.setText(App.analizer.fileCharger(archivo).toString());
    }
    
    @FXML
    private void lexicoPressed() throws IOException {
        Stage stage = new Stage();
        Scene scene = new Scene(App.loadFXML("tablaLex"), 590, 600);
        
        stage.setTitle("Analizador Lexico");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    void initialize() {
        fileChooser = new FileChooser();
    }
}
