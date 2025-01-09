package vista;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import controlador.SistemaController;
import modelo.Factura;

public class FacturasInternalFrame extends JInternalFrame {
    private SistemaController controller;
    private JTable tablaFacturas;
    private DefaultTableModel modeloTabla;
    private static final Color AZUL_PRINCIPAL = new Color(0, 123, 255);
    private static final Color GRIS_CLARO = new Color(248, 249, 250);
    private static final Color BORDE_GRIS = new Color(220, 220, 220);
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public FacturasInternalFrame(SistemaController controller) {
        super("Listado de Facturas", true, true, true, true);
        this.controller = controller;
        initComponents();
        customizeUI();
        cargarFacturas();
        setSize(800, 500);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"No. Factura", "Fecha", "Cliente", "Total", "Estado", "Acciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFacturas = new JTable(modeloTabla);
        
        tablaFacturas.setShowGrid(true);
        tablaFacturas.setGridColor(BORDE_GRIS);
        tablaFacturas.setRowHeight(35);
        tablaFacturas.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaFacturas.setSelectionBackground(new Color(232, 240, 254));
        tablaFacturas.setSelectionForeground(Color.BLACK);

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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tablaFacturas.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
        tablaFacturas.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); 
        tablaFacturas.getColumnModel().getColumn(3).setCellRenderer(moneyRenderer); 
        tablaFacturas.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); 

        tablaFacturas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int column = tablaFacturas.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / tablaFacturas.getRowHeight();

                if (row < tablaFacturas.getRowCount() && row >= 0 && 
                    column == 5) { 
                    mostrarOpcionesFactura(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void mostrarOpcionesFactura(int row) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(BORDE_GRIS));

        JMenuItem menuVer = new JMenuItem("Ver Detalle");
        JMenuItem menuAnular = new JMenuItem("Anular Factura");

        menuVer.setBackground(Color.WHITE);
        menuAnular.setBackground(Color.WHITE);

        boolean facturaAnulada = "ANULADA".equals(modeloTabla.getValueAt(row, 4));
        menuAnular.setEnabled(!facturaAnulada);

        menuVer.addActionListener(e -> mostrarDetalleFactura(row));
        menuAnular.addActionListener(e -> anularFactura(row));

        popupMenu.add(menuVer);
        popupMenu.add(menuAnular);

        Rectangle cellRect = tablaFacturas.getCellRect(row, 5, false);
        popupMenu.show(tablaFacturas, cellRect.x, cellRect.y + cellRect.height);
    }

    private void mostrarDetalleFactura(int row) {
        int numeroFactura = (int) modeloTabla.getValueAt(row, 0);
        Factura factura = controller.buscarFactura(numeroFactura);
        if (factura != null) {
            Frame parent = JOptionPane.getFrameForComponent(this);
            DetalleFacturaDialog dialogo = new DetalleFacturaDialog(parent, factura);
            dialogo.setVisible(true);
        }
    }

    private void anularFactura(int row) {
        int numeroFactura = (int) modeloTabla.getValueAt(row, 0);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de anular la factura No. " + numeroFactura + "?",
            "Confirmar Anulación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirmacion == JOptionPane.YES_OPTION) {
            controller.anularFactura(numeroFactura);
            cargarFacturas();
        }
    }

    private void cargarFacturas() {
        modeloTabla.setRowCount(0);
        for (Factura factura : controller.getFacturas()) {
            Object[] fila = {
                factura.getNumero(),
                sdf.format(factura.getFecha()),
                factura.getCliente().getNombres() + " " + factura.getCliente().getApellidos(),
                factura.getTotal(),
                factura.isAnulada() ? "ANULADA" : "ACTIVA",
                "Acciones"
            };
            modeloTabla.addRow(fila);
        }
    }

    private void customizeUI() {
        ((javax.swing.plaf.basic.BasicInternalFrameUI) getUI()).setNorthPane(null);
        setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
    }
}
