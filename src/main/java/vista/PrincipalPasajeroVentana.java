package vista;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import modelo.Usuario;
import util.Mensajes;

// Pantalla principal de los pasajeros (Estudiante, Empleado, Profesor y Público General)
public class PrincipalPasajeroVentana extends PrincipalVentanaBase {

    private static final long serialVersionUID = 1L;

    private final JButton btnMonedero;

    public PrincipalPasajeroVentana(Usuario usuario) {
        super(Mensajes.get("principal.pasajero.titulo"));

        btnMonedero = crearBotonConImagen(
                "/imagenes/MonederoVirtualButton1.png",
                "/imagenes/MonederoVirtualButton2.png",310, 177);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panel.setOpaque(false);

        JLabel pendiente = new JLabel(Mensajes.get("principal.pasajero.pendiente"), SwingConstants.CENTER);
        pendiente.setForeground(Color.GRAY);

        panel.add(btnMonedero);
        panel.add(pendiente);

        armarVentana(usuario, panel);
    }

    public void setAccionMonedero(Runnable accion) {
        btnMonedero.addActionListener(evento -> accion.run());
    }
}
