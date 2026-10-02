package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import modelo.Usuario;
import util.Mensajes;

// Pantallas Principales
// header de usuarios y mas elementos
public abstract class PrincipalVentanaBase extends JFrame implements PrincipalVista {

    private static final long serialVersionUID = 1L;

    private final JButton botonCerrarSesion = new JButton(Mensajes.get("principal.cerrarSesion"));

    protected PrincipalVentanaBase(String tituloPantalla) {
        super(Mensajes.get("app.nombre") + " - " + tituloPantalla);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    protected final void armarVentana(Usuario usuario, JComponent contenidoCentral) {
        
        JLabel bienvenida = new JLabel(Mensajes.get("principal.bienvenida", usuario.getNombreCompleto()));
        bienvenida.setFont(bienvenida.getFont().deriveFont(Font.BOLD, 28f)); 
        bienvenida.setForeground(Color.BLACK);
        
        
        JLabel rol = new JLabel("Rol : [" + usuario.getRol().getEtiqueta() + "]");
        rol.setFont(rol.getFont().deriveFont(Font.BOLD, 18f));
        rol.setForeground(new Color(160, 160, 160)); // Gris claro
        rol.setBorder(new EmptyBorder(0, 0, 0, 0));

        
        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        encabezado.setOpaque(false);
        encabezado.add(bienvenida);

        // BORDERLAYOUT, pie de pagina pero con elementos separados por si se mueve esto?
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(rol, BorderLayout.WEST); // anclado a la eqn inferior izq
        pie.add(botonCerrarSesion, BorderLayout.EAST); // <-- El botón se queda en la esquina inferior derecha

        // 5. Panel Contenedor Principal (con la imagen de fondo)
        JPanel contenido = new JPanel(new BorderLayout(0, 16)) {
            Image fondoMenu = new ImageIcon(getClass().getResource("/imagenes/CE_BackgroundMenu.png")).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondoMenu != null) {
                    g.drawImage(fondoMenu, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(30, 40, 20, 40)); 
        
        //
        contenido.add(encabezado, BorderLayout.NORTH);
        
        contenidoCentral.setOpaque(false); 
        contenido.add(contenidoCentral, BorderLayout.CENTER);
        
        contenido.add(pie, BorderLayout.SOUTH); // Añadimos el pie que ahora tiene ambos elementos

        setContentPane(contenido);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    // MÉTODO PROTEGIDO: Las clases "hijas" como PrincipalAdministradorVentana podrán usarlo
    protected JButton crearBotonConImagen(String rutaImagenNormal, String rutaImagenHover, int ancho, int alto) {
        JButton boton = new JButton();
        
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(rutaImagenNormal));
        Image imgNormal = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        
        ImageIcon iconoHoverOriginal = new ImageIcon(getClass().getResource(rutaImagenHover));
        Image imgHover = iconoHoverOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        
        boton.setIcon(new ImageIcon(imgNormal));
        boton.setRolloverIcon(new ImageIcon(imgHover));
        boton.setPressedIcon(new ImageIcon(imgHover));
        
        boton.setPreferredSize(new Dimension(ancho, alto));
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        return boton;
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

// :D