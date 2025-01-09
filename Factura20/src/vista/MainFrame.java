package vista;

import javax.swing.*;
import java.awt.*;
import controlador.SistemaController;

public class MainFrame extends JFrame {
    private SistemaController controller;
    private JDesktopPane desktopPane;
    private static final Color AZUL_PRINCIPAL = new Color(51, 122, 183);
    private static final Color FONDO_VENTANA = Color.WHITE;
    private static final Color GRIS_CLARO = new Color(248, 249, 250);
    private static final Color BORDE_GRIS = new Color(220, 220, 220);

    public MainFrame() {
        controller = new SistemaController();
        initComponents();
        createMenuBar();
        customizeUI();
    }

    private void initComponents() {
        setTitle("Sistema de facturación 2.0");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configurar JDesktopPane con fondo personalizado
        desktopPane = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(FONDO_VENTANA);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Dibujar un patrón de puntos suave en el fondo
                g.setColor(new Color(240, 240, 240));
                for (int x = 0; x < getWidth(); x += 20) {
                    for (int y = 0; y < getHeight(); y += 20) {
                        g.fillOval(x, y, 2, 2);
                    }
                }
            }
        };
        setContentPane(desktopPane);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(FONDO_VENTANA);
        menuBar.setBorder(BorderFactory.createLineBorder(BORDE_GRIS));
        
        // Menú Archivo
        JMenu archivo = createMenu("Archivo");
        JMenuItem salir = createMenuItem("Salir");
        salir.addActionListener(e -> confirmarSalir());
        archivo.add(salir);
        
        // Menú Procesos
        JMenu procesos = createMenu("Procesos");
        JMenuItem clientes = createMenuItem("Clientes");
        JMenuItem productos = createMenuItem("Productos");
        JMenuItem facturacion = createMenuItem("Facturación");
        JMenuItem listaFacturas = createMenuItem("Listado de Facturas");
        
        clientes.addActionListener(e -> mostrarClientes());
        productos.addActionListener(e -> mostrarProductos());
        facturacion.addActionListener(e -> mostrarFacturacion());
        listaFacturas.addActionListener(e -> mostrarListadoFacturas());
        
        procesos.add(clientes);
        procesos.add(productos);
        procesos.addSeparator();
        procesos.add(facturacion);
        procesos.add(listaFacturas);
        
        // Menú Ayuda
        JMenu ayuda = createMenu("Ayuda");
        JMenuItem acerca = createMenuItem("Acerca de");
        acerca.addActionListener(e -> mostrarAcercaDe());
        ayuda.add(acerca);
        
        menuBar.add(archivo);
        menuBar.add(procesos);
        menuBar.add(ayuda);
        
        setJMenuBar(menuBar);
    }

    private JMenu createMenu(String texto) {
        JMenu menu = new JMenu(texto);
        menu.setForeground(Color.BLACK);
        menu.setFont(new Font("Arial", Font.BOLD, 12));
        return menu;
    }

    private JMenuItem createMenuItem(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setBackground(FONDO_VENTANA);
        item.setFont(new Font("Arial", Font.PLAIN, 12));
        return item;
    }

    private void customizeUI() {
        // Establecer Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Configurar colores y fuentes por defecto
        UIManager.put("Panel.background", FONDO_VENTANA);
        UIManager.put("OptionPane.background", FONDO_VENTANA);
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 12));
    }

    private void mostrarClientes() {
        ClientesInternalFrame clientesFrame = new ClientesInternalFrame(controller);
        agregarVentanaInterna(clientesFrame);
    }

    private void mostrarProductos() {
        ProductosInternalFrame productosFrame = new ProductosInternalFrame(controller);
        agregarVentanaInterna(productosFrame);
    }

    private void mostrarFacturacion() {
        FacturacionInternalFrame facturacionFrame = new FacturacionInternalFrame(controller);
        agregarVentanaInterna(facturacionFrame);
    }

    private void mostrarListadoFacturas() {
        FacturasInternalFrame facturasFrame = new FacturasInternalFrame(controller);
        agregarVentanaInterna(facturasFrame);
    }

    private void agregarVentanaInterna(JInternalFrame frame) {
        // Verificar si ya existe una ventana del mismo tipo
        for (JInternalFrame ventana : desktopPane.getAllFrames()) {
            if (ventana.getClass().equals(frame.getClass())) {
                try {
                    ventana.setSelected(true);
                    return;
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // Si no existe, agregar la nueva ventana
        desktopPane.add(frame);
        centrarVentana(frame);
        frame.setVisible(true);

        // Intentar seleccionar la ventana
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private void centrarVentana(JInternalFrame frame) {
        Dimension desktopSize = desktopPane.getSize();
        Dimension frameSize = frame.getSize();
        frame.setLocation(
            (desktopSize.width - frameSize.width) / 2,
            (desktopSize.height - frameSize.height) / 2
        );
    }

    private void confirmarSalir() {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea salir?\nLos datos no guardados se perderán.",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(
            this,
            "Sistema de Facturación v2.0.0\n" +
            "Desarrollado por [Tu Nombre]\n" +
            "© 2024 Todos los derechos reservados",
            "Acerca de",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        // Ejecutar la aplicación en el EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Establecer Look and Feel del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}