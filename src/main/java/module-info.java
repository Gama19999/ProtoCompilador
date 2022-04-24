module auto.protocompilador {
    requires javafx.controls;
    requires javafx.fxml;
    requires guava;

    opens auto.protocompilador to javafx.fxml;
    exports auto.protocompilador;
}
