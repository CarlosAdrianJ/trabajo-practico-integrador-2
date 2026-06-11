package integrador.prog2.entities;

public class PerfilUsuario extends Base {

    private String direccion;
    private String ciudad;
    private String dni;
    private String observaciones;

    public PerfilUsuario() {
        super();
    }

    public PerfilUsuario(String direccion, String ciudad, String dni, String observaciones) {
        super();
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.dni = dni;
        this.observaciones = observaciones;
    }

    public PerfilUsuario(Long id, String direccion, String ciudad, String dni, String observaciones) {
        super();
        setId(id);
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.dni = dni;
        this.observaciones = observaciones;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "PerfilUsuario{" +
                "id=" + getId() +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", dni='" + dni + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}