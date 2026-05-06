import exception.VideoclubException;

public class Pelicula {
    private String id_pelicula;
    private int año;
    private String nombre;
    private int precio;
    private Genero genero;
    private String etiqueta;

    public Pelicula(String id_pelicula, int año, String nombre, int precio, Genero genero, String etiqueta) throws VideoclubException {
        this.id_pelicula = id_pelicula;
        setAño(año);
        this.nombre = nombre;
        setPrecio(precio);
        this.genero = genero;
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public Genero getGenero() {
        return genero;
    }

    public String getId_pelicula() {
        return id_pelicula;
    }

    public int getAño() {
        return año;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setAño(int año) throws VideoclubException {
        if (año < 0 || año > 2026){
            throw new VideoclubException("El año es incorrecto");
        }
        this.año = año;
    }

    public void setPrecio(int precio) throws VideoclubException {
        if (precio < 0){
            throw new VideoclubException("El precio no puede ser negativo");
        }
        this.precio = precio;
    }
}
