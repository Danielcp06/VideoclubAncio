package domain;

import domain.Pelicula;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDAO {

    // REQUISITO: Búsqueda con PreparedStatement (2 campos: nombre y precio) [cite: 21, 69]
    public List<Pelicula> buscar(String nombre, double precioMax) throws SQLException {
        List<Pelicula> lista = new ArrayList<>();
        String sql = "SELECT * FROM pelicula WHERE nombre LIKE ? AND precio <= ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            ps.setDouble(2, precioMax);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Creamos el objeto Pelicula con los datos de la BD
                    // Nota: El constructor de Pelicula lanza VideoclubException
                    try {
                        Pelicula p = new Pelicula(
                                String.valueOf(rs.getInt("id_pelicula")),
                                2024, // Para el ejemplo, ya que en SQL pusiste tipo DATE
                                rs.getString("nombre"),
                                rs.getDouble("precio"),
                                null, ""
                        );
                        lista.add(p);
                    } catch (Exception e) { /* Manejar error de validación */ }
                }
            }
        }
        return lista;
    }

    // REQUISITO: Transacción con Rollback (Alta de película y su género) [cite: 17, 31, 63]
    public void insertarConGenero(Pelicula p, int idGenero) throws SQLException {
        String sqlPeli = "INSERT INTO pelicula (id_pelicula, nombre, año, precio) VALUES (?, ?, ?, ?)";
        String sqlRelacion = "INSERT INTO pelicula_genero (id_pelicula, id__genero) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Iniciamos transacción [cite: 153]

            // 1. Insertar Película
            try (PreparedStatement psPeli = conn.prepareStatement(sqlPeli)) {
                psPeli.setInt(1, Integer.parseInt(p.getId_pelicula()));
                psPeli.setString(2, p.getNombre());
                psPeli.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                psPeli.setDouble(4, p.getPrecio());
                psPeli.executeUpdate();
            }

            // 2. Insertar Relación (Tabla intermedia de tu SQL)
            try (PreparedStatement psRel = conn.prepareStatement(sqlRelacion)) {
                psRel.setInt(1, Integer.parseInt(p.getId_pelicula()));
                psRel.setInt(2, idGenero);
                psRel.executeUpdate();
            }

            conn.commit(); // Si todo va bien, confirmamos [cite: 31]
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Si algo falla, deshacemos todo [cite: 31, 59]
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
}