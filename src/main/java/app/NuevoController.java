package app;

import domain.PeliculaDAO;
import domain.Genero;
import domain.Pelicula;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class NuevoController {
    @FXML private TextField txtId, txtNombre, txtPrecio;
    @FXML private ComboBox<Genero> comboGenero;

    private PeliculaDAO dao = new PeliculaDAO(); // <-- ESTO ARREGLA LA LÍNEA 60

    @FXML
    private void onGuardar() {
        Genero seleccionado = comboGenero.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            System.out.println("Debes seleccionar un género");
            return;
        }

        try {
            // Verifica que los nombres de los campos coincidan con tu constructor de Pelicula
            Pelicula p = new Pelicula(txtId.getText(), 2026, txtNombre.getText(),
                    Double.parseDouble(txtPrecio.getText()), null, "");

            dao.guardarPeliculaCompleta(p, seleccionado.getId());
            cerrar(); // <-- ESTO ARREGLA LA LÍNEA 61
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            List<Genero> generos = dao.obtenerTodosLosGeneros();

            if (generos.isEmpty()) {
                System.out.println("OJO: La base de datos no devolvió géneros.");
            }

            // Esta es la línea que "conecta" los datos con el desplegable
            comboGenero.setItems(FXCollections.observableArrayList(generos));

        } catch (SQLException e) {
            System.err.println("Error al cargar géneros desde la DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelar() {
        // Esto simplemente cierra la ventanita actual
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }
}