package domain;

import domain.Pelicula;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDAO {

    public List<Pelicula> buscar(String nombre, double precioMax) throws SQLException {
        List<Pelicula> lista = new ArrayList<>();
        // SQL usando tus tablas: pelicula
        String sql = "SELECT * FROM pelicula WHERE nombre LIKE ? AND precio <= ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            ps.setDouble(2, precioMax);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        // Mapeamos las columnas de tu tabla 'pelicula' al objeto Java
                        Pelicula p = new Pelicula(
                                String.valueOf(rs.getInt("id_pelicula")),
                                2024, // El año en tu SQL es DATE, aquí simplificamos a int para tu constructor
                                rs.getString("nombre"),
                                rs.getDouble("precio"),
                                null, // Genero (puedes dejarlo null de momento)
                                ""    // Etiqueta
                        );
                        lista.add(p);
                    } catch (Exception e) {
                        System.err.println("Error de validación en película: " + e.getMessage());
                    }
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