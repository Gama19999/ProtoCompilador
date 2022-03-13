module auto.protocompilador {
    requires javafx.controls;
    requires javafx.fxml;

    opens auto.protocompilador to javafx.fxml;
    exports auto.protocompilador;
}
