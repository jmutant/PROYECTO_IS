package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
        Image fondo = new ImageIcon(getClass().getResource("/imagenes/userButton.png")).getImage();
        @Override
        protected void paintComponent(Graphics g) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            super.paintComponent(g);
        }
    };
    
    private final JPasswordField campoContrasena = new JPasswordField(20) {
        Image fondo = new ImageIcon(getClass().getResource("/imagenes/passwordButton.png")).getImage();
        @Override
        protected void paintComponent(Graphics g) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            super.paintComponent(g);
        }
    };

    //private final JTextField campoUsuario = new JTextField(20);
    //private final JPasswordField campoContrasena = new JPasswordField(20);
    //private final JButton botonIniciarSesion = new JButton(Mensajes.get("login.boton"));
    //private final JButton botonRegistrarse = new JButton("Registrarse");
    //Hover detectará el mouse y cambiará la imagen del boton
    private final JButton botonIniciarSesion = crearBotonConImagen("/imagenes/Login1.png", "/imagenes/Login2.png", 130, 40);;
    private final JButton botonRegistrarse = crearBotonConImagen("/imagenes/Register1.png", "/imagenes/Register2.png", 130, 40);
    private final JLabel etiquetaError = new JLabel(" ", SwingConstants.CENTER);

    public LoginVentana() {
        super("Sistema de Gestión de Transporte Universitario");
        construirInterfaz();

        campoUsuario.setOpaque(false); // DEBE estar en false para que se vea tu imagen
    
        campoUsuario.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 15)); 
        
        campoUsuario.setForeground(Color.BLACK); 
        // type y tamaño de la letra
        campoUsuario.setFont(new Font("Arial", Font.PLAIN, 14));

        campoContrasena.setOpaque(false);
        campoContrasena.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 15));
        campoContrasena.setForeground(Color.BLACK);
        campoContrasena.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 450));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel Izquierdo para la Imagen y Fondo
        JPanel panelImagen = new JPanel(new BorderLayout()) {
            private Image backgroundImage;

            {
                try {
                    java.net.URL bgURL = getClass().getResource("/imagenes/CE_BackgroundSolidColorImage.png");
                    if (bgURL != null) {
                        backgroundImage = new ImageIcon(bgURL).getImage();
                    }
                } catch (Exception e) {
                    System.err.println("No se pudo cargar la imagen de fondo: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(241, 233, 222)); // Fallback color
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panelImagen.setPreferredSize(new Dimension(500, 450));
        
        JPanel contentLogoPanel = new JPanel(new BorderLayout());
        contentLogoPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel();
        try {
            java.net.URL logoURL = getClass().getResource("/imagenes/CAMPUSLOGO.png");
            if (logoURL != null) {
                ImageIcon originalIcon = new ImageIcon(logoURL);
                Image img = originalIcon.getImage().getScaledInstance(450, -1, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            } else {
                logoLabel.setText("LOGO"); // Fallback
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(new EmptyBorder(50, 0, 0, 0));
        
        JLabel appTitleLabel = new JLabel("Sistema de Gestión de Transporte Universitario", SwingConstants.CENTER);
        appTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        appTitleLabel.setBorder(new EmptyBorder(0, 0, 100, 0));

        JLabel versionLabel = new JLabel("Ver 1.0");
        versionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        versionLabel.setBorder(new EmptyBorder(0, 10, 10, 0));

        contentLogoPanel.add(logoLabel, BorderLayout.CENTER);
        contentLogoPanel.add(appTitleLabel, BorderLayout.SOUTH);
        
        panelImagen.add(contentLogoPanel, BorderLayout.CENTER);
        panelImagen.add(versionLabel, BorderLayout.SOUTH);


        // Panel Derecho para Login
        JPanel panelLogin = new JPanel(new GridBagLayout());
        panelLogin.setBackground(new Color(245, 245, 245));
        panelLogin.setBorder(new EmptyBorder(24, 32, 24, 32));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JLabel etiquetaUsuario = new JLabel("Usuario");
        JLabel etiquetaContrasena = new JLabel("Contraseña");
        etiquetaError.setForeground(COLOR_ERROR);

        c.gridy = 0; c.insets = new Insets(20,0,2,0); panelLogin.add(etiquetaUsuario,c);
        c.gridy = 1; c.insets = new Insets(2,0,15,0); panelLogin.add(campoUsuario,c);
        c.gridy = 2; c.insets = new Insets(10,0,2,0); panelLogin.add(etiquetaContrasena,c);
        c.gridy = 3; c.insets = new Insets(2,0,20,0); panelLogin.add(campoContrasena,c);
        c.gridy = 4; c.insets = new Insets(2,0,10,0); panelLogin.add(etiquetaError,c);

        JPanel botones = new JPanel(new BorderLayout(8,0));
        botones.setOpaque(false);
        botonIniciarSesion.setBackground(Color.WHITE);
        botonRegistrarse.setBackground(Color.WHITE);
        botones.add(botonIniciarSesion, BorderLayout.CENTER);
        botones.add(botonRegistrarse, BorderLayout.EAST);
        c.gridy = 5; c.insets = new Insets(10,0,0,0); panelLogin.add(botones,c);

        panelPrincipal.add(panelImagen, BorderLayout.CENTER);
        panelPrincipal.add(panelLogin, BorderLayout.EAST);

        setContentPane(panelPrincipal);
        getRootPane().setDefaultButton(botonIniciarSesion);
        pack();
        setLocationRelativeTo(null);
    }

        private JButton crearBotonConImagen(String rutaImagenNormal, String rutaImagenHover, int ancho, int alto) {
        JButton boton = new JButton();
        
        // Redimensiona la imagen originaal
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(rutaImagenNormal));
        Image imgNormal = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        ImageIcon iconoNormal = new ImageIcon(imgNormal);
        
        // Cargar y redimensionar la imagen hover 
        ImageIcon iconoHoverOriginal = new ImageIcon(getClass().getResource(rutaImagenHover));
        Image imgHover = iconoHoverOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        ImageIcon iconoHover = new ImageIcon(imgHover);
        
        // Asignar los iconos redimensionados
        boton.setIcon(iconoNormal);
        boton.setRolloverIcon(iconoHover);
        boton.setPressedIcon(iconoHover);
        
        // Forzar el tamaño del boton
        boton.setPreferredSize(new Dimension(ancho, alto));
        
        // Quitar bordes y fondos
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        return boton;
        }

    @Override public String getNombreUsuario() { return campoUsuario.getText(); }
    @Override public char[] getContrasena() { return campoContrasena.getPassword(); }
    @Override public void mostrarError(String mensaje) { etiquetaError.setText(mensaje); }
    @Override public void limpiarContrasena() {
        campoContrasena.setText("");
        campoContrasena.requestFocusInWindow();
    }

    @Override public void setAccionIniciarSesion(Runnable accion) {
        botonIniciarSesion.addActionListener(evento -> accion.run());
    }

    @Override public void setAccionRegistrarse(Runnable accion) {
        botonRegistrarse.addActionListener(evento -> accion.run());
    }

    @Override public void mostrar() {
        setVisible(true);
        campoUsuario.requestFocusInWindow();
    }

    @Override public void cerrar() { dispose(); }
}
