package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

public class ReservarVentana extends JFrame implements ReservarVista {

    private static final long serialVersionUID = 1L;

    // Componentes visuales
    private final JComboBox<Object> comboRutas = new JComboBox<>();
    private final JLabel lblTarifaBase = new JLabel("Tarifa Base: $0.00");
    private final JLabel lblDescuento = new JLabel("Descuento por Rol: 0%");
    private final JLabel lblTotalPagar = new JLabel("Total a Pagar: $0.00");

    private final JButton btnConfirmarReserva = new JButton("Confirmar Reserva y Pagar");
    private final JButton btnRegresar = new JButton("Regresar");

    private double montoCalculadoActual = 0.0;

    public ReservarVentana() {
        super("Campus Express - Reserva de Ruta y Monedero Virtual");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(560, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // -----------------------------------------------------------------
        // 1. PANEL SUPERIOR: Selección de Ruta
        // -----------------------------------------------------------------
        JPanel panelRuta = new JPanel(new BorderLayout(10, 10));
        panelRuta.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "1. Seleccione la Ruta del Itinerario", 
                TitledBorder.LEFT, TitledBorder.TOP));
        
        panelRuta.add(new JLabel(" Rutas disponibles:"), BorderLayout.WEST);
        panelRuta.add(comboRutas, BorderLayout.CENTER);

        // -----------------------------------------------------------------
        // 2. PANEL CENTRAL: Desglose de Cobro (Tarifa Base y Descuento por Rol)
        // -----------------------------------------------------------------
        JPanel panelDesglose = new JPanel(new GridLayout(3, 1, 8, 8));
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

        // Contenedor central agrupado
        JPanel panelContenedor = new JPanel(new BorderLayout(10, 10));
        panelContenedor.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        panelContenedor.add(panelRuta, BorderLayout.NORTH);
        panelContenedor.add(panelDesglose, BorderLayout.CENTER);

        // -----------------------------------------------------------------
        // 3. PANEL INFERIOR: Botones de Acción
        // -----------------------------------------------------------------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        
        btnConfirmarReserva.setBackground(new Color(40, 167, 69));
        btnConfirmarReserva.setForeground(Color.WHITE);
        btnConfirmarReserva.setFont(new Font("Arial", Font.BOLD, 13));

        panelBotones.add(btnRegresar);
        panelBotones.add(btnConfirmarReserva);

        // Añadir a la ventana principal
        add(panelContenedor, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
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
        btnConfirmarReserva.addActionListener(e -> accion.run());
    }

    @Override
    public void setAccionRegresar(Runnable accion) {
        btnRegresar.addActionListener(e -> accion.run());
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