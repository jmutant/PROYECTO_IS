package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

// Ventana "reserva": se abre desde Rutas Disponibles. Por ahora solo tiene el botón para regresar.
public class ReservarVentana extends JFrame implements ReservarVista {

    private static final long serialVersionUID = 1L;

    private static final int ANCHO = 1000;
    private static final int ALTO = 650;
    private final JComboBox<Object> comboRutas = new JComboBox<>();
    private final JLabel lblTarifaBase = new JLabel("Tarifa Base: $0.00");
    private final JLabel lblDescuento = new JLabel("Descuento por Rol: 0%");
    private final JLabel lblTotalPagar = new JLabel("Total a Pagar: $0.00");

    private final JButton botonConfirmarReserva = new JButton("Confirmar Reserva y Pagar");

    private double montoCalculadoActual = 0.0;

    private final RecursosImagen.Escalado fondo =
            new RecursosImagen.Escalado("/imagenes/MonederoPantalla.png");

    private final BotonImagen botonRegresar = new BotonImagen(
            "/imagenes/cross1.png",
            "/imagenes/cross2.png",
            "Regresar");

    public ReservarVentana() {
        super("Campus Express - Reserva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        construirInterfaz();
    }

    private void construirInterfaz() {
        // 1. Panel Principal de Fondo con Imagen
        JPanel fondoPanel = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(fondo.obtener(getWidth(), getHeight()), 0, 0, null);
                g2.dispose();
            }
        };
        fondoPanel.setOpaque(false);
        fondoPanel.setBorder(new EmptyBorder(20, 55, 55, 55));

        // 2. Fila Superior: Botón Regresar alineado a la derecha
        JPanel filaRegresar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaRegresar.setOpaque(false);
        filaRegresar.setBorder(new EmptyBorder(0, 0, 14, 0));
        botonRegresar.setPreferredSize(new Dimension(58, 56));
        filaRegresar.add(botonRegresar);
        fondoPanel.add(filaRegresar, BorderLayout.NORTH);

        // 3. Tarjeta Blanca Central con Bordes Redondeados
        JPanel tarjeta = new JPanel(new BorderLayout(15, 15)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 42, 42);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tarjeta.setOpaque(false);

        // --- CONTENIDO DE LA TARJETA ---

        // 3.1. Panel Superior: Selección de Ruta
        JPanel panelRuta = new JPanel(new BorderLayout(10, 10));
        panelRuta.setOpaque(false);
        panelRuta.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "1. Seleccione la Ruta del Itinerario",
                TitledBorder.LEFT, TitledBorder.TOP));
        panelRuta.add(new JLabel(" Rutas disponibles:"), BorderLayout.WEST);
        panelRuta.add(comboRutas, BorderLayout.CENTER);

        // 3.2. Panel Central: Desglose de Cobro
        JPanel panelDesglose = new JPanel(new GridLayout(3, 1, 8, 8));
        panelDesglose.setOpaque(false);
        panelDesglose.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "2. Desglose de Tarifa según su Rol",
                TitledBorder.LEFT, TitledBorder.TOP));

        lblTarifaBase.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDescuento.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTotalPagar.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalPagar.setForeground(new Color(0, 102, 204));

        panelDesglose.add(lblTarifaBase);
        panelDesglose.add(lblDescuento);
        panelDesglose.add(lblTotalPagar);

        // Agrupar Ruta y Desglose en un contenedor central dentro de la tarjeta
        JPanel panelContenedor = new JPanel(new BorderLayout(10, 10));
        panelContenedor.setOpaque(false);
        panelContenedor.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        panelContenedor.add(panelRuta, BorderLayout.NORTH);
        panelContenedor.add(panelDesglose, BorderLayout.CENTER);

        // 3.3. Panel Inferior: Botones de Acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setOpaque(false);

        botonConfirmarReserva.setBackground(new Color(40, 167, 69));
        botonConfirmarReserva.setForeground(Color.WHITE);
        botonConfirmarReserva.setFont(new Font("Arial", Font.BOLD, 13));
        panelBotones.add(botonConfirmarReserva);

        // Agregar subpaneles a la tarjeta
        tarjeta.add(panelContenedor, BorderLayout.CENTER);
        tarjeta.add(panelBotones, BorderLayout.SOUTH);

        // Agregar la tarjeta al panel con imagen de fondo
        fondoPanel.add(tarjeta, BorderLayout.CENTER);

        // 4. Configurar el ContentPane de la Ventana (JFrame)
        setContentPane(fondoPanel);
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
    }

    @Override
    public void mostrarItinerarios(List<?> itinerarios) {
        comboRutas.removeAllItems();
        for (Object it : itinerarios) {
            comboRutas.addItem(it);
        }
    }

    @Override
    public Object getRutaSeleccionada() {
        return comboRutas.getSelectedItem();
    }

    @Override
    public void mostrarDesgloseCobro(double tarifaBase, double porcentajeSubsidio, double montoFinal) {
        this.montoCalculadoActual = montoFinal;
        lblTarifaBase.setText(String.format("Tarifa Base: $%.2f", tarifaBase));
        lblDescuento.setText(String.format("Subsidio / Descuento Aplicado: %.1f%%", porcentajeSubsidio));
        lblTotalPagar.setText(String.format("Total a Pagar del Monedero: $%.2f", montoFinal));

        // Forzar al panel a repintar el texto actualizado en pantalla
        lblTarifaBase.revalidate();
        lblTarifaBase.repaint();
    }

    @Override
    public double getMontoCalculado() {
        return this.montoCalculadoActual;
    }

    @Override
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Reserva", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarTicketDigital(Object ticket) {
        // Podrías abrir un pequeño diálogo o panel que muestre el ticket digital generado
        JOptionPane.showMessageDialog(this, 
            "=== TICKET DIGITAL GENERADO ===\n" + ticket.toString(), 
            "Comprobante de Viaje", 
            JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void setAccionSeleccionarRuta(ItemListener listener) {
        comboRutas.addItemListener(listener);
    }

    @Override
    public void setAccionConfirmarReserva(Runnable accion) {
        botonConfirmarReserva.addActionListener(evento -> accion.run());
    }

    @Override
    public void setAccionRegresar(Runnable accion) {
        botonRegresar.addActionListener(evento -> accion.run());
    }

    @Override
    public void mostrar() {
        setVisible(true);
    }

    @Override
    public void cerrar() {
        dispose();
    }
}
