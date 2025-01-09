package vista;

import javax.swing.*;
import java.awt.*;
import controlador.SistemaController;
import modelo.Cliente;

public class ClienteDialog extends JDialog {
    private SistemaController controller;
    private Cliente cliente;
    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private static final Color AZUL_PRINCIPAL = new Color(0, 123, 255);
    private static final Color GRIS_BORDE = new Color(220, 220, 220);

    public ClienteDialog(Frame parent, SistemaController controller, Cliente cliente) {
        super(parent, "Cliente", true);
        this.controller = controller;
        this.cliente = cliente;
        initComponents();
        if (cliente != null) {
            cargarDatosCliente();
        }
    }

    private void initComponents() {
        setLayout(null);
        setSize(400, 450); // Aumentamos el alto para asegurar que los botones sean visibles
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        // Título
        JLabel titulo = new JLabel(cliente == null ? "Nuevo Cliente" : "Editar Cliente");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBounds(0, 20, 400, 30);
        add(titulo);

        // Campos con etiquetas
        int y = 70;
        int espaciado = 45; // Aumentamos el espaciado entre campos

        agregarCampo("Cédula:", txtCedula = new JTextField(), 20, y);
        y += espaciado;
        agregarCampo("Nombres:", txtNombres = new JTextField(), 20, y);
        y += espaciado;
        agregarCampo("Apellidos:", txtApellidos = new JTextField(), 20, y);
        y += espaciado;
        agregarCampo("Dirección:", txtDireccion = new JTextField(), 20, y);
        y += espaciado;
        agregarCampo("Teléfono:", txtTelefono = new JTextField(), 20, y);
        y += espaciado;
        agregarCampo("Email:", txtEmail = new JTextField(), 20, y);

        // Panel de botones - Ajustamos la posición Y para que quede visible
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBounds(0, 350, 400, 50); // Ajustamos el alto del panel y su posición
        panelBotones.setBackground(Color.WHITE);

        JButton btnCancelar = crearBoton("Cancelar");
        JButton btnGuardar = crearBoton("Guardar");
        
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setForeground(AZUL_PRINCIPAL);
        btnCancelar.setBorder(BorderFactory.createLineBorder(AZUL_PRINCIPAL));
        
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarCliente());

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

    private void cargarDatosCliente() {
        if (cliente != null) {
            txtCedula.setText(cliente.getCedula());
            txtNombres.setText(cliente.getNombres());
            txtApellidos.setText(cliente.getApellidos());
            txtDireccion.setText(cliente.getDireccion());
            txtTelefono.setText(cliente.getTelefono());
            txtEmail.setText(cliente.getEmail());
            txtCedula.setEditable(false);
        }
    }

    private void guardarCliente() {
        if (validarCampos()) {
            String cedula = txtCedula.getText().trim();
            String nombres = txtNombres.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();

            Cliente nuevoCliente = new Cliente(cedula, nombres, apellidos, 
                                            direccion, telefono, email);

            if (cliente == null) {
                controller.agregarCliente(nuevoCliente);
            } else {
                controller.editarCliente(nuevoCliente);
            }
            dispose();
        }
    }

    private boolean validarCampos() {
        if (txtCedula.getText().trim().isEmpty() ||
            txtNombres.getText().trim().isEmpty() ||
            txtApellidos.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Los campos Cédula, Nombres y Apellidos son obligatorios",
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}