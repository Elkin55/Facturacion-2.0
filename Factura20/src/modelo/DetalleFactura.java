package modelo;

public class DetalleFactura {
    private Producto producto;
    private int cantidad;
    private double subtotal;
    private double iva;
    private double total;

    public DetalleFactura(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        calcularTotales();
    }

    private void calcularTotales() {
        subtotal = producto.getPrecio() * cantidad;
        iva = subtotal * 0.12;
        total = subtotal + iva;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
    public double getIva() { return iva; }
    public double getTotal() { return total; }
}
