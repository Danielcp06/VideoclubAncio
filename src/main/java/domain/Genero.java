package domain;

public class Genero {
    private int id;
    private String nombre;

    public Genero(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }

    public String getNombre() {
        return nombre;
    }

    // Este método es el que usa el ComboBox para saber qué texto mostrar
    @Override
    public String toString() { return nombre; }
}