package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Asegúrate de que la ruta al FXML sea correcta
        Parent root = FXMLLoader.load(getClass().getResource("/app/main.fxml"));
        primaryStage.setTitle("Videoclub Ancio");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}