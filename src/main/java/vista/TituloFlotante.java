package vista;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.HierarchyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.Timer;

// Funcion para el título flotante en pantallas
final class TituloFlotante {

    private static final double REF_ANCHO = 1920;
    private static final double REF_ALTO = 1080;
    private static final int PERIODO_MS = 2400;
    private static final int INTERVALO_MS = 40;
    private static final double AMPLITUD_BASE = 6;     // píxeles con un panel de 1021 px de ancho
    private static final double ANCHO_BASE = 1021;

    private final transient RecursosImagen.Escalado imagen;
    private final double x;
    private final double y;
    private final double ancho;
    private final double alto;
    private final double proporcion; // alto / ancho de la imagen

    private Timer temporizador;
    private JComponent panel;
    private double desplazamiento = 0;
    private double amplitudActual = AMPLITUD_BASE;

//
    TituloFlotante(String ruta, double x, double y, double ancho, double alto) {
        RecursosImagen.Escalado cargada = null;
        double prop = alto / ancho;
        try {
            BufferedImage original = RecursosImagen.cargar(ruta);
            prop = (double) original.getHeight() / original.getWidth();
            cargada = new RecursosImagen.Escalado(ruta);
        } catch (IllegalStateException e) {
            // Si falta la imagen no se rompe la pantalla: simplemente no se dibuja el título animado
            System.err.println("TituloFlotante: " + e.getMessage());
        }
        this.imagen = cargada;
        this.proporcion = prop;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    // start animacion
    void conectar(JComponent componente) {
        this.panel = componente;
        componente.addHierarchyListener(evento -> {
            if ((evento.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (componente.isShowing()) {
                    iniciar();
                } else {
                    detener();
                }
            }
        });
        if (componente.isShowing()) {
            iniciar();
        }
    }

    private void iniciar() {
        if (imagen == null || temporizador != null) {
            return;
        }
        temporizador = new Timer(INTERVALO_MS, evento -> {
            long tiempo = System.nanoTime() / 1_000_000L;
            double fase = (tiempo % PERIODO_MS) / (double) PERIODO_MS * Math.PI * 2.0;
            desplazamiento = Math.round(Math.sin(fase) * amplitudActual);
            repintarTitulo();
        });
        temporizador.start();
    }

    private void detener() {
        if (temporizador != null) {
            temporizador.stop();
            temporizador = null;
        }
    }

    // Zona que ocupa el título para un tamaño de panel y un desplazamiento vertical dados
    private Rectangle zona(int anchoPanel, int altoPanel, double desplazamientoY) {
        double escalaX = anchoPanel / REF_ANCHO;
        double escalaY = altoPanel / REF_ALTO;
        int w = Math.max(1, (int) Math.round(ancho * escalaX));
        int h = Math.max(1, (int) Math.round(w * proporcion));
        int px = (int) Math.round(x * escalaX);
        int py = (int) Math.round((y + alto / 2) * escalaY - h / 2.0 + desplazamientoY);
        return new Rectangle(px, py, w, h);
    }

    // Solo se repinta la franja del título (no todo el panel) para que la animación sea ligera
    private void repintarTitulo() {
        if (panel == null || panel.getWidth() <= 0) {
            return;
        }
        int amplitud = (int) Math.ceil(amplitudActual);
        Rectangle reposo = zona(panel.getWidth(), panel.getHeight(), 0);
        panel.repaint(reposo.x - 4, reposo.y - amplitud - 4, reposo.width + 8, reposo.height + 2 * amplitud + 8);
    }

   //Dibuja el título en su posición actual de la onda. Llamar justo después de dibujar el fond
    void dibujar(Graphics2D g2, int anchoPanel, int altoPanel) {
        if (imagen == null) {
            return;
        }
        amplitudActual = Math.max(2, AMPLITUD_BASE * anchoPanel / ANCHO_BASE);
        Rectangle r = zona(anchoPanel, altoPanel, desplazamiento);
        g2.drawImage(imagen.obtener(r.width, r.height), r.x, r.y, null);
    }
}
