package controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import modelo.*;

public class SistemaController {
    private List<Cliente> clientes;
    private List<Producto> productos;
    private List<Factura> facturas;
    private static int numeroFactura = 1;

    public SistemaController() {
        clientes = new ArrayList<>();
        productos = new ArrayList<>();
        facturas = new ArrayList<>();
    }

    public void agregarCliente(Cliente cliente) {
        if (buscarCliente(cliente.getCedula()) == null) {
            clientes.add(cliente);
        }
    }

    public void editarCliente(Cliente cliente) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getCedula().equals(cliente.getCedula())) {
                clientes.set(i, cliente);
                break;
            }
        }
    }

    public void eliminarCliente(String cedula) {
        clientes.removeIf(c -> c.getCedula().equals(cedula));
    }

    public Cliente buscarCliente(String cedula) {
        return clientes.stream()
                .filter(c -> c.getCedula().equals(cedula))
                .findFirst()
                .orElse(null);
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes);
    }

    public void agregarProducto(Producto producto) {
        if (buscarProducto(producto.getCodigo()) == null) {
            productos.add(producto);
        }
    }

    public void editarProducto(Producto producto) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getCodigo().equals(producto.getCodigo())) {
                productos.set(i, producto);
                break;
            }
        }
    }

    public void eliminarProducto(String codigo) {
        productos.removeIf(p -> p.getCodigo().equals(codigo));
    }

    public Producto buscarProducto(String codigo) {
        return productos.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public Factura crearFactura(Cliente cliente) {
        Factura factura = new Factura(numeroFactura++, cliente);
        facturas.add(factura);
        return factura;
    }

    public void anularFactura(int numero) {
        Factura factura = buscarFactura(numero);
        if (factura != null && !factura.isAnulada()) {
            factura.setAnulada(true);      
            for (DetalleFactura detalle : factura.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
            }
        }
    }

    public Factura buscarFactura(int numero) {
        return facturas.stream()
                .filter(f -> f.getNumero() == numero)
                .findFirst()
                .orElse(null);
    }

    public List<Factura> getFacturas() {
        return new ArrayList<>(facturas);
    }

    public List<Factura> getFacturasActivas() {
        return facturas.stream()
                .filter(f -> !f.isAnulada())
                .collect(Collectors.toList());
    }
}