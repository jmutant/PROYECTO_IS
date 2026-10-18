package vista;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JOptionPane;
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

    private static final String MENSAJE_NO_DISPONIBLE = "Función aún no disponible";

    private final JButton btnRutasDisponibles;
    private final JButton btnMonedero;
    private final JButton btnRutasOfertantes;
    private final JButton btnPostularCupo;
    private final JButton btnModeloComunitario;
    private final JButton btnConsultarRecorrido;

    public PrincipalPasajeroVentana(Usuario usuario) {
        super(Mensajes.get("principal.pasajero.titulo"));

        btnRutasDisponibles = crearBotonLista(
                "/imagenes/RutasDisponibles1.png",
                "/imagenes/RutasDisponibles2.png");

        btnMonedero = crearBotonLista(
                "/imagenes/MonederoVirtualButton1.png",
                "/imagenes/MonederoVirtualButton2.png");

        btnRutasOfertantes = crearBotonLista(
                "/imagenes/RutasOfertantes1.png",
                "/imagenes/RutasOfertantes2.png");

        btnPostularCupo = crearBotonLista(
                "/imagenes/PostularCupo1.png",
                "/imagenes/PostularCupo2.png");

        btnModeloComunitario = crearBotonLista(
                "/imagenes/ModeloCom1.png",
                "/imagenes/ModeloCom2.png");

        btnConsultarRecorrido = crearBotonLista(
                "/imagenes/ConsultarRecorrido1.png",
                "/imagenes/ConsultarRecorrido2.png");

        // Estas funciones todavía no existen: solo avisan al usuario
        for (JButton boton : new JButton[]{btnRutasOfertantes, btnPostularCupo,
                btnModeloComunitario, btnConsultarRecorrido}) {
            boton.addActionListener(evento -> mostrarNoDisponible());
        }

        // Lista de botones
        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(SEPARACION / 2, 0, SEPARACION / 2, 0);
        panelBotones.add(btnRutasDisponibles, gbc);

        gbc.gridy = 1;
        panelBotones.add(btnMonedero, gbc);

        gbc.gridy = 2;
        panelBotones.add(btnRutasOfertantes, gbc);

        gbc.gridy = 3;
        panelBotones.add(btnPostularCupo, gbc);

        gbc.gridy = 4;
        panelBotones.add(btnModeloComunitario, gbc);

        gbc.gridy = 5;
        panelBotones.add(btnConsultarRecorrido, gbc);

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

    private void mostrarNoDisponible() {
        JOptionPane.showMessageDialog(this, MENSAJE_NO_DISPONIBLE, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    public void setAccionRutasDisponibles(Runnable accion) {
        btnRutasDisponibles.addActionListener(evento -> accion.run());
    }

    public void setAccionMonedero(Runnable accion) {
        btnMonedero.addActionListener(evento -> accion.run());
    }
}