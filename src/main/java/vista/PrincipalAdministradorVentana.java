package vista;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import modelo.Usuario;

public class PrincipalAdministradorVentana extends PrincipalVentanaBase {

    private static final long serialVersionUID = 1L;

    // botones
    private final JButton btnGestionFlota;
    private final JButton btnGestionItinerarios;
    private final JButton btnCrearAdministrador = new JButton("Crear Administrador");
    //Punto de integración de HU-002 (Gestión de Flota de Unidades) y Punto de integración de HU-003 (Gestionar Itinerarios).

    public PrincipalAdministradorVentana(Usuario usuario) {
        super("Panel de Control"); // Título de la ventana

        //declaraciones para el hover de los botones, con imagenes
        btnGestionFlota = crearBotonConImagen(
            "/imagenes/GestionDeFlotaButton1.png", 
            "/imagenes/GestionDeFlotaButton2.png", 
            320, 150
        );

        btnGestionItinerarios = crearBotonConImagen(
            "/imagenes/GestionarItinerariosButton1.png", 
            "/imagenes/GestionarItinerariosButton2.png", 
            320, 150
        );

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false); // Fundamental para que el fondo de PrincipalVentanaBase se vea

        // preestablecer posiciones de los botones (GridBagConstraints)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Margen de 20 píxeles alrededor de cada botón
        gbc.gridy = 0; // Ambos estarán en la misma fila

        // boton de Gestionar Flota en la columna 0
        gbc.gridx = 0;
        panelBotones.add(btnGestionFlota, gbc);

        // boton de Gestionar Itinerarios en la columna 1
        gbc.gridx = 1;
        panelBotones.add(btnGestionItinerarios, gbc);

        // boton de Crear Administrador: segunda fila, centrado debajo de los botones "Gestionar..."
        btnCrearAdministrador.setFont(btnCrearAdministrador.getFont().deriveFont(Font.BOLD, 16f));
        btnCrearAdministrador.setPreferredSize(new Dimension(320, 50));
        btnCrearAdministrador.setFocusPainted(false);
        btnCrearAdministrador.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panelBotones.add(btnCrearAdministrador, gbc);

        armarVentana(usuario, panelBotones);
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
}

/* 
// old code de referencia x si se cambia la forma en la se llaman los botones
package vista;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelo.Usuario;
import util.Mensajes; */

/* public class PrincipalAdministradorVentana extends PrincipalVentanaBase {

    private static final long serialVersionUID = 1L;

    private final JButton botonFlota = new JButton(Mensajes.get("principal.administrador.flota"));
    private final JButton botonItinerarios = new JButton(Mensajes.get("principal.administrador.itinerarios"));

    private Runnable accionGestionFlota = () -> avisarModuloEnDesarrollo("HU-002");
    private Runnable accionGestionItinerarios = () -> avisarModuloEnDesarrollo("HU-003");

    public PrincipalAdministradorVentana(Usuario usuario) {
        super(Mensajes.get("principal.administrador.titulo"));

        botonFlota.setPreferredSize(new Dimension(220, 64));
        botonItinerarios.setPreferredSize(new Dimension(220, 64));
        botonFlota.addActionListener(evento -> accionGestionFlota.run());
        botonItinerarios.addActionListener(evento -> accionGestionItinerarios.run());

        JPanel modulos = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        modulos.add(botonFlota);
        modulos.add(botonItinerarios);

        armarVentana(usuario, modulos);
    }


    public void setAccionGestionFlota(Runnable accion) {
        this.accionGestionFlota = accion;
    }

    //
    public void setAccionGestionItinerarios(Runnable accion) {
        this.accionGestionItinerarios = accion;
    }

    private void avisarModuloEnDesarrollo(String historia) {
        JOptionPane.showMessageDialog(this, Mensajes.get("principal.moduloEnDesarrollo", historia),
                getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }
}
 */