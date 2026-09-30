package vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import modelo.Usuario;

public class PrincipalAdministradorVentana extends PrincipalVentanaBase {

    private static final long serialVersionUID = 1L;

    // 1. Declarar ambos botones
    private final JButton btnGestionFlota;
    private final JButton btnGestionItinerarios;

    public PrincipalAdministradorVentana(Usuario usuario) {
        super("Panel de Control"); // Título de la ventana

        // 2. Inicializar los botones llamando al método que creamos en la clase padre
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

        // 3. Crear el panel central que contendrá los botones
        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false); // Fundamental para que el fondo de PrincipalVentanaBase se vea

        // 4. Configurar las reglas de posicionamiento (GridBagConstraints)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Margen de 20 píxeles alrededor de cada botón
        gbc.gridy = 0; // Ambos estarán en la misma fila

        // Posicionar el primer botón (Gestión de Flota) en la columna 0
        gbc.gridx = 0;
        panelBotones.add(btnGestionFlota, gbc);

        // Posicionar el segundo botón (Gestionar Itinerarios) en la columna 1
        gbc.gridx = 1;
        panelBotones.add(btnGestionItinerarios, gbc);

        // 5. Llamar al método de la clase padre (PrincipalVentanaBase) para emsamblar todo
        armarVentana(usuario, panelBotones);
    }

    // Métodos para asignar los eventos a los botones desde el controlador
    // (Asegúrate de que los nombres de estos métodos coincidan con la interfaz de la vista si la usas)
    
    public void setAccionGestionFlota(Runnable accion) {
        btnGestionFlota.addActionListener(evento -> accion.run());
    }

    public void setAccionGestionItinerarios(Runnable accion) {
        btnGestionItinerarios.addActionListener(evento -> accion.run());
    }
}

/* package vista;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelo.Usuario;
import util.Mensajes; */

/**
 * Pantalla principal del Administrador. Sus botones son los puntos de entrada a los módulos de las
 * otras historias; mientras no estén implementados muestran un aviso de "módulo en desarrollo".
 */
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

    //Punto de integración de HU-002 (Gestión de Flota de Unidades)
    public void setAccionGestionFlota(Runnable accion) {
        this.accionGestionFlota = accion;
    }

    //Punto de integración de HU-003 (Gestionar Itinerarios).
    public void setAccionGestionItinerarios(Runnable accion) {
        this.accionGestionItinerarios = accion;
    }

    private void avisarModuloEnDesarrollo(String historia) {
        JOptionPane.showMessageDialog(this, Mensajes.get("principal.moduloEnDesarrollo", historia),
                getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }
}
 */