package vista;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;

import modelo.Rol;


public class RegistroPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Tamaño de la maqueta
    private static final double REF_ANCHO = 1920;
    private static final double REF_ALTO = 1080;
    public static final int ANCHO_PREFERIDO = 960;
    public static final int ALTO_PREFERIDO = 540;

    // Zona de la maqueta que se usa: toda la pantalla, o solo la "X" y la tarjeta si el fondo es transparente
    private static final double[] AREA_COMPLETA = {0, 0, REF_ANCHO, REF_ALTO};
    private static final double[] AREA_TRANSPARENTE = {400, 18, 1148, 1042};

    // Coordenadas
    private static final double[] TARJETA = {400, 142, 1148, 918};
    private static final double[] BOTON_CERRAR = {908, 18, 114, 111};
    private static final double[] BOTON_REGISTRAR = {763, 899, 397, 112};
    private static final double ETIQUETA_X = 415;
    private static final double ETIQUETA_ANCHO = 310;
    private static final double CAMPO_X = 802;
    private static final double CAMPO_ANCHO = 618;
    private static final double CAMPO_ALTO = 92;
    private static final double[] FILA_Y = {210, 321, 432, 543, 654, 765};
    private static final String[] TEXTOS_ETIQUETA = {"Nombre", "Apellido", "Cédula", "Rol", "Usuario", "Contraseña"};

    private static final String IMG_FONDO = "/imagenes/CE_BackgroundSolidColorImage.png";
    private static final String IMG_TARJETA = "/imagenes/BlanckWindow.png";
    private static final String IMG_CAMPO = "/imagenes/appButton.png";

    private final JTextField txtNombre = new CampoTextoImagen();
    private final JTextField txtApellido = new CampoTextoImagen();
    private final JTextField txtCedula = new CampoTextoImagen();
    private final JTextField txtUsername = new CampoTextoImagen();
    private final JPasswordField txtPassword = new CampoClaveImagen();
    private final SelectorRol selectorRol;

    private final JLabel[] etiquetas = new JLabel[TEXTOS_ETIQUETA.length];
    private final BotonImagen botonRegistrar =
            new BotonImagen("/imagenes/Register1.png", "/imagenes/Register2.png", "Registrarse");
    private final BotonImagen botonCerrar =
            new BotonImagen("/imagenes/cross1.png", "/imagenes/cross2.png", "Cerrar");

    private final transient RecursosImagen.Escalado fondo = new RecursosImagen.Escalado(IMG_FONDO);
    private final transient RecursosImagen.Escalado tarjeta = new RecursosImagen.Escalado(IMG_TARJETA);

    private double escala = 0.5;
    private int desfaseX = 0;
    private int desfaseY = 0;
    private double[] area = AREA_COMPLETA;
    private boolean fondoTransparente = false;

    /** @param modoAdministrador true: el selector de rol solo muestra Administrador */
    public RegistroPanel(boolean modoAdministrador) {
        super(null);
        setOpaque(true);
        setPreferredSize(new Dimension(ANCHO_PREFERIDO, ALTO_PREFERIDO));

        selectorRol = modoAdministrador
                ? new SelectorRol(Rol.ADMINISTRADOR)
                : new SelectorRol(Rol.ESTUDIANTE, Rol.EMPLEADO, Rol.PROFESOR, Rol.PUBLICO_GENERAL);

        // Rango de la CI
        ((AbstractDocument) txtCedula.getDocument()).setDocumentFilter(new FiltroCedula());

        for (int i = 0; i < etiquetas.length; i++) {
            etiquetas[i] = new JLabel(TEXTOS_ETIQUETA[i], SwingConstants.RIGHT);
            etiquetas[i].setForeground(Color.BLACK);
            add(etiquetas[i]);
        }
        add(txtNombre);
        add(txtApellido);
        add(txtCedula);
        add(txtUsername);
        add(txtPassword);
        add(botonRegistrar);
        add(botonCerrar);
        add(selectorRol);
        setComponentZOrder(selectorRol, 0); // el desplegable se dibuja por encima de los campos de abajo
        botonCerrar.setFocusable(false);

        // Sin barra de título del sistema, la ventana se mueve arrastrando el fondo
        MouseAdapter mover = new MouseAdapter() {
            private Point inicio;

            @Override
            public void mousePressed(MouseEvent e) {
                inicio = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Window ventana = SwingUtilities.getWindowAncestor(RegistroPanel.this);
                if (inicio != null && ventana != null) {
                    Point pantalla = e.getLocationOnScreen();
                    ventana.setLocation(pantalla.x - inicio.x, pantalla.y - inicio.y);
                }
            }
        };
        addMouseListener(mover);
        addMouseMotionListener(mover);
    }

    
    // if true, no se dibuja el fondo degradado ni el borde, solo la tarjeta, los campos y la "X". Osea el fondo será Transparente
    public void setFondoTransparente(boolean transparente) {
        this.fondoTransparente = transparente;
        this.area = transparente ? AREA_TRANSPARENTE : AREA_COMPLETA;
        setOpaque(!transparente);
        setPreferredSize(transparente
                ? new Dimension(ANCHO_PREFERIDO * 574 / 960, ALTO_PREFERIDO * 521 / 540)
                : new Dimension(ANCHO_PREFERIDO, ALTO_PREFERIDO));
        revalidate();
        repaint();
    }

    // ------------------------------------------------------------------ acceso a los datos

    public String getNombre() { return txtNombre.getText(); }
    public String getApellido() { return txtApellido.getText(); }
    public String getCedula() { return txtCedula.getText(); }
    public String getUsername() { return txtUsername.getText(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public Rol getRolSeleccionado() { return selectorRol.getRolSeleccionado(); }

    public JTextField getCampoCedula() { return txtCedula; }
    public SelectorRol getSelectorRol() { return selectorRol; }

    public void limpiar() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
    }

    public void enfocarPrimerCampo() {
        txtNombre.requestFocusInWindow();
    }

    public void setAccionRegistrar(Runnable accion) {
        botonRegistrar.addActionListener(e -> accion.run());
    }

    public void setAccionCerrar(Runnable accion) {
        botonCerrar.addActionListener(e -> accion.run());
    }

    /** Botón por defecto de la ventana (Enter lo presiona). */
    public BotonImagen getBotonRegistrar() {
        return botonRegistrar;
    }

    // ------------------------------------------------------------------ layout y dibujo

    private int px(double valorMaqueta) {
        return Math.max(1, (int) Math.round(valorMaqueta * escala));
    }

    private Rectangle rect(double x, double y, double ancho, double alto) {
        return new Rectangle(desfaseX + (int) Math.round(x * escala),
                desfaseY + (int) Math.round(y * escala), px(ancho), px(alto));
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        escala = Math.min(w / area[2], h / area[3]);
        desfaseX = (int) Math.round((w - area[2] * escala) / 2 - area[0] * escala);
        desfaseY = (int) Math.round((h - area[3] * escala) / 2 - area[1] * escala);

        Font fuenteEtiqueta = new Font("Arial", Font.PLAIN, px(48));
        Font fuenteCampo = new Font("Arial", Font.PLAIN, px(40));
        EmptyBorder margenTexto = new EmptyBorder(0, px(45), 0, px(45));

        Component[] campos = {txtNombre, txtApellido, txtCedula, selectorRol, txtUsername, txtPassword};
        for (int i = 0; i < campos.length; i++) {
            etiquetas[i].setFont(fuenteEtiqueta);
            etiquetas[i].setBounds(rect(ETIQUETA_X, FILA_Y[i], ETIQUETA_ANCHO, CAMPO_ALTO));

            Rectangle r = rect(CAMPO_X, FILA_Y[i], CAMPO_ANCHO, CAMPO_ALTO);
            if (campos[i] instanceof SelectorRol) {
                r.height = selectorRol.isAbierto()
                        ? selectorRol.getAlturaAbierta(r.width)
                        : selectorRol.getAlturaCerrada(r.width);
            } else {
                JTextComponent campo = (JTextComponent) campos[i];
                campo.setFont(fuenteCampo);
                campo.setBorder(margenTexto);
            }
            campos[i].setBounds(r);
        }

        botonRegistrar.setBounds(rect(BOTON_REGISTRAR[0], BOTON_REGISTRAR[1], BOTON_REGISTRAR[2], BOTON_REGISTRAR[3]));
        botonCerrar.setBounds(rect(BOTON_CERRAR[0], BOTON_CERRAR[1], BOTON_CERRAR[2], BOTON_CERRAR[3]));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (fondoTransparente) {
            // Se borra todo antes de redibujar para que las sombras semitransparentes no se acumulen
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(AlphaComposite.SrcOver);
        } else {
            g2.drawImage(fondo.obtener(getWidth(), getHeight()), 0, 0, null);
        }
        Rectangle t = rect(TARJETA[0], TARJETA[1], TARJETA[2], TARJETA[3]);
        g2.drawImage(tarjeta.obtener(t.width, t.height), t.x, t.y, null);

        if (!fondoTransparente) {
            g2.setColor(new Color(0xBF, 0xAF, 0xA3)); // borde fino: la ventana no tiene marco del sistema
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        g2.dispose();
    }

    // ------------------------------------------------------------------ campos con imagen de fondo

    private static final class CampoTextoImagen extends JTextField {
        private static final long serialVersionUID = 1L;
        private final transient RecursosImagen.Escalado fondoCampo = new RecursosImagen.Escalado(IMG_CAMPO);

        CampoTextoImagen() {
            setOpaque(false);
            setForeground(Color.BLACK);
            setCaretColor(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.drawImage(fondoCampo.obtener(getWidth(), getHeight()), 0, 0, null);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    private static final class CampoClaveImagen extends JPasswordField {
        private static final long serialVersionUID = 1L;
        private final transient RecursosImagen.Escalado fondoCampo = new RecursosImagen.Escalado(IMG_CAMPO);

        CampoClaveImagen() {
            setOpaque(false);
            setForeground(Color.BLACK);
            setCaretColor(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.drawImage(fondoCampo.obtener(getWidth(), getHeight()), 0, 0, null);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
