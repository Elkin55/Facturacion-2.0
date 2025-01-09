package vista;

import javax.swing.*;
import java.awt.*;
import controlador.SistemaController;
import modelo.Producto;

public class ProductoDialog extends JDialog {
    private SistemaController controller;
    private Producto producto;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private static final Color AZUL_PRINCIPAL = new Color(0, 123, 255);
    private static final Color GRIS_BORDE = new Color(220, 220, 220);

    public ProductoDialog(Frame parent, SistemaController controller, Producto producto) {
        super(parent, "Producto", true);
        this.controller = controller;
        this.producto = producto;
        initComponents();
        if (producto != null) {
            cargarDatosProducto();
        }
    }

    private void initComponents() {
        setLayout(null);
        setSize(400, 350); 
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        JLabel titulo = new JLabel(producto == null ? "Nuevo Producto" : "Editar Producto");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBounds(0, 20, 400, 30);
        add(titulo);


        int y = 70;
        int espaciado = 45; 

        agregarCampo("Código:", txtCodigo = new JTextField(), 20, y);
        y += espaciado;
        agregarCampo("Nombre:", txtNombre = new JTextField(), 20, y);
        y += espaciado;
        agregarCampo("Precio:", txtPrecio = new JTextField(), 20, y);
        y += espaciado;
        agregarCampo("Stock:", txtStock = new JTextField(), 20, y);

 
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBounds(0, 250, 400, 50); 
        panelBotones.setBackground(Color.WHITE);

        JButton btnCancelar = crearBoton("Cancelar");
        JButton btnGuardar = crearBoton("Guardar");
        
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setForeground(AZUL_PRINCIPAL);
        btnCancelar.setBorder(BorderFactory.createLineBorder(AZUL_PRINCIPAL));
        
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarProducto());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        add(panelBotones);
    }

    private void agregarCampo(String etiqueta, JTextField campo, int x, int y) {
        JLabel label = new JLabel(etiqueta);
        label.setBounds(x, y, 80, 25);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        
        campo.setBounds(x + 85, y, 275, 30);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BORDE),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        campo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        add(label);
        add(campo);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(AZUL_PRINCIPAL);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.setPreferredSize(new Dimension(100, 35));
        return boton;
    }

    private void cargarDatosProducto() {
        if (producto != null) {
            txtCodigo.setText(producto.getCodigo());
            txtNombre.setText(producto.getNombre());
            txtPrecio.setText(String.valueOf(producto.getPrecio()));
            txtStock.setText(String.valueOf(producto.getStock()));
            txtCodigo.setEditable(false);
        }
    }

    private void guardarProducto() {
        if (validarCampos()) {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            Producto nuevoProducto = new Producto(codigo, nombre, precio, stock);

            if (producto == null) {
                controller.agregarProducto(nuevoProducto);
            } else {
                controller.editarProducto(nuevoProducto);
            }
            dispose();
        }
    }

    private boolean validarCampos() {
        if (txtCodigo.getText().trim().isEmpty() ||
            txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Los campos Código y Nombre son obligatorios",
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El precio debe ser un número mayor que 0",
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El stock debe ser un número entero no negativo",
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}