package vista;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import modelo.Factura;
import modelo.DetalleFactura;

public class DetalleFacturaDialog extends JDialog {
    private Factura factura;
    private JTable tablaDetalles;
    private DefaultTableModel modeloTabla;
    private static final Color AZUL_PRINCIPAL = new Color(0, 123, 255);
    private static final Color GRIS_CLARO = new Color(248, 249, 250);
    private static final Color BORDE_GRIS = new Color(220, 220, 220);
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public DetalleFacturaDialog(Frame parent, Factura factura) {
        super(parent, "Detalle de Factura", true);
        this.factura = factura;
        initComponents();
        setSize(700, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelEncabezado = new JPanel(new GridLayout(3, 2, 10, 5));
        panelEncabezado.setBackground(Color.WHITE);
        panelEncabezado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE_GRIS),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        agregarCampoInfo(panelEncabezado, "No. Factura:", String.valueOf(factura.getNumero()));
        agregarCampoInfo(panelEncabezado, "Fecha:", sdf.format(factura.getFecha()));
        agregarCampoInfo(panelEncabezado, "Estado:", factura.isAnulada() ? "ANULADA" : "ACTIVA");
        agregarCampoInfo(panelEncabezado, "Cliente:", factura.getCliente().getNombres() + " " + 
                                                     factura.getCliente().getApellidos());
        agregarCampoInfo(panelEncabezado, "Cédula:", factura.getCliente().getCedula());
        agregarCampoInfo(panelEncabezado, "Dirección:", factura.getCliente().getDireccion());

        mainPanel.add(panelEncabezado, BorderLayout.NORTH);

        String[] columnas = {"Código", "Producto", "Cantidad", "Precio Unit.", "Subtotal", "IVA", "Total"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaDetalles = new JTable(modeloTabla);
        
        tablaDetalles.setShowGrid(true);
        tablaDetalles.setGridColor(BORDE_GRIS);
        tablaDetalles.setRowHeight(30);
        tablaDetalles.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaDetalles.setEnabled(false);

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

        for (int i = 3; i < tablaDetalles.getColumnCount(); i++) {
            tablaDetalles.getColumnModel().getColumn(i).setCellRenderer(moneyRenderer);
        }

        for (DetalleFactura detalle : factura.getDetalles()) {
            Object[] fila = {
                detalle.getProducto().getCodigo(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getProducto().getPrecio(),
                detalle.getSubtotal(),
                detalle.getIva(),
                detalle.getTotal()
            };
            modeloTabla.addRow(fila);
        }

        JScrollPane scrollPane = new JScrollPane(tablaDetalles);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel panelTotales = new JPanel(new GridLayout(3, 2, 5, 5));
        panelTotales.setBackground(Color.WHITE);
        panelTotales.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        agregarCampoTotal(panelTotales, "Subtotal:", String.format("$%.2f", factura.getSubtotal()));
        agregarCampoTotal(panelTotales, "IVA:", String.format("$%.2f", factura.getIva()));
        agregarCampoTotal(panelTotales, "Total:", String.format("$%.2f", factura.getTotal()));

        JPanel panelTotalesWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotalesWrapper.setBackground(Color.WHITE);
        panelTotalesWrapper.add(panelTotales);
        
        mainPanel.add(panelTotalesWrapper, BorderLayout.SOUTH);

        add(mainPanel);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setBackground(Color.WHITE);
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(AZUL_PRINCIPAL);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setPreferredSize(new Dimension(100, 35));
        btnCerrar.addActionListener(e -> dispose());
        panelBoton.add(btnCerrar);
        
        add(panelBoton, BorderLayout.SOUTH);
    }

    private void agregarCampoInfo(JPanel panel, String etiqueta, String valor) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(lblEtiqueta);
        panel.add(lblValor);
    }

    private void agregarCampoTotal(JPanel panel, String etiqueta, String valor) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 12));
        lblEtiqueta.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 14));
        lblValor.setHorizontalAlignment(SwingConstants.RIGHT);
        
        panel.add(lblEtiqueta);
        panel.add(lblValor);
    }
}