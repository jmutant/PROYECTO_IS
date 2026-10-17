package vista;

import java.awt.event.ItemListener;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

// Ventana "reserva": se abre desde Rutas Disponibles. El dibujo y la posición de los elementos están en ReservarPanel.
public class ReservarVentana extends JFrame implements ReservarVista {

    private static final long serialVersionUID = 1L;

    private final ReservarPanel panel = new ReservarPanel();
    private double montoCalculadoActual = 0.0;

    public ReservarVentana() {
        super("Campus Express - Reserva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void mostrarItinerarios(List<?> itinerarios) {
        panel.getComboRutas().removeAllItems();
        for (Object it : itinerarios) {
            panel.getComboRutas().addItem(it);
        }
    }

    @Override
    public Object getRutaSeleccionada() {
        return panel.getComboRutas().getSelectedItem();
    }

    @Override
    public void mostrarDesgloseCobro(double tarifaBase, double porcentajeSubsidio, double montoFinal) {
        this.montoCalculadoActual = montoFinal;
        panel.setDesglose(
                String.format(Locale.US, "%,.2f Bs", tarifaBase),
                String.format(Locale.US, "%.2f%%", porcentajeSubsidio),
                String.format(Locale.US, "%,.2f", montoFinal));
    }

    @Override
    public double getMontoCalculado() {
        return this.montoCalculadoActual;
    }

    @Override
    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Reserva exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Reserva", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarTicketDigital(Object ticket) {
        JOptionPane.showMessageDialog(this,
                "=== TICKET DIGITAL GENERADO ===\n" + ticket.toString(),
                "Comprobante de Viaje",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void setAccionSeleccionarRuta(ItemListener listener) {
        panel.getComboRutas().addItemListener(listener);
    }

    @Override
    public void setAccionConfirmarReserva(Runnable accion) {
        panel.getBotonReservar().addActionListener(evento -> accion.run());
    }

    @Override
    public void setAccionRegresar(Runnable accion) {
        panel.getBotonRegresar().addActionListener(evento -> accion.run());
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
