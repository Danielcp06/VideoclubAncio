package domain;

public class Genero {
    private String id_genero;
    private String nombre;

    public String getId_genero() {
        return id_genero;
    }

    public String getNombre() {
        return nombre;
    }

    public Genero(String id_genero, String nombre) {
        this.id_genero = id_genero;
        this.nombre = nombre;
    }
}
