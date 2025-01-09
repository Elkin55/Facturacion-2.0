package vista;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import controlador.SistemaController;
import modelo.Producto;

public class ProductosInternalFrame extends JInternalFrame {
    private SistemaController controller;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private static final Color AZUL_PRINCIPAL = new Color(0, 123, 255);
    private static final Color GRIS_CLARO = new Color(248, 249, 250);
    private static final Color BORDE_GRIS = new Color(220, 220, 220);
    private static final Color ROJO = new Color(220, 53, 69);

    public ProductosInternalFrame(SistemaController controller) {
        super("Productos", true, true, true, true);
        this.controller = controller;
        initComponents();
        customizeUI();
        cargarProductos();
        setSize(800, 500);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnNuevo = createStyledButton("Nuevo Producto", AZUL_PRINCIPAL);
        btnNuevo.addActionListener(e -> mostrarDialogoProducto(null));
        panelSuperior.add(btnNuevo);
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        String[] columnas = {"Código", "Nombre", "Precio", "Stock", "Acciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setShowGrid(true);
        tablaProductos.setGridColor(BORDE_GRIS);
        tablaProductos.setRowHeight(35);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaProductos.setSelectionBackground(new Color(232, 240, 254));
        tablaProductos.setSelectionForeground(Color.BLACK);

        TableColumnModel columnModel = tablaProductos.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(200);

        TableColumn columnAcciones = tablaProductos.getColumnModel().getColumn(4);
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

        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    value = String.format("$%.2f", value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        moneyRenderer.setHorizontalAlignment(JLabel.RIGHT);
        columnModel.getColumn(2).setCellRenderer(moneyRenderer);

        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tablaProductos.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / tablaProductos.getRowHeight();
                
                if (row < tablaProductos.getRowCount() && row >= 0 && column == 4) {
                    Rectangle cellRect = tablaProductos.getCellRect(row, column, false);
                    Point clickPoint = e.getPoint();
                    clickPoint.translate(-cellRect.x, -cellRect.y);
                    
                    Component[] components = ((JPanel)tablaProductos.getCellRenderer(row, column)
                        .getTableCellRendererComponent(tablaProductos, null, false, false, row, column))
                        .getComponents();
                    
                    for (Component c : components) {
                        if (c instanceof JButton && c.getBounds().contains(clickPoint)) {
                            if ("Editar".equals(((JButton)c).getText())) {
                                editarProducto(row);
                            } else {
                                eliminarProducto(row);
                            }
                            break;
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
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

    private void editarProducto(int row) {
        String codigo = (String) modeloTabla.getValueAt(row, 0);
        Producto producto = controller.buscarProducto(codigo);
        if (producto != null) {
            mostrarDialogoProducto(producto);
        }
    }

    private void eliminarProducto(int row) {
        String codigo = (String) modeloTabla.getValueAt(row, 0);
        String nombre = (String) modeloTabla.getValueAt(row, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el producto " + nombre + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            controller.eliminarProducto(codigo);
            cargarProductos();
        }
    }

    private void mostrarDialogoProducto(Producto producto) {
        Frame parent = JOptionPane.getFrameForComponent(this);
        ProductoDialog dialogo = new ProductoDialog(parent, controller, producto);
        dialogo.setVisible(true);
        cargarProductos();
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        for (Producto producto : controller.getProductos()) {
            Object[] fila = {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                ""
            };
            modeloTabla.addRow(fila);
        }
    }
}