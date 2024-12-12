package Pojo;

public class Pedido {
    String Nombrecliente;
    String Direccion;
    String Telefono;
    String NumeroDeVenta;
    String EstadoDelpedido;
    String LongitudPedido;
    String LatitudPedido;
    String EsForaneo;

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

    public String getLongitudPedido() {
        return LongitudPedido;
    }

    public void setLongitudPedido(String longitudPedido) {
        LongitudPedido = longitudPedido;
    }

    public String getLatitudPedido() {
        return LatitudPedido;
    }

    public void setLatitudPedido(String latitudPedido) {
        LatitudPedido = latitudPedido;
    }

    public String getEsForaneo() {
        return EsForaneo;
    }

    public void setEsForaneo(String esForaneo) {
        EsForaneo = esForaneo;
    }
}
