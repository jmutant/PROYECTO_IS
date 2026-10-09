package vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import modelo.Usuario;

public class PrincipalAdministradorVentana extends PrincipalVentanaBase {

    private static final long serialVersionUID = 1L;

    // Las imágenes con proporción 7.5:1 ya q las imagenes de los botones pueden deformarse 
    private static final int ANCHO_BOTON = 450;
    private static final int ALTO_BOTON = 60;
    private static final int SEPARACION = 16;

    // botones
    private final JButton btnGestionFlota;
    private final JButton btnGestionItinerarios;
    private final JButton btnMonedero;
    private final JButton btnCrearAdministrador;
    //Punto de integración de HU-002 (Gestión de Flota de Unidades) y Punto de integración de HU-003 (Gestionar Itinerarios).

    public PrincipalAdministradorVentana(Usuario usuario) {
        super("Panel de Control");

        // Botones con imagen normal y hover
        btnGestionFlota = crearBotonLista(
            "/imagenes/GestionDeFlotaButton1.png",
            "/imagenes/GestionDeFlotaButton2.png");

        btnGestionItinerarios = crearBotonLista(
            "/imagenes/GestionarItinerariosButton1.png",
            "/imagenes/GestionarItinerariosButton2.png");

        btnMonedero = crearBotonLista(
            "/imagenes/MonederoVirtualButton1.png",
            "/imagenes/MonederoVirtualButton2.png");

        btnCrearAdministrador = crearBotonLista(
            "/imagenes/CrearAdmin1.png",
            "/imagenes/CrearAdmin2.png");

        // Lista d botones
        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(SEPARACION / 2, 0, SEPARACION / 2, 0);

        gbc.gridy = 0;
        panelBotones.add(btnGestionFlota, gbc);

        gbc.gridy = 1;
        panelBotones.add(btnGestionItinerarios, gbc);

        gbc.gridy = 2;
        panelBotones.add(btnMonedero, gbc);

        gbc.gridy = 3;
        panelBotones.add(btnCrearAdministrador, gbc);

        armarVentana(usuario, panelBotones);
    }

    private JButton crearBotonLista(String rutaNormal, String rutaHover) {
        BotonImagen boton = new BotonImagen(rutaNormal, rutaHover, rutaHover);
        java.awt.Dimension tamano = new java.awt.Dimension(ANCHO_BOTON, ALTO_BOTON);
        boton.setPreferredSize(tamano);
        boton.setMinimumSize(tamano);
        boton.setMaximumSize(tamano);
        return boton;
    }

    // Métodos para asignar los eventos a los botones desde el controlador

    public void setAccionGestionFlota(Runnable accion) {
        btnGestionFlota.addActionListener(evento -> accion.run());
    }

    public void setAccionGestionItinerarios(Runnable accion) {
        btnGestionItinerarios.addActionListener(evento -> accion.run());
    }

    public void setAccionCrearAdministrador(Runnable accion) {
        btnCrearAdministrador.addActionListener(evento -> accion.run());
    }

    public void setAccionMonedero(Runnable accion) {
        btnMonedero.addActionListener(evento -> accion.run());
    }
}
