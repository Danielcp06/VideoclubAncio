package app;


import domain.Genero;
import domain.Pelicula;
import domain.PeliculaDAO;
import exception.VideoclubException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class Main {


    @FXML private TableView<Pelicula> tablePeliculas;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colNombre;
    @FXML private TableColumn<Pelicula, Integer> colAño;
    @FXML private TableColumn<Pelicula, Double> colPrecio;


    private PeliculaDAO peliDAO = new PeliculaDAO();

    @FXML
    void main(String[] args) {

        initialize();

    }



    // Asegúrate de tener estos @FXML arriba si quieres usarlos para filtrar
// @FXML private TextField txtNombre;
// @FXML private TextField txtPrecioMax;


    @FXML
    private void onSearch() {
        try {
            // 1. Recogemos los filtros (puedes poner valores fijos para probar)
            String nombreBusqueda = ""; // txtNombre.getText();
            double precioBusqueda = 999.0; // Double.parseDouble(txtPrecioMax.getText());

            // 2. Llamamos al DAO (Lógica SQL con PreparedStatement)
            List<Pelicula> resultados = peliDAO.buscar(nombreBusqueda, precioBusqueda);

            // 3. Convertimos a ObservableList para la TableView de JavaFX
            ObservableList<Pelicula> listaObservable = FXCollections.observableArrayList(resultados);
            tablePeliculas.setItems(listaObservable);

            System.out.println("Búsqueda realizada con éxito. Encontrados: " + resultados.size());

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Base de Datos");
            alert.setContentText("No se pudo conectar o buscar: " + e.getMessage());
            alert.showAndWait();
        } catch (NumberFormatException e) {
            System.out.println("Error: El precio debe ser un número válido");
        }
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
    private void onDelete() {
        Pelicula seleccionada = tablePeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar " + seleccionada.getNombre() + "?");
            confirmacion.showAndWait();
        }
    }
}