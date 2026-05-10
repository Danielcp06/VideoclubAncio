package app;

import domain.PeliculaDAO;
import domain.Genero;
import domain.Pelicula;
import exception.VideoclubException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NuevoController {
    @FXML private TextField txtId, txtNombre, txtPrecio;
    @FXML private ComboBox<Genero> comboGenero;
    @FXML private TextField txtAnio; // Vinculado al fx:id del FXML


    private PeliculaDAO dao = new PeliculaDAO(); // <-- ESTO ARREGLA LA LÍNEA 60

    @FXML
    private void onGuardar() {
        List<Integer> idsSeleccionados = new ArrayList<>();
        for (CheckBox cb : checkBoxesList) {
            if (cb.isSelected()) {
                idsSeleccionados.add((Integer) cb.getUserData());
            }
        }

        if (idsSeleccionados.isEmpty()) {
            System.out.println("Debes seleccionar al menos un género");
            return;
        }

        try {
            // LEER EL AÑO REAL DEL USUARIO
            int anioValor = Integer.parseInt(txtAnio.getText());

            Pelicula p = new Pelicula(
                    null,
                    anioValor, // <--- ESTO ES LO QUE SE GUARDA
                    txtNombre.getText(),
                    Double.parseDouble(txtPrecio.getText()),
                    null, ""
            );

            dao.guardarPeliculaConMultiplesGeneros(p, idsSeleccionados);
            cerrar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private VBox containerGeneros; // Vinculamos el VBox del FXML
    private List<CheckBox> checkBoxesList = new ArrayList<>(); // Para rastrearlos fácilmente

    @FXML
    public void initialize() {
        try {
            List<Genero> todosLosGeneros = dao.obtenerTodosLosGeneros();

            for (Genero g : todosLosGeneros) {
                CheckBox cb = new CheckBox(g.getNombre());
                cb.setUserData(g.getId()); // Guardamos el ID del género "escondido" en el checkbox
                cb.setStyle("-fx-text-fill: white;"); // Para que se vea en el modo oscuro

                checkBoxesList.add(cb);
                containerGeneros.getChildren().add(cb); // Lo añadimos a la vista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onCancelar() {
        // Usamos txtNombre porque txtId es null y daría error
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }



    @FXML
    private void cerrar() {
        // Cambia txtId (que ya no existe) por txtNombre
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }


}