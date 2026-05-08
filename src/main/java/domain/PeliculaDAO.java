package domain;

import exception.VideoclubException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDAO {

    // 1. METODO DE BÚSQUEDA UNIFICADO (Gestiona el error de DATE a Integer)
    public List<Pelicula> buscar(String nombre, double precioMax) throws SQLException {
        List<Pelicula> lista = new ArrayList<>();

        // Usamos GROUP_CONCAT para agrupar los géneros en un solo String separado por comas
        String sql = "SELECT p.id_pelicula, p.nombre, YEAR(p.año) as anio_num, p.precio, " +
                "GROUP_CONCAT(g.nombre SEPARATOR ', ') as nombres_generos " +
                "FROM pelicula p " +
                "LEFT JOIN pelicula_genero pg ON p.id_pelicula = pg.id_pelicula " +
                "LEFT JOIN genero g ON pg.id__genero = g.id__genero " +
                "WHERE p.nombre LIKE ? AND p.precio <= ? " +
                "GROUP BY p.id_pelicula"; // Agrupamos por película para que no salgan repetidas

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            ps.setDouble(2, precioMax);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        Pelicula p = new Pelicula(
                                rs.getString("id_pelicula"),
                                rs.getInt("anio_num"),
                                rs.getString("nombre"),
                                rs.getDouble("precio"),
                                null, ""
                        );
                        // Aquí asignamos el String con todos los géneros (ej: "Acción, Comedia")
                        p.setNombreGenero(rs.getString("nombres_generos"));
                        lista.add(p);
                    } catch (VideoclubException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return lista;
    }

    // 2. METODO PARA GUARDAR (Con AUTO_INCREMENT y Múltiples Géneros)
    public void guardarPeliculaConMultiplesGeneros(Pelicula p, List<Integer> idsGeneros) throws SQLException {
        String sqlPeli = "INSERT INTO pelicula (nombre, año, precio) VALUES (?, ?, ?)";
        String sqlRelacion = "INSERT INTO pelicula_genero (id_pelicula, id__genero) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Transacción activa

            int idGenerado = -1;
            try (PreparedStatement psPeli = conn.prepareStatement(sqlPeli, Statement.RETURN_GENERATED_KEYS)) {
                psPeli.setString(1, p.getNombre());
                psPeli.setString(2, p.getAño() + "-01-01"); // Fecha por defecto
                psPeli.setDouble(3, p.getPrecio());
                psPeli.executeUpdate();

                try (ResultSet rs = psPeli.getGeneratedKeys()) {
                    if (rs.next()) idGenerado = rs.getInt(1);
                }
            }

            if (idGenerado == -1) throw new SQLException("Error al generar ID.");

            try (PreparedStatement psRel = conn.prepareStatement(sqlRelacion)) {
                for (Integer idGen : idsGeneros) {
                    psRel.setInt(1, idGenerado);
                    psRel.setInt(2, idGen);
                    psRel.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    // 3. OBTENER GÉNEROS PARA LOS CHECKBOXES
    public List<Genero> obtenerTodosLosGeneros() throws SQLException {
        List<Genero> lista = new ArrayList<>();
        String sql = "SELECT id__genero, nombre FROM genero";
        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Genero(rs.getInt("id__genero"), rs.getString("nombre")));
            }
        }
        return lista;
    }

    public void eliminarPelicula(String id) throws SQLException {
        // 1. Sentencia para borrar la relación en la tabla intermedia
        String sqlGeneros = "DELETE FROM pelicula_genero WHERE id_pelicula = ?";
        // 2. Sentencia para borrar la película
        String sqlPeli = "DELETE FROM pelicula WHERE id_pelicula = ?";

        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Iniciamos transacción para que se borre TODO o NADA

            // PASO A: Borrar los géneros de esa película
            try (PreparedStatement ps1 = conn.prepareStatement(sqlGeneros)) {
                ps1.setInt(1, Integer.parseInt(id));
                ps1.executeUpdate();
            }

            // PASO B: Borrar la película
            try (PreparedStatement ps2 = conn.prepareStatement(sqlPeli)) {
                ps2.setInt(1, Integer.parseInt(id));
                ps2.executeUpdate();
            }

            conn.commit(); // Si ambos borrados funcionan, confirmamos
            System.out.println("Película y sus géneros eliminados correctamente.");

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Si algo falla, no borramos nada
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
}