package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Base de las pantallas con formulario arriba y tabla abajo (Registrar Conductor, Configurar Tarifa).
 * Se dibuja sobre una imagen de fondo de 1920 x 1080 y se posiciona con las medidas de la maqueta,
 * escalándolas al tamaño real de la ventana. Aquí están la tarjeta, la cruz de cerrar y la tabla;
 * cada pantalla coloca sus propios campos y botones en colocarFormulario().
 */
public abstract class PanelFormularioTabla extends JPanel {
    private static final long serialVersionUID = 1L;

    protected static final double REF_ANCHO = 1920;
    protected static final double REF_ALTO = 1080;

    // Medidas de la maqueta {x, y, ancho, alto}
    private static final double[] TARJETA = {38, 151, 1849, 901};
    private static final double CRUZ_X = 1774;
    private static final double CRUZ_Y = 17;
    private static final double CRUZ_ANCHO = 113;
    private static final double ENCABEZADO_X = 90;
    private static final double ENCABEZADO_Y = 627;
    private static final double ENCABEZADO_ANCHO = 1722;
    private static final double ENCABEZADO_ALTO = 92;
    private static final double CUERPO_ABAJO_Y = 1004;
    private static final double FILAS_X = 108;
    private static final double FILAS_Y = 724;
    private static final double FILAS_ALTO = 267;
    private static final double FILA_ALTO = 75;
    private static final double BARRA_ANCHO = 75;
    private static final double BARRA_SEPARACION = 20;
    private static final double MARGEN_TEXTO_IZQ = 28;

    protected static final Color AZUL = new Color(0x00, 0x49, 0xAB);
    private static final Color CUERPO_ARRIBA = new Color(216, 212, 211);
    private static final Color CUERPO_ABAJO = new Color(203, 199, 199);
    private static final Color GRIS_FILA = new Color(176, 173, 173);
    private static final Color GRIS_PISTA = new Color(172, 170, 170);

    protected final BotonImagen botonCerrar = new BotonImagen("/imagenes/cross1.png", "/imagenes/cross2.png", "Cerrar");
    protected final DefaultTableModel modeloTabla;
    protected final JTable tabla;

    private final JScrollPane scroll;
    private final FilaPildoraRenderer renderer = new FilaPildoraRenderer(AZUL, GRIS_FILA, true);
    private final String[] columnas;
    private final double[] anchosColumnas; // en la escala de la maqueta; suman el ancho de la tabla
    private final boolean conBarra;
    private final double fuenteEncabezado;
    private final double fuenteFilas;

    private final transient RecursosImagen.Escalado fondo;
    private final transient TarjetaNueveCortes tarjeta = new TarjetaNueveCortes("/imagenes/BlanckWindow.png");
    private final double proporcionCruz;

    protected double escala = 1;
    protected int desfaseX = 0;
    protected int desfaseY = 0;

