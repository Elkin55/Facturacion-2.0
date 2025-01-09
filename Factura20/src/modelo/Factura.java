package modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Factura {
    private int numero;
    private Date fecha;
    private Cliente cliente;
    private List<DetalleFactura> detalles;
    private boolean anulada;
    private double subtotal;
    private double iva;
    private double total;

    public Factura(int numero, Cliente cliente) {
        this.numero = numero;
        this.fecha = new Date();
        this.cliente = cliente;
        this.detalles = new ArrayList<>();
        this.anulada = false;
    }

    public void calcularTotales() {
        subtotal = 0;
        for (DetalleFactura detalle : detalles) {
            subtotal += detalle.getSubtotal();
        }
        iva = subtotal * 0.12;
        total = subtotal + iva;
    }

    public int getNumero() { return numero; }
    public Date getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public List<DetalleFactura> getDetalles() { return detalles; }
    public boolean isAnulada() { return anulada; }
    public void setAnulada(boolean anulada) { this.anulada = anulada; }
    public double getSubtotal() { return subtotal; }
    public double getIva() { return iva; }
    public double getTotal() { return total; }
}