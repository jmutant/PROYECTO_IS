package vista;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelo.Usuario;
import util.Mensajes;

/**
 * Pantalla principal del Administrador. Sus botones son los puntos de entrada a los módulos de las
 * otras historias; mientras no estén implementados muestran un aviso de "módulo en desarrollo".
 */
public class PrincipalAdministradorVentana extends PrincipalVentanaBase {

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

    /** Punto de integración de HU-002 (Gestión de Flota de Unidades). */
    public void setAccionGestionFlota(Runnable accion) {
        this.accionGestionFlota = accion;
    }

    /** Punto de integración de HU-003 (Gestionar Itinerarios). */
    public void setAccionGestionItinerarios(Runnable accion) {
        this.accionGestionItinerarios = accion;
    }

    private void avisarModuloEnDesarrollo(String historia) {
        JOptionPane.showMessageDialog(this, Mensajes.get("principal.moduloEnDesarrollo", historia),
                getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }
}
