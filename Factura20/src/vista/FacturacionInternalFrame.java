package vista;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import controlador.SistemaController;
import modelo.*;
import java.util.List;

public class FacturacionInternalFrame extends JInternalFrame {
    private SistemaController controller;
    private JTextField txtNumeroFactura;
    private JTextField txtFecha;
    private JTextField txtCedulaCliente;
    private JTextField txtNombresCliente;
    private JTextField txtCodigoProducto;
    private JTextField txtCantidad;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JLabel lblSubtotal;
    private JLabel lblIva;
    private JLabel lblTotal;
    private Factura facturaActual;
    
    private static final Color AZUL_PRINCIPAL = new Color(0, 123, 255);
    private static final Color GRIS_CLARO = new Color(248, 249, 250);
    private static final Color BORDE_GRIS = new Color(220, 220, 220);

    public FacturacionInternalFrame(SistemaController controller) {
        super("Facturación", true, true, true, true);
        this.controller = controller;
        initComponents();
        setSize(800, 600);
        customizeUI();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Panel principal con padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior - datos de factura y cliente
        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 0, 10));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE_GRIS),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Panel datos factura
        JPanel panelDatosFactura = new JPanel(new GridLayout(1, 4, 10, 0));
        panelDatosFactura.setBackground(Color.WHITE);
        
        agregarCampo(panelDatosFactura, "No. Factura:", txtNumeroFactura = new JTextField());
        txtNumeroFactura.setEditable(false);
        agregarCampo(panelDatosFactura, "Fecha:", txtFecha = new JTextField());
        txtFecha.setEditable(false);

        // Panel datos cliente
        JPanel panelCliente = new JPanel(new GridLayout(1, 3, 10, 0));
        panelCliente.setBackground(Color.WHITE);

        // Panel para cédula y botón buscar
        JPanel panelCedula = new JPanel(new BorderLayout(5, 0));
        panelCedula.setBackground(Color.WHITE);
        agregarCampo(panelCedula, "Cédula Cliente:", txtCedulaCliente = new JTextField());
        JButton btnBuscarCliente = createStyledButton("Buscar");
        panelCedula.add(btnBuscarCliente, BorderLayout.EAST);

        agregarCampo(panelCliente, "", panelCedula);
        agregarCampo(panelCliente, "Cliente:", txtNombresCliente = new JTextField());
        txtNombresCliente.setEditable(false);

        panelSuperior.add(panelDatosFactura);
        panelSuperior.add(panelCliente);

        mainPanel.add(panelSuperior, BorderLayout.NORTH);

        // Panel central - productos
        JPanel panelCentral = new JPanel(new BorderLayout(0, 10));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE_GRIS),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Panel para agregar productos
        JPanel panelAgregarProducto = new JPanel(new GridLayout(1, 5, 10, 0));
        panelAgregarProducto.setBackground(Color.WHITE);
        
        agregarCampo(panelAgregarProducto, "Código:", txtCodigoProducto = new JTextField());
        agregarCampo(panelAgregarProducto, "Cantidad:", txtCantidad = new JTextField());
        
        JButton btnAgregarProducto = createStyledButton("Agregar");
        JButton btnBuscarProducto = createStyledButton("Buscar Producto");
        
        panelAgregarProducto.add(btnAgregarProducto);
        panelAgregarProducto.add(btnBuscarProducto);

        panelCentral.add(panelAgregarProducto, BorderLayout.NORTH);

        // Tabla de productos
        String[] columnas = {"Código", "Producto", "Cantidad", "Precio Unit.", "Subtotal", "IVA", "Total"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTabla);
        
        // Estilo de la tabla
        tablaProductos.setShowGrid(true);
        tablaProductos.setGridColor(BORDE_GRIS);
        tablaProductos.setRowHeight(30);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaProductos.setSelectionBackground(new Color(232, 240, 254));
        tablaProductos.setSelectionForeground(Color.BLACK);

        // Renderer para formato de moneda
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setHorizontalAlignment(RIGHT);
                if (value instanceof Double) {
                    setText(String.format("$%.2f", value));
                }
                return c;
            }
        };

        // Aplicar renderers
        for (int i = 0; i < tablaProductos.getColumnCount(); i++) {
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            if (i >= 3) { // Columnas numéricas
                renderer = moneyRenderer;
            }
            tablaProductos.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
        panelCentral.add(scrollPane, BorderLayout.CENTER);

        // Panel de totales
        JPanel panelTotales = new JPanel(new GridLayout(3, 2, 5, 5));
        panelTotales.setBackground(Color.WHITE);
        panelTotales.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        lblSubtotal = new JLabel("0.00");
        lblIva = new JLabel("0.00");
        lblTotal = new JLabel("0.00");

        agregarLabelTotal(panelTotales, "Subtotal:", lblSubtotal);
        agregarLabelTotal(panelTotales, "IVA:", lblIva);
        agregarLabelTotal(panelTotales, "Total:", lblTotal);

        JPanel panelTotalesWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotalesWrapper.setBackground(Color.WHITE);
        panelTotalesWrapper.add(panelTotales);
        
        panelCentral.add(panelTotalesWrapper, BorderLayout.SOUTH);
        mainPanel.add(panelCentral, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton btnNuevaFactura = createStyledButton("Nueva Factura");
        JButton btnGuardarFactura = createStyledButton("Guardar Factura");
        
        btnNuevaFactura.addActionListener(e -> nuevaFactura());
        btnGuardarFactura.addActionListener(e -> guardarFactura());
        
        panelBotones.add(btnNuevaFactura);
        panelBotones.add(btnGuardarFactura);
        
        mainPanel.add(panelBotones, BorderLayout.SOUTH);

        // Agregar panel principal
        add(mainPanel);

        // Agregar listeners
        btnBuscarCliente.addActionListener(e -> mostrarDialogoBusquedaCliente());
        btnBuscarProducto.addActionListener(e -> mostrarDialogoBusquedaProducto());
        btnAgregarProducto.addActionListener(e -> agregarProductoAFactura());

        txtCedulaCliente.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarCliente();
                }
            }
        });

        txtCodigoProducto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarProducto();
                }
            }
        });

        txtCantidad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    agregarProductoAFactura();
                }
            }
        });

        // Iniciar con una nueva factura
        nuevaFactura();
    }

    private void agregarCampo(JPanel panel, String etiqueta, Component campo) {
        JPanel wrapper = new JPanel(new BorderLayout(5, 0));
        wrapper.setBackground(Color.WHITE);
        
        if (!etiqueta.isEmpty()) {
            JLabel label = new JLabel(etiqueta);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            wrapper.add(label, BorderLayout.WEST);
        }
        
        if (campo instanceof JTextField) {
            JTextField textField = (JTextField) campo;
            textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_GRIS),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
            ));
            textField.setFont(new Font("Arial", Font.PLAIN, 12));
        }
        
        wrapper.add(campo, BorderLayout.CENTER);
        panel.add(wrapper);
    }

    private void agregarLabelTotal(JPanel panel, String etiqueta, JLabel label) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 12));
        lblEtiqueta.setHorizontalAlignment(SwingConstants.RIGHT);
        
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(lblEtiqueta);
        panel.add(label);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(AZUL_PRINCIPAL);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }

    private void customizeUI() {
        ((javax.swing.plaf.basic.BasicInternalFrameUI) getUI()).setNorthPane(null);
        setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
    }
    
    private void mostrarDialogoBusquedaCliente() {
        Frame parent = JOptionPane.getFrameForComponent(this);
        JDialog dialogo = new JDialog(parent, "Buscar Cliente", true);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setSize(500, 400);

        // Panel principal con padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Crear tabla de clientes
        String[] columnas = {"Cédula", "Nombres", "Apellidos"};
        DefaultTableModel modeloTablaClientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaClientes = new JTable(modeloTablaClientes);
        
        // Estilo de la tabla
        tablaClientes.setShowGrid(true);
        tablaClientes.setGridColor(BORDE_GRIS);
        tablaClientes.setRowHeight(30);
        tablaClientes.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaClientes.setSelectionBackground(new Color(232, 240, 254));
        tablaClientes.setSelectionForeground(Color.BLACK);

        // Cargar datos de clientes
        List<Cliente> clientes = controller.getClientes();
        for (Cliente cliente : clientes) {
            Object[] fila = {
                cliente.getCedula(),
                cliente.getNombres(),
                cliente.getApellidos()
            };
            modeloTablaClientes.addRow(fila);
        }

        // ScrollPane para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botón seleccionar
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton btnSeleccionar = createStyledButton("Seleccionar");
        btnSeleccionar.addActionListener(e -> {
            int row = tablaClientes.getSelectedRow();
            if (row != -1) {
                String cedula = (String) modeloTablaClientes.getValueAt(row, 0);
                Cliente cliente = controller.buscarCliente(cedula);
                if (cliente != null) {
                    txtCedulaCliente.setText(cliente.getCedula());
                    txtNombresCliente.setText(cliente.getNombres() + " " + cliente.getApellidos());
                }
                dialogo.dispose();
            }
        });

        panelBotones.add(btnSeleccionar);
        mainPanel.add(panelBotones, BorderLayout.SOUTH);

        dialogo.add(mainPanel);
        dialogo.setLocationRelativeTo(parent);
        dialogo.setVisible(true);
    }

    private void mostrarDialogoBusquedaProducto() {
        Frame parent = JOptionPane.getFrameForComponent(this);
        JDialog dialogo = new JDialog(parent, "Buscar Producto", true);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setSize(600, 400);

        // Panel principal con padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Tabla de productos
        String[] columnas = {"Código", "Nombre", "Precio", "Stock"};
        DefaultTableModel modeloTablaProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaProductosBusqueda = new JTable(modeloTablaProductos);

        // Estilo de la tabla
        tablaProductosBusqueda.setShowGrid(true);
        tablaProductosBusqueda.setGridColor(BORDE_GRIS);
        tablaProductosBusqueda.setRowHeight(30);
        tablaProductosBusqueda.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaProductosBusqueda.setSelectionBackground(new Color(232, 240, 254));
        tablaProductosBusqueda.setSelectionForeground(Color.BLACK);

        // Renderer para la columna de precio
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setHorizontalAlignment(RIGHT);
                if (value instanceof Double) {
                    setText(String.format("$%.2f", value));
                }
                return c;
            }
        };
        tablaProductosBusqueda.getColumnModel().getColumn(2).setCellRenderer(moneyRenderer);

        // Cargar datos de productos
        for (Producto producto : controller.getProductos()) {
            Object[] fila = {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock()
            };
            modeloTablaProductos.addRow(fila);
        }

        // ScrollPane para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaProductosBusqueda);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton btnSeleccionar = createStyledButton("Seleccionar");
        btnSeleccionar.addActionListener(e -> {
            int row = tablaProductosBusqueda.getSelectedRow();
            if (row != -1) {
                String codigo = (String) modeloTablaProductos.getValueAt(row, 0);
                txtCodigoProducto.setText(codigo);
                txtCantidad.requestFocus();
                dialogo.dispose();
            }
        });

        panelBotones.add(btnSeleccionar);
        mainPanel.add(panelBotones, BorderLayout.SOUTH);

        dialogo.add(mainPanel);
        dialogo.setLocationRelativeTo(parent);
        dialogo.setVisible(true);
    }

    private void nuevaFactura() {
        facturaActual = null;
        txtNumeroFactura.setText("");
        txtFecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
        txtCedulaCliente.setText("");
        txtNombresCliente.setText("");
        txtCodigoProducto.setText("");
        txtCantidad.setText("");
        modeloTabla.setRowCount(0);
        actualizarTotales();
        txtCedulaCliente.requestFocus();
    }

    private void buscarCliente() {
        String cedula = txtCedulaCliente.getText().trim();
        Cliente cliente = controller.buscarCliente(cedula);
        if (cliente != null) {
            txtNombresCliente.setText(cliente.getNombres() + " " + cliente.getApellidos());
            txtCodigoProducto.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                "No se encontró el cliente con la cédula: " + cedula,
                "Cliente no encontrado",
                JOptionPane.WARNING_MESSAGE);
            txtCedulaCliente.requestFocus();
            txtCedulaCliente.selectAll();
        }
    }

    private void buscarProducto() {
        String codigo = txtCodigoProducto.getText().trim();
        Producto producto = controller.buscarProducto(codigo);
        if (producto != null) {
            txtCantidad.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                "No se encontró el producto con el código: " + codigo,
                "Producto no encontrado",
                JOptionPane.WARNING_MESSAGE);
            txtCodigoProducto.requestFocus();
            txtCodigoProducto.selectAll();
        }
    }

    private void agregarProductoAFactura() {
        String codigo = txtCodigoProducto.getText().trim();
        Producto producto = controller.buscarProducto(codigo);
        
        if (producto == null) {
            JOptionPane.showMessageDialog(this,
                "Producto no encontrado",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }

            if (cantidad > producto.getStock()) {
                JOptionPane.showMessageDialog(this,
                    "Stock insuficiente. Stock disponible: " + producto.getStock(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            double subtotal = producto.getPrecio() * cantidad;
            double iva = subtotal * 0.12;
            double total = subtotal + iva;

            Object[] fila = {
                producto.getCodigo(),
                producto.getNombre(),
                cantidad,
                producto.getPrecio(),
                subtotal,
                iva,
                total
            };
            modeloTabla.addRow(fila);

            txtCodigoProducto.setText("");
            txtCantidad.setText("");
            txtCodigoProducto.requestFocus();

            actualizarTotales();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "La cantidad debe ser un número entero positivo",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            txtCantidad.requestFocus();
            txtCantidad.selectAll();
        }
    }

    private void actualizarTotales() {
        double subtotal = 0;
        double iva = 0;
        double total = 0;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            subtotal += (double) modeloTabla.getValueAt(i, 4);
            iva += (double) modeloTabla.getValueAt(i, 5);
            total += (double) modeloTabla.getValueAt(i, 6);
        }

        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblIva.setText(String.format("$%.2f", iva));
        lblTotal.setText(String.format("$%.2f", total));
    }

    private void guardarFactura() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Debe agregar al menos un producto a la factura",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtCedulaCliente.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un cliente",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente cliente = controller.buscarCliente(txtCedulaCliente.getText().trim());
        if (cliente == null) {
            JOptionPane.showMessageDialog(this,
                "Cliente no válido",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        facturaActual = controller.crearFactura(cliente);

        // Agregar productos
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String codigo = (String) modeloTabla.getValueAt(i, 0);
            int cantidad = (int) modeloTabla.getValueAt(i, 2);
            Producto producto = controller.buscarProducto(codigo);
            
            if (producto != null) {
                DetalleFactura detalle = new DetalleFactura(producto, cantidad);
                facturaActual.getDetalles().add(detalle);
                
                // Actualizar stock
                producto.setStock(producto.getStock() - cantidad);
            }
        }

        facturaActual.calcularTotales();
        txtNumeroFactura.setText(String.valueOf(facturaActual.getNumero()));
        
        JOptionPane.showMessageDialog(this,
            "Factura guardada exitosamente",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
            
        nuevaFactura();
    }
}