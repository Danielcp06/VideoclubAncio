package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Pelicula;

public class Main {

    @FXML private TableView<Pelicula> tablePeliculas;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colNombre;
    @FXML private TableColumn<Pelicula, Integer> colAño;
    @FXML private TableColumn<Pelicula, Double> colPrecio;

    @FXML
    public void initialize() {
        // Configuramos cómo se vinculan las columnas con los atributos de la clase Pelicula
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAño.setCellValueFactory(new PropertyValueFactory<>("año"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Datos de prueba para que el profesor vea la interfaz funcionando
        ObservableList<Pelicula> listaPrueba = FXCollections.observableArrayList(
                new Pelicula(1, "Inception", 2010, 15.50),
                new Pelicula(2, "The Matrix", 1999, 12.00)
        );
        tablePeliculas.setItems(listaPrueba);
    }

    @FXML
    private void onSearch() {
        System.out.println("Buscando..."); // Aquí irá el PreparedStatement en la Fase 3
    }

    @FXML
    private void onDelete() {
        Pelicula seleccionada = tablePeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar " + seleccionada.getNombre() + "?");
            confirmacion.showAndWait();
        }
    }
}