package domain;

import java.util.ArrayList;

public class Usuario {
    private String nombre;
    private String id_usuario;
    private ArrayList<Pelicula> peliculas;

    public Usuario(String nombre, String id_usuario) {
        this.nombre = nombre;
        this.id_usuario = id_usuario;
        peliculas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public ArrayList<Pelicula> getPeliculas() {
        return peliculas;
    }
}
