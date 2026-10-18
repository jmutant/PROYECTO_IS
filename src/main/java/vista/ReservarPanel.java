package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla "Rutas disponibles" (reserva). Se dibuja sobre una imagen de fondo de 1920 x 1080 y se posiciona
 * con las medidas de la maqueta, escalándolas al tamaño real de la ventana (igual que PanelFormularioTabla).
 */
public class ReservarPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final double REF_ANCHO = 1920;
    private static final double REF_ALTO = 1080;

    // Medidas de la maqueta {x, y, ancho, alto}
    private static final double[] TARJETA = {34, 181, 1854, 849};
    private static final double CRUZ_X = 1774;
    private static final double CRUZ_Y = 17;
    private static final double CRUZ_ANCHO = 113;
    private static final double[] ETIQUETA_RUTA = {34, 181, 731, 92};
    private static final double[] COMBO = {47, 291, 1766, 79};
    private static final double[] ETIQUETA_DESGLOSE = {34, 386, 731, 92};
    private static final double[] PANEL_DESGLOSE = {59, 495, 1754, 278};
    private static final double[] PANEL_TOTAL = {1204, 495, 609, 278};
    private static final double[] CAMPO_TARIFA = {560, 513, 513, 110};
    private static final double[] CAMPO_SUBSIDIO = {560, 641, 513, 110};
    private static final double ETIQUETAS_DERECHA = 531;
    private static final double BOTON_X = 1360;
    private static final double BOTON_Y = 827;
    private static final double BOTON_ANCHO = 465;

    private static final Color AZUL = new Color(0x00, 0x49, 0xAB);
    private static final Color GRIS_DESGLOSE = new Color(230, 229, 229);
    private static final Color GRIS_TOTAL = new Color(212, 211, 211);
    private static final Color VERDE = new Color(114, 208, 141);

    private final BotonImagen botonRegresar = new BotonImagen("/imagenes/cross1.png", "/imagenes/cross2.png", "Regresar");
    private final BotonImagen botonReservar = new BotonImagen("/imagenes/ReservarBoton1.png", "/imagenes/ReservarBoton2.png", "Reservar");
    private final JComboBox<Object> comboRutas = new JComboBox<>();
    private final JTextField campoTarifa = new CampoFondoImagen();
    private final JTextField campoSubsidio = new CampoFondoImagen();

    private final transient RecursosImagen.Escalado fondo = new RecursosImagen.Escalado("/imagenes/RutasPantallaa.png");
    // Título animado (flotación). El lienzo del PNG mide 1673 x 151 (sistema 1920 x 1080): x, y, ancho, alto
    private final transient TituloFlotante titulo =
            new TituloFlotante("/imagenes/RUTASDISPONIBLESTEXTO.png", 15, 8, 1673, 151);

    private final transient TarjetaNueveCortes tarjeta = new TarjetaNueveCortes("/imagenes/BlanckWindow.png");
    private final double proporcionCruz = proporcion("/imagenes/cross1.png");
    private final double proporcionReservar = proporcion("/imagenes/ReservarBoton1.png");

    private String textoTotal = "0.00";
    private double escala = 1;
    private int desfaseX = 0;
    private int desfaseY = 0;

    public ReservarPanel() {
        super(null);
        setOpaque(true);
        setPreferredSize(new Dimension(1100, 619));
        titulo.conectar(this);

        EstiloCombo.aplicar(comboRutas, true, false, null); // misma lista desplegable del resto del proyecto

        for (JTextField campo : new JTextField[]{campoTarifa, campoSubsidio}) {
            campo.setEditable(false);
            campo.setFocusable(false);
            campo.setHorizontalAlignment(SwingConstants.CENTER);
        }
        botonRegresar.setFocusable(false);
        botonReservar.setFocusable(false);

        add(botonRegresar);
        add(comboRutas);
        add(campoTarifa);
        add(campoSubsidio);
        add(botonReservar);
    }

    public JComboBox<Object> getComboRutas() { return comboRutas; }
    public BotonImagen getBotonRegresar() { return botonRegresar; }
    public BotonImagen getBotonReservar() { return botonReservar; }

    public void setDesglose(String tarifaBase, String subsidio, String total) {
        campoTarifa.setText(tarifaBase);
        campoSubsidio.setText(subsidio);
        textoTotal = total;
        repaint();
    }

    private static double proporcion(String ruta) {
        BufferedImage img = RecursosImagen.cargar(ruta);
        return (double) img.getHeight() / img.getWidth();
    }

    private int px(double valor) {
        return Math.max(1, (int) Math.round(valor * escala));
    }

    private Rectangle rect(double[] m) {
        return rect(m[0], m[1], m[2], m[3]);
    }

    private Rectangle rect(double x, double y, double ancho, double alto) {
        return new Rectangle(desfaseX + (int) Math.round(x * escala),
                desfaseY + (int) Math.round(y * escala), px(ancho), px(alto));
    }

    @Override
    public void doLayout() {
        escala = Math.min(getWidth() / REF_ANCHO, getHeight() / REF_ALTO);
        desfaseX = (int) Math.round((getWidth() - REF_ANCHO * escala) / 2);
        desfaseY = (int) Math.round((getHeight() - REF_ALTO * escala) / 2);

        botonRegresar.setBounds(rect(CRUZ_X, CRUZ_Y, CRUZ_ANCHO, CRUZ_ANCHO * proporcionCruz));
        botonReservar.setBounds(rect(BOTON_X, BOTON_Y, BOTON_ANCHO, BOTON_ANCHO * proporcionReservar));

        comboRutas.setFont(new Font("Arial", Font.PLAIN, px(34)));
        comboRutas.setBounds(rect(COMBO));

        Font fuenteCampo = new Font("Arial", Font.PLAIN, px(40));
        for (JTextField campo : new JTextField[]{campoTarifa, campoSubsidio}) {
            campo.setFont(fuenteCampo);
            campo.setBorder(new EmptyBorder(0, px(20), 0, px(20)));
        }
        campoTarifa.setBounds(rect(CAMPO_TARIFA));
        campoSubsidio.setBounds(rect(CAMPO_SUBSIDIO));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.drawImage(fondo.obtener(getWidth(), getHeight()), 0, 0, null);
        titulo.dibujar(g2, getWidth(), getHeight());

        Rectangle t = rect(TARJETA);
        tarjeta.dibujar(g2, t.x, t.y, t.width, t.height, px(90));

        // Panel gris del desglose (izquierda) y recuadro del total (derecha)
        Rectangle desglose = rect(PANEL_DESGLOSE);
        g2.setColor(GRIS_DESGLOSE);
        g2.fillRoundRect(desglose.x, desglose.y, desglose.width, desglose.height, px(100), px(100));
        Rectangle total = rect(PANEL_TOTAL);
        g2.setColor(GRIS_TOTAL);
        g2.fillRoundRect(total.x, total.y, total.width, total.height, px(100), px(100));

        // Etiquetas azules
        dibujarPildora(g2, rect(ETIQUETA_RUTA), "Seleccione la Ruta del itinerario");
        dibujarPildora(g2, rect(ETIQUETA_DESGLOSE), "Desglose de la tarifa según su rol");

        // Textos del desglose
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.PLAIN, px(46)));
        dibujarTextoDerecha(g2, "Tarifa Base", ETIQUETAS_DERECHA, CAMPO_TARIFA[1] + CAMPO_TARIFA[3] / 2);
        g2.setFont(new Font("Arial", Font.PLAIN, px(40)));
        dibujarTextoDerecha(g2, "Subsidio/Descuento", ETIQUETAS_DERECHA, CAMPO_SUBSIDIO[1] + CAMPO_SUBSIDIO[3] / 2 - 25);
        dibujarTextoDerecha(g2, "Aplicado", ETIQUETAS_DERECHA, CAMPO_SUBSIDIO[1] + CAMPO_SUBSIDIO[3] / 2 + 25);

        // Total a pagar
        g2.setFont(new Font("Arial", Font.PLAIN, px(44)));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("Total a Pagar", total.x + px(45), total.y + px(60) + fm.getAscent() / 2);

        g2.setFont(new Font("Arial", Font.PLAIN, px(40)));
        FontMetrics fmBs = g2.getFontMetrics();
        int derecha = total.x + total.width - px(45);
        int baseY = total.y + total.height - px(40);
        g2.setColor(VERDE);
        g2.drawString("Bs", derecha - fmBs.stringWidth("Bs"), baseY);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.PLAIN, px(78)));
        FontMetrics fmTotal = g2.getFontMetrics();
        g2.drawString(textoTotal, derecha - fmBs.stringWidth("Bs") - px(10) - fmTotal.stringWidth(textoTotal), baseY);

        g2.dispose();
    }

    private void dibujarPildora(Graphics2D g2, Rectangle r, String texto) {
        g2.setColor(AZUL);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, r.height, r.height);
        g2.setColor(Color.WHITE);
        // El texto se achica si no cabe dentro de la etiqueta (según la fuente disponible en el equipo)
        int tamano = px(38);
        FontMetrics fm = g2.getFontMetrics(new Font("Arial", Font.BOLD, tamano));
        while (tamano > 8 && fm.stringWidth(texto) > r.width - r.height) {
            tamano--;
            fm = g2.getFontMetrics(new Font("Arial", Font.BOLD, tamano));
        }
        g2.setFont(new Font("Arial", Font.BOLD, tamano));
        fm = g2.getFontMetrics();
        g2.drawString(texto, r.x + (r.width - fm.stringWidth(texto)) / 2,
                r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent());
    }

    // Texto alineado a la derecha en x (escala de la maqueta), centrado verticalmente en centroY
    private void dibujarTextoDerecha(Graphics2D g2, String texto, double x, double centroY) {
        FontMetrics fm = g2.getFontMetrics();
        int xPantalla = desfaseX + (int) Math.round(x * escala);
        int yPantalla = desfaseY + (int) Math.round(centroY * escala);
        g2.drawString(texto, xPantalla - fm.stringWidth(texto), yPantalla + (fm.getAscent() - fm.getDescent()) / 2);
    }
}
