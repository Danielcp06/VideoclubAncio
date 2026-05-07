package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Cargamos el diseño
        Parent root = FXMLLoader.load(getClass().getResource("/app/main.fxml"));

        // 2. Creamos la escena y la guardamos en una variable "scene"
        Scene scene = new Scene(root);

        // 3. AÑADIMOS EL CSS (Asegúrate de que el archivo style.css esté en resources/app/)
        scene.getStylesheets().add(getClass().getResource("/app/style.css").toExternalForm());

        primaryStage.setTitle("Videoclub Ancio");

        // 4. Le pasamos la escena ya "tuneada" al stage
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}