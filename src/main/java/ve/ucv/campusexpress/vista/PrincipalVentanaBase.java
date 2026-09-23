package ve.ucv.campusexpress.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import ve.ucv.campusexpress.modelo.Usuario;
import ve.ucv.campusexpress.util.Mensajes;

/**
 * Esqueleto común de las pantallas principales: encabezado con el usuario, contenido central
 * propio de cada rol y botón para cerrar sesión.
 *
 * <p>Las subclases construyen su contenido central y llaman a
 * {@link #armarVentana(Usuario, JComponent)} al final de su constructor.</p>
 */
public abstract class PrincipalVentanaBase extends JFrame implements PrincipalVista {

    private static final long serialVersionUID = 1L;

    private final JButton botonCerrarSesion = new JButton(Mensajes.get("principal.cerrarSesion"));

    protected PrincipalVentanaBase(String tituloPantalla) {
        super(Mensajes.get("app.nombre") + " - " + tituloPantalla);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    protected final void armarVentana(Usuario usuario, JComponent contenidoCentral) {
        JLabel bienvenida = new JLabel(Mensajes.get("principal.bienvenida", usuario.getNombreCompleto()));
        bienvenida.setFont(bienvenida.getFont().deriveFont(Font.BOLD, 20f));
        JLabel rol = new JLabel(Mensajes.get("principal.rol", usuario.getRol().getEtiqueta()));
        rol.setForeground(Color.GRAY);

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        encabezado.add(bienvenida);
        encabezado.add(rol);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.add(botonCerrarSesion);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBorder(new EmptyBorder(20, 24, 16, 24));
        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(contenidoCentral, BorderLayout.CENTER);
        contenido.add(pie, BorderLayout.SOUTH);

        setContentPane(contenido);
        setSize(640, 400);
        setLocationRelativeTo(null);
    }

    @Override
    public void setAccionCerrarSesion(Runnable accion) {
        botonCerrarSesion.addActionListener(evento -> accion.run());
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
