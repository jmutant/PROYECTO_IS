package vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;


public class RecargaPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final double REF_ANCHO = 1920;
    private static final double REF_ALTO = 1080;

    // Coordenadas en la maqueta
    private static final double[] TEXTO_INSTRUCCION = {300, 128, 1320, 70};
    private static final double[] TARJETA = {435, 205, 1050, 840};
    private static final double[] BOTON_VOLVER = {265, 480, 114, 111};
    private static final double[] BOTON_ACEPTAR = {746, 852, 440, 108};
    private static final double ETIQUETA_X = 455;
    private static final double ETIQUETA_ANCHO = 330;
    private static final double CAMPO_X = 810;
    private static final double CAMPO_ANCHO = 620;
    private static final double CAMPO_MONTO_ANCHO = 500;
    private static final double CAMPO_ALTO = 92;
    private static final double[] FILA_Y = {248, 360, 472, 584, 696};
    private static final String[] TEXTOS_ETIQUETA = {"Monto", "Teléfono", "Cédula", "Banco", "Referencia"};

    private static final String IMG_FONDO = "/imagenes/RecargaPantalla.png";
    private static final String IMG_TARJETA = "/imagenes/BlanckWindow.png";
    private static final String IMG_CAMPO = "/imagenes/appButton.png";

    private final JTextField txtMonto = new CampoTextoFondo();
    private final JTextField txtTelefono = new CampoTextoFondo();
    private final JTextField txtCedula = new CampoTextoFondo();
    private final SelectorOpciones selectorBanco = new SelectorOpciones(
            "Banco de Venezuela", "Mercantil", "Bancamiga", "Bancaribe");
    private final JTextField txtReferencia = new CampoTextoFondo();

    private final JLabel lblInstruccion =
            new JLabel("Ingrese los datos del Pago Movil realizado", SwingConstants.CENTER);
    private final JLabel lblBs = new JLabel("Bs.");
    private final JLabel[] etiquetas = new JLabel[TEXTOS_ETIQUETA.length];

    private final BotonImagen botonVolver =
            new BotonImagen("/imagenes/comeback1.png", "/imagenes/comeback2.png", "Volver al monedero");
    private final BotonAceptar botonAceptar = new BotonAceptar("Aceptar");

    private final transient RecursosImagen.Escalado fondo = new RecursosImagen.Escalado(IMG_FONDO);
    private final transient RecursosImagen.Escalado tarjeta = new RecursosImagen.Escalado(IMG_TARJETA);

    private double escala = 0.5;
    private int desfaseX = 0;
    private int desfaseY = 0;

    public RecargaPanel() {
        super(null);
        setOpaque(true);
        setPreferredSize(new Dimension(1000, 650));

        // Monto: solo números positivos. Cédula: mismo filtro y rango que el registro de usuarios.
        ((AbstractDocument) txtMonto.getDocument()).setDocumentFilter(new FiltroMonto());
        ((AbstractDocument) txtCedula.getDocument()).setDocumentFilter(new FiltroCedula());

        lblInstruccion.setForeground(Color.BLACK);
        lblBs.setForeground(Color.BLACK);
        add(lblInstruccion);
        add(lblBs);

        for (int i = 0; i < etiquetas.length; i++) {
            etiquetas[i] = new JLabel(TEXTOS_ETIQUETA[i], SwingConstants.RIGHT);
            etiquetas[i].setForeground(Color.BLACK);
            add(etiquetas[i]);
        }
        add(txtMonto);
        add(txtTelefono);
        add(txtCedula);
        add(selectorBanco);
        add(txtReferencia);
        add(botonAceptar);
        add(botonVolver);
        botonVolver.setFocusable(false);
        setComponentZOrder(selectorBanco, 0); // la lista desplegada se dibuja por encima de lo de abajo
    }

    // ------------------------------------------------------------------ acceso a los datos

    public String getMonto() { return txtMonto.getText(); }
    public String getTelefono() { return txtTelefono.getText(); }
    public String getCedula() { return txtCedula.getText(); }
    public String getBanco() { return selectorBanco.getOpcionSeleccionada(); }
    public String getReferencia() { return txtReferencia.getText(); }

    public void limpiar() {
        txtMonto.setText("");
        txtTelefono.setText("");
        txtCedula.setText("");
        selectorBanco.reiniciar();
        txtReferencia.setText("");
    }

    public void enfocarPrimerCampo() {
        txtMonto.requestFocusInWindow();
    }

    public void setAccionAceptar(Runnable accion) {
        botonAceptar.addActionListener(e -> accion.run());
    }

    public void setAccionVolver(Runnable accion) {
        botonVolver.addActionListener(e -> accion.run());
    }

    public JButton getBotonAceptar() {
        return botonAceptar;
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
        escala = Math.min(w / REF_ANCHO, h / REF_ALTO);
        desfaseX = (int) Math.round((w - REF_ANCHO * escala) / 2);
        desfaseY = (int) Math.round((h - REF_ALTO * escala) / 2);

        lblInstruccion.setFont(new Font("Arial", Font.PLAIN, px(46)));
        lblInstruccion.setBounds(rect(TEXTO_INSTRUCCION[0], TEXTO_INSTRUCCION[1],
                TEXTO_INSTRUCCION[2], TEXTO_INSTRUCCION[3]));

        Font fuenteEtiqueta = new Font("Arial", Font.PLAIN, px(48));
        Font fuenteCampo = new Font("Arial", Font.PLAIN, px(40));
        EmptyBorder margenTexto = new EmptyBorder(0, px(45), 0, px(45));

        Component[] campos = {txtMonto, txtTelefono, txtCedula, selectorBanco, txtReferencia};
        for (int i = 0; i < campos.length; i++) {
            etiquetas[i].setFont(fuenteEtiqueta);
            etiquetas[i].setBounds(rect(ETIQUETA_X, FILA_Y[i], ETIQUETA_ANCHO, CAMPO_ALTO));

            double ancho = i == 0 ? CAMPO_MONTO_ANCHO : CAMPO_ANCHO;
            Rectangle r = rect(CAMPO_X, FILA_Y[i], ancho, CAMPO_ALTO);
            if (campos[i] instanceof SelectorOpciones) {
                r.height = selectorBanco.isAbierto()
                        ? selectorBanco.getAlturaAbierta(r.width)
                        : selectorBanco.getAlturaCerrada(r.width);
            } else {
                JTextComponent campo = (JTextComponent) campos[i];
                campo.setFont(fuenteCampo);
                campo.setBorder(margenTexto);
            }
            campos[i].setBounds(r);
        }

        lblBs.setFont(fuenteEtiqueta);
        lblBs.setBounds(rect(CAMPO_X + CAMPO_MONTO_ANCHO + 18, FILA_Y[0] + 14, 120, CAMPO_ALTO));

        botonAceptar.setBounds(rect(BOTON_ACEPTAR[0], BOTON_ACEPTAR[1], BOTON_ACEPTAR[2], BOTON_ACEPTAR[3]));
        botonVolver.setBounds(rect(BOTON_VOLVER[0], BOTON_VOLVER[1], BOTON_VOLVER[2], BOTON_VOLVER[3]));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(fondo.obtener(getWidth(), getHeight()), 0, 0, null);
        Rectangle t = rect(TARJETA[0], TARJETA[1], TARJETA[2], TARJETA[3]);
        g2.drawImage(tarjeta.obtener(t.width, t.height), t.x, t.y, null);
        g2.dispose();
    }

    // ------------------------------------------------------------------ componentes propios

    /** Campo de texto con el fondo gris redondeado de appButton.png. */
    private static final class CampoTextoFondo extends JTextField {
        private static final long serialVersionUID = 1L;
        private final transient RecursosImagen.Escalado fondoCampo = new RecursosImagen.Escalado(IMG_CAMPO);

        CampoTextoFondo() {
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

    //Botón 
    private static final class BotonAceptar extends JButton {
        private static final long serialVersionUID = 1L;
        private static final Color AZUL = new Color(0x00, 0x49, 0xAB);
        private static final Color AZUL_HOVER = new Color(0x00, 0x38, 0x85);
        private static final Color SOMBRA = new Color(0xDA, 0xDA, 0xDA);

        BotonAceptar(String texto) {
            super(texto);
            setRolloverEnabled(true);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            getAccessibleContext().setAccessibleName(texto);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int sombra = Math.max(3, getHeight() / 9);
            int ancho = getWidth() - sombra;
            int alto = getHeight() - sombra;
            g2.setColor(SOMBRA);
            g2.fillRoundRect(sombra, sombra, ancho, alto, alto, alto);

            boolean activo = getModel().isRollover() || getModel().isPressed();
            g2.setColor(activo ? AZUL_HOVER : AZUL);
            g2.fillRoundRect(0, 0, ancho, alto, alto, alto);

            g2.setFont(new Font("Arial", Font.BOLD, Math.max(10, (int) Math.round(alto * 0.46))));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), (ancho - fm.stringWidth(getText())) / 2,
                    (alto - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}