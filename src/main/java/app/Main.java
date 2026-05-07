package app;


import domain.Genero;
import domain.Pelicula;
import exception.VideoclubException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class Main {

    @FXML private TableView<Pelicula> tablePeliculas;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colNombre;
    @FXML private TableColumn<Pelicula, Integer> colAño;
    @FXML private TableColumn<Pelicula, Double> colPrecio;

    @FXML
    void main(String[] args) {

        initialize();

    }
    public void initialize() {
        // Configuramos cómo se vinculan las columnas con los atributos de la clase domain.Pelicula
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAño.setCellValueFactory(new PropertyValueFactory<>("año"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Datos de prueba para que el profesor vea la interfaz funcionando
        ObservableList<Pelicula> listaPrueba = null;
        try {
            listaPrueba = FXCollections.observableArrayList(
                    new Pelicula("1", 2010,"Inception" , 15.50,new Genero("1","Suspense"),"+12"),
                    new Pelicula("2",1999 , "The Matrix", 12.00,new Genero("2","Accion"),"+16")
            );
        } catch (VideoclubException e) {
            System.out.println(e.getMessage());
        }
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