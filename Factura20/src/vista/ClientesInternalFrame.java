package vista;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import controlador.SistemaController;
import modelo.Cliente;

public class ClientesInternalFrame extends JInternalFrame {
    private SistemaController controller;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private static final Color AZUL_PRINCIPAL = new Color(0, 123, 255);
    private static final Color GRIS_CLARO = new Color(248, 249, 250);
    private static final Color BORDE_GRIS = new Color(220, 220, 220);
    private static final Color ROJO = new Color(220, 53, 69);

    public ClientesInternalFrame(SistemaController controller) {
        super("Clientes", true, true, true, true);
        this.controller = controller;
        initComponents();
        customizeUI();
        cargarClientes();
        setSize(800, 500);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnNuevo = createStyledButton("Nuevo Cliente", AZUL_PRINCIPAL);
        btnNuevo.addActionListener(e -> mostrarDialogoCliente(null));
        panelSuperior.add(btnNuevo);

        add(panelSuperior, BorderLayout.NORTH);

        
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        String[] columnas = {"Cédula", "Nombres", "Apellidos", "Acciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaClientes = new JTable(modeloTabla);
        
        tablaClientes.setShowGrid(true);
        tablaClientes.setGridColor(BORDE_GRIS);
        tablaClientes.setRowHeight(35);
        tablaClientes.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaClientes.setSelectionBackground(new Color(232, 240, 254));
        tablaClientes.setSelectionForeground(Color.BLACK);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(Color.WHITE);
        
        for (int i = 0; i < 3; i++) {
            tablaClientes.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        TableColumn columnAcciones = tablaClientes.getColumnModel().getColumn(3);
        columnAcciones.setCellRenderer(new DefaultTableCellRenderer() {
            private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            private JButton btnEditar = createStyledButton("Editar", AZUL_PRINCIPAL);
            private JButton btnEliminar = createStyledButton("Eliminar", ROJO);

            {
                btnEditar.setPreferredSize(new Dimension(75, 25));
                btnEliminar.setPreferredSize(new Dimension(75, 25));
                panel.setBackground(Color.WHITE);
                panel.add(btnEditar);
                panel.add(btnEliminar);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                return panel;
            }
        });

        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tablaClientes.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / tablaClientes.getRowHeight();
                
                if (row < tablaClientes.getRowCount() && row >= 0 && column == 3) {
                    Rectangle cellRect = tablaClientes.getCellRect(row, column, false);
                    Point clickPoint = e.getPoint();
                    clickPoint.translate(-cellRect.x, -cellRect.y);
                    
                    Component[] components = ((JPanel)tablaClientes.getCellRenderer(row, column)
                        .getTableCellRendererComponent(tablaClientes, null, false, false, row, column))
                        .getComponents();
                    
                    for (Component c : components) {
                        if (c instanceof JButton && c.getBounds().contains(clickPoint)) {
                            if ("Editar".equals(((JButton)c).getText())) {
                                editarClienteSeleccionado(row);
                            } else {
                                eliminarClienteSeleccionado(row);
                            }
                            break;
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
        panelCentral.add(scrollPane);

        add(panelCentral);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }

    private void customizeUI() {
        ((javax.swing.plaf.basic.BasicInternalFrameUI) getUI()).setNorthPane(null);
        setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
        setFrameIcon(null);
    }

    private void editarClienteSeleccionado(int row) {
        String cedula = (String) modeloTabla.getValueAt(row, 0);
        Cliente cliente = controller.buscarCliente(cedula);
        if (cliente != null) {
            mostrarDialogoCliente(cliente);
        }
    }

    private void eliminarClienteSeleccionado(int row) {
        String cedula = (String) modeloTabla.getValueAt(row, 0);
        String nombre = modeloTabla.getValueAt(row, 1) + " " + modeloTabla.getValueAt(row, 2);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al cliente " + nombre + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            controller.eliminarCliente(cedula);
            cargarClientes();
        }
    }

    private void mostrarDialogoCliente(Cliente cliente) {
        Frame parent = JOptionPane.getFrameForComponent(this);
        ClienteDialog dialogo = new ClienteDialog(parent, controller, cliente);
        dialogo.setVisible(true);
        cargarClientes();
    }

    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        for (Cliente cliente : controller.getClientes()) {
            Object[] fila = {
                cliente.getCedula(),
                cliente.getNombres(),
                cliente.getApellidos(),
                "Acciones"
            };
            modeloTabla.addRow(fila);
        }
    }
}