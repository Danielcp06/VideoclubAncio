package app;

import domain.Pelicula;
import domain.PeliculaDAO; // Importamos el DAO que creamos antes
import exception.VideoclubException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {

    @FXML private TableView<Pelicula> tablePeliculas;
    @FXML private TableColumn<Pelicula, String> colId; // Cambiado a String para coincidir con tu clase Pelicula
    @FXML private TableColumn<Pelicula, String> colNombre;
    @FXML private TableColumn<Pelicula, Integer> colAño;
    @FXML private TableColumn<Pelicula, Double> colPrecio;

    private PeliculaDAO peliDAO = new PeliculaDAO();

    // Este Metodo se ejecuta SOLO cuando el FXML ya está cargado
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id_pelicula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colAño.setCellValueFactory(new PropertyValueFactory<>("año"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            // Ya no usamos datos de prueba, ¡traemos los de la base de datos!
            List<Pelicula> lista = peliDAO.buscar("", 9999.0);
            tablePeliculas.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
    }
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

    @FXML
    private void onOpenAdd() {
        try {
            // Carga la vista del modal para añadir
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/nuevo.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Crea una nueva ventana (Stage)
            scene.getStylesheets().add(getClass().getResource("/app/style.css").toExternalForm());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Hace que sea un modal (bloquea la de atrás)
            stage.setTitle("Añadir Nueva Película");
            stage.setScene(scene);
            stage.show();

            // Muestra la ventana y espera a que se cierre
            stage.showAndWait();

            // Al cerrar, refrescamos la tabla automáticamente
            onSearch();

        } catch (IOException e) {
            System.err.println("Error al abrir el modal de añadir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onDelete() {
        Pelicula seleccionada = tablePeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar " + seleccionada.getNombre() + "?");
            confirmacion.showAndWait();
        }


    }
    @FXML
    private TextField txtBuscar; // El cuadro donde el usuario escribe

    @FXML
    private void onBuscar() {
        String texto = txtBuscar.getText();

        if (texto == null || texto.trim().isEmpty()) {
            onActualizar();
            return;
        }

        try {
            // Intentamos buscar
            List<Pelicula> resultados = peliDAO.buscarPorNombre(texto);
            tablePeliculas.setItems(FXCollections.observableArrayList(resultados));
            System.out.println("Búsqueda finalizada. Encontrados: " + resultados.size());

        } catch (SQLException e) {
            // Error de SQL normal
            mostrarError("Error de SQL", e.getMessage());
        }
    }

    // Método auxiliar para no repetir código de alertas
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    private void onActualizar() {
        txtBuscar.clear();
        // Cambiado cargarDatosTabla() por cargarDatos() que es tu método real
        cargarDatos();
    }
}