    /**
     * @param columnas         títulos de la tabla
     * @param anchosColumnas   ancho de cada columna en la escala de la maqueta
     * @param conBarra         si la tabla muestra la barra de desplazamiento a la derecha
     * @param fuenteEncabezado tamaño de la letra de los títulos (escala de la maqueta)
     * @param fuenteFilas      tamaño de la letra de las filas (escala de la maqueta)
     */
    protected PanelFormularioTabla(String rutaFondo, String[] columnas, double[] anchosColumnas,
            boolean conBarra, double fuenteEncabezado, double fuenteFilas) {
        super(null);
        this.columnas = columnas.clone();
        this.anchosColumnas = anchosColumnas.clone();
        this.conBarra = conBarra;
        this.fuenteEncabezado = fuenteEncabezado;
        this.fuenteFilas = fuenteFilas;
        this.fondo = new RecursosImagen.Escalado(rutaFondo);
        BufferedImage cruz = RecursosImagen.cargar("/imagenes/cross1.png");
        this.proporcionCruz = (double) cruz.getHeight() / cruz.getWidth();

        setOpaque(true);
        setPreferredSize(new Dimension(1100, 619));

        modeloTabla = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        scroll = new JScrollPane(tabla,
                conBarra ? JScrollPane.VERTICAL_SCROLLBAR_ALWAYS : JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        tabla.setTableHeader(null);
        tabla.setOpaque(false);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(false);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setDefaultRenderer(Object.class, renderer);

        scroll.setOpaque(false);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        if (conBarra) {
            scroll.getVerticalScrollBar().setUI(new BarraDesplazamientoUI(GRIS_PISTA, AZUL, true));
            scroll.getVerticalScrollBar().setOpaque(false);
            scroll.getVerticalScrollBar().setUnitIncrement(20);
        }

        add(botonCerrar);
        add(scroll);
        botonCerrar.setFocusable(false);
    }

    public JTable getTabla() { return tabla; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public BotonImagen getBotonCerrar() { return botonCerrar; }

    // ------------------------------------------------------------------ para las pantallas hijas

    /** Coloca (setBounds) los campos y botones propios de la pantalla; ya están calculados escala y desfases. */
    protected abstract void colocarFormulario();

    /** Dibuja textos propios de la pantalla (etiquetas, ayudas); se llama después de la tarjeta y la tabla. */
    protected void dibujarFormulario(Graphics2D g2) { }

    protected int px(double valor) {
        return Math.max(1, (int) Math.round(valor * escala));
    }

    protected Rectangle rect(double x, double y, double ancho, double alto) {
        return new Rectangle(desfaseX + (int) Math.round(x * escala),
                desfaseY + (int) Math.round(y * escala), px(ancho), px(alto));
    }

    /** Botón de ancho fijo cuyo alto sale de la proporción de su imagen; y es el borde superior. */
    protected Rectangle rectBoton(double x, double y, double ancho, double proporcion) {
        return rect(x, y, ancho, ancho * proporcion);
    }

    protected static double proporcion(String ruta) {
        BufferedImage img = RecursosImagen.cargar(ruta);
        return (double) img.getHeight() / img.getWidth();
    }

    /**
     * Dibuja una o varias líneas de texto centradas verticalmente en centroY.
     * @param x       borde derecho si alinearDerecha, borde izquierdo si no
     * @param tamano  tamaño de la letra en la escala de la maqueta
     */
    protected void dibujarTexto(Graphics2D g2, String[] lineas, double x, double centroY, double tamano,
            boolean alinearDerecha, Color color) {
        g2.setFont(new Font("Arial", Font.PLAIN, px(tamano)));
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        double separacion = tamano * 1.05 * escala;
        for (int i = 0; i < lineas.length; i++) {
            double centro = desfaseY + centroY * escala + (i - (lineas.length - 1) / 2.0) * separacion;
            int base = (int) Math.round(centro + (fm.getAscent() - fm.getDescent()) / 2.0);
            int ancho = fm.stringWidth(lineas[i]);
            int xPantalla = desfaseX + (int) Math.round(x * escala);
            g2.drawString(lineas[i], alinearDerecha ? xPantalla - ancho : xPantalla, base);
        }
    }

    // ------------------------------------------------------------------ layout y dibujo

    private double anchoFilas() {
        return conBarra ? 1799 - FILAS_X : 1792 - FILAS_X;
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        escala = Math.min(w / REF_ANCHO, h / REF_ALTO);
        desfaseX = (int) Math.round((w - REF_ANCHO * escala) / 2);
        desfaseY = (int) Math.round((h - REF_ALTO * escala) / 2);

        botonCerrar.setBounds(rect(CRUZ_X, CRUZ_Y, CRUZ_ANCHO, CRUZ_ANCHO * proporcionCruz));

        tabla.setRowHeight(px(FILA_ALTO));
        tabla.setFont(new Font("Arial", Font.BOLD, px(fuenteFilas)));
        renderer.setMargenIzquierdo(px(MARGEN_TEXTO_IZQ));
        int anchoBarra = 0;
        if (conBarra) {
            anchoBarra = px(BARRA_ANCHO + BARRA_SEPARACION);
            scroll.getVerticalScrollBar().setPreferredSize(new Dimension(anchoBarra, 0));
            scroll.getVerticalScrollBar().setBorder(new EmptyBorder(0, px(BARRA_SEPARACION), 0, 0));
        }
        Rectangle area = rect(FILAS_X, FILAS_Y, anchoFilas(), FILAS_ALTO);
        scroll.setBounds(area);
        ajustarColumnas(area.width - anchoBarra);
        scroll.doLayout();

        colocarFormulario();
    }

    // Reparte el ancho disponible entre las columnas según las proporciones de la maqueta
    private void ajustarColumnas(int anchoTotal) {
        double suma = 0;
        for (double a : anchosColumnas) {
            suma += a;
        }
        int acumulado = 0;
        for (int i = 0; i < anchosColumnas.length; i++) {
            int ancho = (i == anchosColumnas.length - 1)
                    ? anchoTotal - acumulado
                    : (int) Math.round(anchosColumnas[i] / suma * anchoTotal);
            tabla.getColumnModel().getColumn(i).setPreferredWidth(ancho);
            tabla.getColumnModel().getColumn(i).setWidth(ancho);
            acumulado += ancho;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.drawImage(fondo.obtener(getWidth(), getHeight()), 0, 0, null);

        Rectangle t = rect(TARJETA[0], TARJETA[1], TARJETA[2], TARJETA[3]);
        tarjeta.dibujar(g2, t.x, t.y, t.width, t.height, px(90));

        dibujarTabla(g2);
        dibujarFormulario(g2);
        g2.dispose();
    }

    // Fondo gris de la tabla (con borde blanco) y barra azul de encabezado con sus títulos
    private void dibujarTabla(Graphics2D g2) {
        Rectangle cuerpo = rect(ENCABEZADO_X, ENCABEZADO_Y + ENCABEZADO_ALTO / 2, ENCABEZADO_ANCHO,
                CUERPO_ABAJO_Y - ENCABEZADO_Y - ENCABEZADO_ALTO / 2);
        int arco = px(80);
        int borde = px(6);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(cuerpo.x, cuerpo.y, cuerpo.width, cuerpo.height, arco, arco);
        g2.setPaint(new GradientPaint(0, cuerpo.y, CUERPO_ARRIBA, 0, cuerpo.y + cuerpo.height, CUERPO_ABAJO));
        g2.fillRoundRect(cuerpo.x + borde, cuerpo.y + borde, cuerpo.width - 2 * borde, cuerpo.height - 2 * borde,
                arco - borde, arco - borde);

        Rectangle enc = rect(ENCABEZADO_X, ENCABEZADO_Y, ENCABEZADO_ANCHO, ENCABEZADO_ALTO);
        g2.setColor(AZUL);
        g2.fillRoundRect(enc.x, enc.y, enc.width, enc.height, enc.height, enc.height);

        g2.setFont(new Font("Arial", Font.BOLD, px(fuenteEncabezado)));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        int baseY = enc.y + (enc.height - fm.getHeight()) / 2 + fm.getAscent();
        double inicio = 0;
        for (int i = 0; i < columnas.length; i++) {
            double ancho = anchosColumnas[i];
            if (i == 0) {
                // primer título alineado a la izquierda, igual que el texto de las filas
                int x = desfaseX + (int) Math.round((FILAS_X + MARGEN_TEXTO_IZQ) * escala);
                g2.drawString(columnas[i], x, baseY);
            } else {
                int centro = desfaseX + (int) Math.round((FILAS_X + inicio + ancho / 2) * escala);
                g2.drawString(columnas[i], centro - fm.stringWidth(columnas[i]) / 2, baseY);
            }
            inicio += ancho;
        }
    }
}
