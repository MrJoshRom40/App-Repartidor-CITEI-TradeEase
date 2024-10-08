package Pojo;

public class Pedido {
    String Nombrecliente;
    String Direccion;
    String Telefono;
    String NumeroDeVenta;
    String EstadoDelpedido;

//hols
    public String getNombrecliente() {
        return Nombrecliente;
    }

    public void setNombrecliente(String nombrecliente) {
        Nombrecliente = nombrecliente;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getNumeroDeVenta() {
        return NumeroDeVenta;
    }

    public void setNumeroDeVenta(String numeroDeVenta) {
        NumeroDeVenta = numeroDeVenta;
    }

    public String getEstadoDelpedido() {
        return EstadoDelpedido;
    }

    public void setEstadoDelpedido(String estadoDelpedido) {
        EstadoDelpedido = estadoDelpedido;
    }
}
