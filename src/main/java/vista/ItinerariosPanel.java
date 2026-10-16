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
import java.time.DayOfWeek;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import modelo.Conductor;
import modelo.TipoRuta;
import modelo.Unidad;

/**
 * Pantalla "Gestionar Itinerarios". Se dibuja sobre ItinerariosPantalla.png y se posiciona con las medidas
 * de la maqueta (1222 x 684), escalándolas al tamaño real de la ventana.
 * Aquí vive solo el diseño; la lógica está en GestionItinerariosVentana.
 */
public class ItinerariosPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final double REF_ANCHO = 1222;
    private static final double REF_ALTO = 684;

    // Medidas de la maqueta {x, y, ancho, alto}
    private static final double[] TARJETA = {25, 94, 1173, 576};
    private static final double CRUZ_X = 1113;
    private static final double CRUZ_Y = 10;
    private static final double CRUZ_ANCHO = 70;
    private static final double[] ETIQUETA_ORIGEN = {67, 104, 220, 28};
    private static final double[] CAMPO_ORIGEN = {67, 134, 299, 44};
    private static final double[] ETIQUETA_DESTINO = {471, 104, 220, 28};
    private static final double[] CAMPO_DESTINO = {458, 134, 298, 44};
    private static final double[] ETIQUETA_DIA = {44, 189, 140, 42};
    private static final String FONDO_BARRA = "/imagenes/BarButton.png";
    private static final double[] COMBO_DIA = {200, 189, 218, 42};
    private static final double[] ETIQUETA_HORA = {430, 189, 85, 42};
    private static final double[] COMBO_HORA = {527, 189, 95, 42};
    private static final double[] COMBO_MINUTO = {621, 189, 95, 42};
    private static final double[] DOS_PUNTOS = {604, 189, 34, 42};
    private static final double[] ETIQUETA_TIPO = {48, 239, 185, 38};
    private static final double[] COMBO_TIPO = {237, 239, 595, 38};
    private static final double[] ETIQUETA_UNIDAD = {50, 284, 185, 38};
    private static final double[] COMBO_UNIDAD = {237, 284, 595, 38};
    private static final double[] ETIQUETA_CONDUCTOR = {50, 330, 185, 38};
    private static final double[] COMBO_CONDUCTOR = {237, 330, 595, 38};
    // Botones: x, ancho y centro vertical (el alto sale de la proporción de cada imagen)
    private static final double BOTONES_ANCHO = 296;
    private static final double[] BOTON_REGISTRAR = {857, 138};
    private static final double[] BOTON_MODIFICAR = {857, 231};
    private static final double[] BOTON_ELIMINAR = {855, 323};

    private static final double TABLA_X = 38;
    private static final double TABLA_Y = 377;
    private static final double TABLA_ANCHO = 1138;
    private static final double TABLA_ALTO = 264;
    private static final double ENCABEZADO_ALTO = 47;
    private static final double SCROLL_X = 57;
    private static final double SCROLL_Y = 430;
    private static final double SCROLL_ANCHO = 1111;
    private static final double SCROLL_ALTO = 200;
    private static final double FILA_ALTO = 49;
    private static final double BARRA_ANCHO = 48;
    private static final double BARRA_SEPARACION = 7;
    private static final String[] COLUMNAS = {"Origen", "Destino", "Día", "Hora", "Tipo", "Unidad", "Conductor"};
    private static final double[] ANCHO_COLUMNAS = {130, 170, 130, 110, 140, 165, 211}; // suman 1056

    private static final Color AZUL = new Color(0x00, 0x49, 0xAB);
    private static final Color CUERPO_ARRIBA = new Color(216, 212, 211);
    private static final Color CUERPO_ABAJO = new Color(203, 199, 199);
    private static final Color GRIS_FILA = new Color(176, 173, 173);
    private static final Color GRIS_PISTA = new Color(172, 170, 170);

    private static final String[] DIAS_ES =
            {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    /** Nombre del día en español (DayOfWeek: 1 = lunes ... 7 = domingo). */
    public static String nombreDia(DayOfWeek dia) {
        return DIAS_ES[dia.getValue() - 1];
    }

    // Componentes (la ventana los usa para leer y cargar datos)
    private final JTextField campoOrigen = new CampoFondoImagen();
    private final JTextField campoDestino = new CampoFondoImagen();
    private final JComboBox<DayOfWeek> comboDiaSemana = new JComboBox<>(DayOfWeek.values());
    private final JComboBox<String> cbHora = new JComboBox<>(generarHoras());
    private final JComboBox<String> cbMinuto = new JComboBox<>(new String[]{"00", "15", "30", "45"});
    private final JComboBox<TipoRuta> comboTipoRuta = new JComboBox<>(TipoRuta.values());
    private final JComboBox<Unidad> comboUnidades = new JComboBox<>();
    private final JComboBox<Conductor> comboConductores = new JComboBox<>();

    private final BotonImagen botonRegistrar = new BotonImagen("/imagenes/RegisItinerario1.png",
            "/imagenes/RegisItinerario2.png", "Registrar Itinerario");
    private final BotonImagen botonModificar = new BotonImagen("/imagenes/ModificarButton1.png",
            "/imagenes/ModificarButton2.png", "Modificar");
    private final BotonImagen botonEliminar = new BotonImagen("/imagenes/Erase1.png",
            "/imagenes/Erase2.png", "Eliminar");
    private final BotonImagen botonRegresar = new BotonImagen("/imagenes/cross1.png",
            "/imagenes/cross2.png", "Cerrar");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JScrollPane scroll = new JScrollPane(tabla,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private final JLabel lblOrigen = new JLabel("Origen");
    private final JLabel lblDestino = new JLabel("Destino");
    private final JLabel lblDia = new JLabel("Día", SwingConstants.RIGHT);
    private final JLabel lblHora = new JLabel("Hora", SwingConstants.RIGHT);
    private final JLabel lblDosPuntos = new JLabel(":", SwingConstants.CENTER);
    private final JLabel lblTipo = new JLabel("Tipo de Ruta");
    private final JLabel lblUnidad = new JLabel("Unidad");
    private final JLabel lblConductor = new JLabel("Conductor");

    private final transient RecursosImagen.Escalado fondo =
            new RecursosImagen.Escalado("/imagenes/ItinerariosPantalla.png");
    private final transient TarjetaNueveCortes tarjeta = new TarjetaNueveCortes("/imagenes/BlanckWindow.png");

    // Proporción (alto / ancho) de cada imagen, para no deformar los botones
    private final double proporcionRegistrar = proporcion("/imagenes/RegisItinerario1.png");
    private final double proporcionModificar = proporcion("/imagenes/ModificarButton1.png");
    private final double proporcionEliminar = proporcion("/imagenes/Erase1.png");
    private final double proporcionCruz = proporcion("/imagenes/cross1.png");

    private double escala = 1;
    private int desfaseX = 0;
    private int desfaseY = 0;

    public ItinerariosPanel() {
        super(null);
        setOpaque(true);
        setPreferredSize(new Dimension(1100, 616));

        // Unidad y Conductor son editables: se escribe para buscar y la lista se va filtrando
        comboUnidades.setEditable(true);
        comboConductores.setEditable(true);

        EstiloCombo.aplicar(comboDiaSemana, true, true,
                v -> v instanceof DayOfWeek ? nombreDia((DayOfWeek) v) : String.valueOf(v));
        EstiloCombo.aplicar(cbHora, false, true, null);
        EstiloCombo.aplicar(cbMinuto, false, true, null);
        EstiloCombo.aplicar(comboTipoRuta, true, false, null, FONDO_BARRA);
        EstiloCombo.aplicar(comboUnidades, true, false, null, FONDO_BARRA);
        EstiloCombo.aplicar(comboConductores, true, false, null, FONDO_BARRA);

        for (JLabel etiqueta : new JLabel[]{lblOrigen, lblDestino, lblDia, lblHora, lblDosPuntos,
                lblTipo, lblUnidad, lblConductor}) {
            etiqueta.setForeground(Color.BLACK);
            add(etiqueta);
        }
        add(campoOrigen);
        add(campoDestino);
        add(comboDiaSemana);
        add(cbHora);
        add(cbMinuto);
        add(comboTipoRuta);
        add(comboUnidades);
        add(comboConductores);
        add(botonRegistrar);
        add(botonModificar);
        add(botonEliminar);
        add(botonRegresar);
        add(scroll);
        setComponentZOrder(lblDosPuntos, 0); // los dos puntos quedan encima de los combos de hora y minuto
        botonRegresar.setFocusable(false);

        configurarTabla();
    }

    // ------------------------------------------------------------------ acceso desde la ventana

    public JTextField getCampoOrigen() { return campoOrigen; }
    public JTextField getCampoDestino() { return campoDestino; }
    public JComboBox<DayOfWeek> getComboDiaSemana() { return comboDiaSemana; }
    public JComboBox<String> getCbHora() { return cbHora; }
    public JComboBox<String> getCbMinuto() { return cbMinuto; }
    public JComboBox<TipoRuta> getComboTipoRuta() { return comboTipoRuta; }
    public JComboBox<Unidad> getComboUnidades() { return comboUnidades; }
    public JComboBox<Conductor> getComboConductores() { return comboConductores; }
    public BotonImagen getBotonRegistrar() { return botonRegistrar; }
    public BotonImagen getBotonModificar() { return botonModificar; }
    public BotonImagen getBotonEliminar() { return botonEliminar; }
    public BotonImagen getBotonRegresar() { return botonRegresar; }
    public JTable getTabla() { return tabla; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }

    private static String[] generarHoras() {
        String[] horas = new String[24];
        for (int i = 0; i < 24; i++) {
            horas[i] = String.format("%02d", i); // "00", "01", ..., "23"
        }
        return horas;
    }

    private static double proporcion(String ruta) {
        BufferedImage img = RecursosImagen.cargar(ruta);
        return (double) img.getHeight() / img.getWidth();
    }

    // ------------------------------------------------------------------ configuración

    private void configurarTabla() {
        tabla.setTableHeader(null);
        tabla.setOpaque(false);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(false);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        FilaPildoraRenderer renderer = new FilaPildoraRenderer(AZUL, GRIS_FILA);
        tabla.setDefaultRenderer(Object.class, renderer);

        scroll.setOpaque(false);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scroll.getVerticalScrollBar().setUI(new BarraDesplazamientoUI(GRIS_PISTA, AZUL, true));
        scroll.getVerticalScrollBar().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
    }

    // ------------------------------------------------------------------ layout y dibujo

    private int px(double valor) {
        return Math.max(1, (int) Math.round(valor * escala));
    }

    private Rectangle rect(double x, double y, double ancho, double alto) {
        return new Rectangle(desfaseX + (int) Math.round(x * escala),
                desfaseY + (int) Math.round(y * escala), px(ancho), px(alto));
    }

    private Rectangle rect(double[] r) {
        return rect(r[0], r[1], r[2], r[3]);
    }

    // Botón de ancho fijo y alto según la imagen, centrado verticalmente en cy
    private Rectangle rectBoton(double x, double cy, double ancho, double proporcion) {
        double alto = ancho * proporcion;
        return rect(x, cy - alto / 2, ancho, alto);
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        escala = Math.min(w / REF_ANCHO, h / REF_ALTO);
        desfaseX = (int) Math.round((w - REF_ANCHO * escala) / 2);
        desfaseY = (int) Math.round((h - REF_ALTO * escala) / 2);

        Font fuenteEtiquetaChica = new Font("Arial", Font.PLAIN, px(21));
        Font fuenteEtiqueta = new Font("Arial", Font.PLAIN, px(26));
        Font fuenteCampo = new Font("Arial", Font.PLAIN, px(20));

        lblOrigen.setFont(fuenteEtiquetaChica);
        lblOrigen.setBounds(rect(ETIQUETA_ORIGEN));
        lblDestino.setFont(fuenteEtiquetaChica);
        lblDestino.setBounds(rect(ETIQUETA_DESTINO));
        lblDia.setFont(fuenteEtiqueta);
        lblDia.setBounds(rect(ETIQUETA_DIA));
        lblHora.setFont(fuenteEtiqueta);
        lblHora.setBounds(rect(ETIQUETA_HORA));
        lblDosPuntos.setFont(new Font("Arial", Font.BOLD, px(30)));
        lblDosPuntos.setBounds(rect(DOS_PUNTOS));
        lblTipo.setFont(fuenteEtiqueta);
        lblTipo.setBounds(rect(ETIQUETA_TIPO));
        lblUnidad.setFont(fuenteEtiqueta);
        lblUnidad.setBounds(rect(ETIQUETA_UNIDAD));
        lblConductor.setFont(fuenteEtiqueta);
        lblConductor.setBounds(rect(ETIQUETA_CONDUCTOR));

        EmptyBorder margenTexto = new EmptyBorder(0, px(18), 0, px(18));
        campoOrigen.setFont(fuenteCampo);
        campoOrigen.setBorder(margenTexto);
        campoOrigen.setBounds(rect(CAMPO_ORIGEN));
        campoDestino.setFont(fuenteCampo);
        campoDestino.setBorder(margenTexto);
        campoDestino.setBounds(rect(CAMPO_DESTINO));

        colocarCombo(comboDiaSemana, COMBO_DIA, fuenteCampo);
        colocarCombo(cbHora, COMBO_HORA, fuenteCampo);
        colocarCombo(cbMinuto, COMBO_MINUTO, fuenteCampo);
        colocarCombo(comboTipoRuta, COMBO_TIPO, fuenteCampo);
        colocarCombo(comboUnidades, COMBO_UNIDAD, fuenteCampo);
        colocarCombo(comboConductores, COMBO_CONDUCTOR, fuenteCampo);

        botonRegistrar.setBounds(rectBoton(BOTON_REGISTRAR[0], BOTON_REGISTRAR[1], BOTONES_ANCHO, proporcionRegistrar));
        botonModificar.setBounds(rectBoton(BOTON_MODIFICAR[0], BOTON_MODIFICAR[1], BOTONES_ANCHO, proporcionModificar));
        botonEliminar.setBounds(rectBoton(BOTON_ELIMINAR[0], BOTON_ELIMINAR[1], BOTONES_ANCHO, proporcionEliminar));
        botonRegresar.setBounds(rect(CRUZ_X, CRUZ_Y, CRUZ_ANCHO, CRUZ_ANCHO * proporcionCruz));

        // Tabla
        tabla.setRowHeight(px(FILA_ALTO));
        tabla.setFont(new Font("Arial", Font.PLAIN, px(16)));
        int anchoBarra = px(BARRA_ANCHO + BARRA_SEPARACION);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(anchoBarra, 0));
        scroll.getVerticalScrollBar().setBorder(new EmptyBorder(0, px(BARRA_SEPARACION), 0, 0));
        Rectangle area = rect(SCROLL_X, SCROLL_Y, SCROLL_ANCHO, SCROLL_ALTO);
        scroll.setBounds(area);
        ajustarColumnas(area.width - anchoBarra);
        scroll.doLayout();
    }

    private void colocarCombo(JComboBox<?> combo, double[] medidas, Font fuente) {
        combo.setFont(fuente);
        if (combo.isEditable() && combo.getEditor().getEditorComponent() instanceof JTextField) {
            combo.getEditor().getEditorComponent().setFont(fuente);
        }
        combo.setBounds(rect(medidas));
    }

    // Reparte el ancho disponible entre las columnas según las proporciones de la maqueta
    private void ajustarColumnas(int anchoTotal) {
        double suma = 0;
        for (double a : ANCHO_COLUMNAS) {
            suma += a;
        }
        int acumulado = 0;
        for (int i = 0; i < ANCHO_COLUMNAS.length; i++) {
            int ancho = (i == ANCHO_COLUMNAS.length - 1)
                    ? anchoTotal - acumulado
                    : (int) Math.round(ANCHO_COLUMNAS[i] / suma * anchoTotal);
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

        Rectangle t = rect(TARJETA);
        tarjeta.dibujar(g2, t.x, t.y, t.width, t.height, px(50));

        dibujarTabla(g2);
        g2.dispose();
    }

    // Fondo gris de la tabla (con borde blanco) y barra azul de encabezado con sus títulos
    private void dibujarTabla(Graphics2D g2) {
        Rectangle cuerpo = rect(TABLA_X, TABLA_Y + ENCABEZADO_ALTO / 2, TABLA_ANCHO, TABLA_ALTO - ENCABEZADO_ALTO / 2);
        int arco = px(46);
        int borde = px(4);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(cuerpo.x, cuerpo.y, cuerpo.width, cuerpo.height, arco, arco);
        g2.setPaint(new GradientPaint(0, cuerpo.y, CUERPO_ARRIBA, 0, cuerpo.y + cuerpo.height, CUERPO_ABAJO));
        g2.fillRoundRect(cuerpo.x + borde, cuerpo.y + borde, cuerpo.width - 2 * borde, cuerpo.height - 2 * borde,
                arco - borde, arco - borde);

        Rectangle enc = rect(TABLA_X, TABLA_Y, TABLA_ANCHO, ENCABEZADO_ALTO);
        g2.setColor(AZUL);
        g2.fillRoundRect(enc.x, enc.y, enc.width, enc.height, enc.height, enc.height);

        g2.setFont(new Font("Arial", Font.BOLD, px(24)));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        int baseY = enc.y + (enc.height - fm.getHeight()) / 2 + fm.getAscent();
        // Cada título va centrado sobre su columna (mismas proporciones que la tabla)
        double sumaAnchos = 0;
        for (double a : ANCHO_COLUMNAS) {
            sumaAnchos += a;
        }
        double anchoColumnas = SCROLL_ANCHO - BARRA_ANCHO - BARRA_SEPARACION;
        double inicio = 0;
        for (int i = 0; i < COLUMNAS.length; i++) {
            double ancho = ANCHO_COLUMNAS[i] / sumaAnchos * anchoColumnas;
            int centro = desfaseX + (int) Math.round((SCROLL_X + inicio + ancho / 2) * escala);
            g2.drawString(COLUMNAS[i], centro - fm.stringWidth(COLUMNAS[i]) / 2, baseY);
            inicio += ancho;
        }
    }
}
