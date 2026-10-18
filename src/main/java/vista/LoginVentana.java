package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class LoginVentana extends JFrame implements LoginVista {

    private static final long serialVersionUID = 1L;
    private static final Color COLOR_ERROR = new Color(0xB0, 0x00, 0x20);

    private final JTextField campoUsuario = new JTextField(20) {
        private static final long serialVersionUID = 1L;

        Image fondo = new ImageIcon(
                getClass().getResource("/imagenes/userButton.png")
        ).getImage();

        @Override
        protected void paintComponent(Graphics g) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            super.paintComponent(g);
        }
    };

    private final JPasswordField campoContrasena = new JPasswordField(20) {
        private static final long serialVersionUID = 1L;

        Image fondo = new ImageIcon(
                getClass().getResource("/imagenes/passwordButton.png")
        ).getImage();

        @Override
        protected void paintComponent(Graphics g) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            super.paintComponent(g);
        }
    };

    private final JButton botonIniciarSesion = crearBotonConImagen(
            "/imagenes/Login1.png",
            "/imagenes/Login2.png",
            130,
            40
    );

    private final JButton botonRegistrarse = crearBotonConImagen(
            "/imagenes/Register1.png",
            "/imagenes/Register2.png",
            130,
            40
    );

    private final JLabel etiquetaError =
            new JLabel(" ", SwingConstants.CENTER);

    private Timer temporizadorLogo;
    private IconoFlotante iconoLogoAnimado;
    private JLabel logoLabel;

    public LoginVentana() {
        super("Sistema de Gestión de Transporte Universitario");

        construirInterfaz();

        campoUsuario.setOpaque(false);
        campoUsuario.setBorder(
                BorderFactory.createEmptyBorder(15, 50, 15, 15)
        );
        campoUsuario.setForeground(Color.BLACK);
        campoUsuario.setFont(new Font("Arial", Font.PLAIN, 14));

        campoContrasena.setOpaque(false);
        campoContrasena.setBorder(
                BorderFactory.createEmptyBorder(15, 50, 15, 15)
        );
        campoContrasena.setForeground(Color.BLACK);
        campoContrasena.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void construirInterfaz() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 540));

        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;
            private transient Image backgroundImage;

            {
                java.net.URL bgURL = getClass().getResource(
                        "/imagenes/BackgroundPantallaCAMPUS.png"
                );

                if (bgURL != null) {
                    backgroundImage = new ImageIcon(bgURL).getImage();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (backgroundImage != null) {
                    g.drawImage(
                            backgroundImage,
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            this
                    );
                } else {
                    g.setColor(new Color(241, 233, 222));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // Panel izquierdo para el logo.
        JPanel panelImagen = new JPanel(new BorderLayout());
        panelImagen.setOpaque(false);
        panelImagen.setPreferredSize(new Dimension(540, 540));

        JPanel contentLogoPanel = new JPanel(new BorderLayout());
        contentLogoPanel.setOpaque(false);

        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(new EmptyBorder(50, 0, 0, 0));

        cargarLogo();

        JLabel appTitleLabel = new JLabel(
                "Sistema de Gestión de Transporte Universitario",
                SwingConstants.CENTER
        );

        appTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        appTitleLabel.setBorder(new EmptyBorder(0, 0, 100, 0));

        JLabel versionLabel = new JLabel("Ver 1.0");
        versionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        versionLabel.setBorder(new EmptyBorder(0, 10, 10, 0));

        contentLogoPanel.add(logoLabel, BorderLayout.CENTER);
        contentLogoPanel.add(appTitleLabel, BorderLayout.SOUTH);

        panelImagen.add(contentLogoPanel, BorderLayout.CENTER);
        panelImagen.add(versionLabel, BorderLayout.SOUTH);

        // Panel derecho para el inicio de sesión.
        JPanel panelLogin = new JPanel(new GridBagLayout());
        panelLogin.setOpaque(false);
        panelLogin.setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JLabel etiquetaUsuario = new JLabel("Usuario");
        JLabel etiquetaContrasena = new JLabel("Contraseña");

        etiquetaError.setForeground(COLOR_ERROR);

        c.gridy = 0;
        c.insets = new Insets(20, 0, 2, 0);
        panelLogin.add(etiquetaUsuario, c);

        c.gridy = 1;
        c.insets = new Insets(2, 0, 15, 0);
        panelLogin.add(campoUsuario, c);

        c.gridy = 2;
        c.insets = new Insets(10, 0, 2, 0);
        panelLogin.add(etiquetaContrasena, c);

        c.gridy = 3;
        c.insets = new Insets(2, 0, 20, 0);
        panelLogin.add(campoContrasena, c);

        c.gridy = 4;
        c.insets = new Insets(2, 0, 10, 0);
        panelLogin.add(etiquetaError, c);

        JPanel botones = new JPanel(new BorderLayout(8, 0));
        botones.setOpaque(false);

        botonIniciarSesion.setBackground(Color.WHITE);
        botonRegistrarse.setBackground(Color.WHITE);

        botones.add(botonIniciarSesion, BorderLayout.CENTER);
        botones.add(botonRegistrarse, BorderLayout.EAST);

        c.gridy = 5;
        c.insets = new Insets(10, 0, 0, 0);
        panelLogin.add(botones, c);

        panelPrincipal.add(panelImagen, BorderLayout.CENTER);
        //
        panelPrincipal.add(panelLogin, BorderLayout.EAST);

        setContentPane(panelPrincipal);
        getRootPane().setDefaultButton(botonIniciarSesion);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Carga CAMPUSLOGO.png y conserva su proporción original.
     */
    private void cargarLogo() {

        java.net.URL logoURL = getClass().getResource(
                "/imagenes/CAMPUSLOGO.png"
        );

        if (logoURL == null) {
            System.err.println(
                    "No se encontró CAMPUSLOGO.png en /imagenes/"
            );
            logoLabel.setText("No se pudo cargar el logo");
            return;
        }

        ImageIcon originalIcon = new ImageIcon(logoURL);

        int anchoOriginal = originalIcon.getIconWidth();
        int altoOriginal = originalIcon.getIconHeight();

        if (anchoOriginal <= 0 || altoOriginal <= 0) {
            System.err.println("Las dimensiones del logo no son válidas.");
            logoLabel.setText("No se pudo cargar el logo");
            return;
        }

        int anchoLogo = 480;

        int altoLogo = (int) Math.round(
                (double) altoOriginal * anchoLogo / anchoOriginal
        );

        Image imagenEscalada = originalIcon.getImage().getScaledInstance(
                anchoLogo,
                altoLogo,
                Image.SCALE_SMOOTH
        );

        // ImageIcon garantiza que se conozcan las dimensiones de la imagen.
        ImageIcon imagenFinal = new ImageIcon(imagenEscalada);

        iconoLogoAnimado = new IconoFlotante(
                imagenFinal.getImage(),
                imagenFinal.getIconWidth(),
                imagenFinal.getIconHeight()
        );

        logoLabel.setIcon(iconoLogoAnimado);
        logoLabel.setText(null);
    }

    /**
     * Icono animado que mantiene unas dimensiones constantes.
     */
    private static final class IconoFlotante implements Icon {

        private final Image imagen;
        private final int ancho;
        private final int alto;

        private int desplazamientoY = 0;

        private IconoFlotante(Image imagen, int ancho, int alto) {
            this.imagen = imagen;
            this.ancho = ancho;
            this.alto = alto;
        }

        @Override
        public int getIconWidth() {
            return ancho;
        }

        @Override
        public int getIconHeight() {
            // Espacio reservado para que el logo se mueva sin alterar
            // el tamaño que ocupa en el diseño.
            return alto + 14;
        }

        @Override
        public void paintIcon(
                Component componente,
                Graphics g,
                int x,
                int y
        ) {
            g.drawImage(
                    imagen,
                    x,
                    y + 7 + desplazamientoY,
                    componente
            );
        }

        private void setDesplazamientoY(int desplazamientoY) {
            this.desplazamientoY = desplazamientoY;
        }
    }

    /**
     * Inicia la animación de flotación con Swing Timer.
     */
    private void iniciarAnimacionLogo() {

        if (iconoLogoAnimado == null || temporizadorLogo != null) {
            return;
        }

        temporizadorLogo = new Timer(40, evento -> {

            long tiempo = System.nanoTime() / 1_000_000L;

            double fase = (tiempo % 2400) / 2400.0
                    * Math.PI * 2.0;

            int desplazamiento = (int) Math.round(
                    Math.sin(fase) * 6
            );

            iconoLogoAnimado.setDesplazamientoY(desplazamiento);
            logoLabel.repaint();
        });

        temporizadorLogo.start();
    }

    private JButton crearBotonConImagen(
            String rutaImagenNormal,
            String rutaImagenHover,
            int ancho,
            int alto
    ) {

        JButton boton = new JButton();

        ImageIcon iconoOriginal = new ImageIcon(
                getClass().getResource(rutaImagenNormal)
        );

        Image imgNormal = iconoOriginal.getImage().getScaledInstance(
                ancho,
                alto,
                Image.SCALE_SMOOTH
        );

        ImageIcon iconoNormal = new ImageIcon(imgNormal);

        ImageIcon iconoHoverOriginal = new ImageIcon(
                getClass().getResource(rutaImagenHover)
        );

        Image imgHover = iconoHoverOriginal.getImage().getScaledInstance(
                ancho,
                alto,
                Image.SCALE_SMOOTH
        );

        ImageIcon iconoHover = new ImageIcon(imgHover);

        boton.setIcon(iconoNormal);
        boton.setRolloverIcon(iconoHover);
        boton.setPressedIcon(iconoHover);

        boton.setPreferredSize(new Dimension(ancho, alto));

        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);

        boton.setCursor(
                new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
        );

        return boton;
    }

    @Override
    public String getNombreUsuario() {
        return campoUsuario.getText();
    }

    @Override
    public char[] getContrasena() {
        return campoContrasena.getPassword();
    }

    @Override
    public void mostrarError(String mensaje) {
        etiquetaError.setText(mensaje);
    }

    @Override
    public void limpiarContrasena() {
        campoContrasena.setText("");
        campoContrasena.requestFocusInWindow();
    }

    @Override
    public void setAccionIniciarSesion(Runnable accion) {
        botonIniciarSesion.addActionListener(
                evento -> accion.run()
        );
    }

    @Override
    public void setAccionRegistrarse(Runnable accion) {
        botonRegistrarse.addActionListener(
                evento -> accion.run()
        );
    }

    @Override
    public void mostrar() {
        setVisible(true);
        iniciarAnimacionLogo();
        campoUsuario.requestFocusInWindow();
    }

    @Override
    public void cerrar() {
        if (temporizadorLogo != null) {
            temporizadorLogo.stop();
            temporizadorLogo = null;
        }

        dispose();
    }
}