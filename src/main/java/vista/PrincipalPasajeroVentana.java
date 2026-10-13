package vista;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import modelo.Usuario;
import util.Mensajes;

// Pantalla principal de los pasajeros (Estudiante, Empleado, Profesor y Público General)
public class PrincipalPasajeroVentana extends PrincipalVentanaBase {

    private static final long serialVersionUID = 1L;

    // Mismo tamaño de botón en lista que usa la pantalla del administrador
    private static final int ANCHO_BOTON = 450;
    private static final int ALTO_BOTON = 60;
    private static final int SEPARACION = 16;

    private final JButton btnMonedero;

    public PrincipalPasajeroVentana(Usuario usuario) {
        super(Mensajes.get("principal.pasajero.titulo"));

        btnMonedero = crearBotonLista(
                "/imagenes/MonederoVirtualButton1.png",
                "/imagenes/MonederoVirtualButton2.png");

        // Lista de botones (por ahora solo el monedero)
        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(SEPARACION / 2, 0, SEPARACION / 2, 0);
        panelBotones.add(btnMonedero, gbc);

        armarVentana(usuario, panelBotones);
    }

    private JButton crearBotonLista(String rutaNormal, String rutaHover) {
        BotonImagen boton = new BotonImagen(rutaNormal, rutaHover, rutaHover);
        Dimension tamano = new Dimension(ANCHO_BOTON, ALTO_BOTON);
        boton.setPreferredSize(tamano);
        boton.setMinimumSize(tamano);
        boton.setMaximumSize(tamano);
        return boton;
    }

    public void setAccionMonedero(Runnable accion) {
        btnMonedero.addActionListener(evento -> accion.run());
    }
}