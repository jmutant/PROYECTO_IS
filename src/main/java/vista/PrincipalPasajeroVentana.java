package vista;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import modelo.Usuario;
import util.Mensajes;

/** Pantalla principal de los pasajeros (Estudiante, Empleado, Profesor y Público General). */
public class PrincipalPasajeroVentana extends PrincipalVentanaBase {

    private static final long serialVersionUID = 1L;

    public PrincipalPasajeroVentana(Usuario usuario) {
        super(Mensajes.get("principal.pasajero.titulo"));

        JLabel pendiente = new JLabel(Mensajes.get("principal.pasajero.pendiente"), SwingConstants.CENTER);
        pendiente.setForeground(Color.GRAY);

        armarVentana(usuario, pendiente);
    }
}
