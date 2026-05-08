package domain;

import domain.Pelicula;
import exception.VideoclubException;

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

    public void insertarPeliculaConGenero(Pelicula p, int idGenero) throws SQLException {
        String sqlPeli = "INSERT INTO pelicula (id_pelicula, nombre, año, precio) VALUES (?, ?, ?, ?)";
        String sqlRelacion = "INSERT INTO pelicula_genero (id_pelicula, id__genero) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Desactivamos el auto-commit para manejar la transacción [cite: 17]

            // 1. Insertar la película
            try (PreparedStatement psPeli = conn.prepareStatement(sqlPeli)) {
                psPeli.setInt(1, Integer.parseInt(p.getId_pelicula()));
                psPeli.setString(2, p.getNombre());
                psPeli.setDate(3, java.sql.Date.valueOf("2024-01-01")); // Fecha de ejemplo
                psPeli.setDouble(4, p.getPrecio());
                psPeli.executeUpdate();
            }

            // 2. Insertar la relación en la tabla intermedia (Falla aquí si el idGenero no existe)
            try (PreparedStatement psRel = conn.prepareStatement(sqlRelacion)) {
                psRel.setInt(1, Integer.parseInt(p.getId_pelicula()));
                psRel.setInt(2, idGenero);
                psRel.executeUpdate();
            }

            conn.commit(); // Si todo va bien, guardamos cambios [cite: 17]
            System.out.println("Transacción completada con éxito.");
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Si algo falla, se deshace TODO [cite: 17, 31]
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public void guardarPeliculaCompleta(Pelicula p, int idGenero) throws SQLException {
        String sqlPeli = "INSERT INTO pelicula (id_pelicula, nombre, año, precio) VALUES (?, ?, ?, ?)";
        String sqlRelacion = "INSERT INTO pelicula_genero (id_pelicula, id__genero) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            // 1. Insertar Película
            try (PreparedStatement psPeli = conn.prepareStatement(sqlPeli)) {
                psPeli.setInt(1, Integer.parseInt(p.getId_pelicula()));
                psPeli.setString(2, p.getNombre());
                psPeli.setString(3, "2024-01-01"); // Año simplificado
                psPeli.setDouble(4, p.getPrecio());
                psPeli.executeUpdate();
            }

            // 2. Insertar Relación con Género
            try (PreparedStatement psRel = conn.prepareStatement(sqlRelacion)) {
                psRel.setInt(1, Integer.parseInt(p.getId_pelicula()));
                psRel.setInt(2, idGenero);
                psRel.executeUpdate();
            }

            conn.commit(); // TODO OK -> GUARDAR
            System.out.println("Guardado con éxito");

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // ERROR -> DESHACER TODO
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public void eliminarPelicula(String id) throws SQLException {
        String sql = "DELETE FROM pelicula WHERE id_pelicula = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();
        }
    }

    public List<Genero> obtenerTodosLosGeneros() throws SQLException {
        List<Genero> lista = new ArrayList<>();
        String sql = "SELECT id__genero, nombre FROM genero"; //

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Importante: comprueba los nombres de columna en tu tabla genero
                lista.add(new Genero(rs.getInt("id__genero"), rs.getString("nombre")));
            }
        }
        return lista;
    }

    // En PeliculaDAO.java
    public void insertarSoloGenero(String nombre) throws SQLException {
        String sql = "INSERT INTO genero (nombre) VALUES (?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }

    public List<Pelicula> buscarPorNombre(String nombre) throws SQLException {
        List<Pelicula> lista = new ArrayList<>();

        // 1. Modificamos la consulta para extraer el año como un número
        String sql = "SELECT id_pelicula, nombre, YEAR(año) AS anio_num, precio FROM pelicula WHERE nombre LIKE ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // 2. AQUÍ PONES LA LÍNEA: Extraemos el alias 'anio_num' que creamos en el SQL
                int anio = rs.getInt("anio_num");

                // 3. Usamos la variable 'anio' para crear el objeto Pelicula
                try {
                    lista.add(new Pelicula(
                            rs.getString("id_pelicula"),
                            anio, // Pasamos el entero directamente
                            rs.getString("nombre"),
                            rs.getDouble("precio"),
                            null,
                            ""
                    ));
                } catch (VideoclubException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return lista;
    }

}