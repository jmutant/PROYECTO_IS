package vista;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public final class RecursosImagen {

    private RecursosImagen() { }

    //Carga una imagen desde el classpath (por ejemplo "/imagenes/appButton.png")
    public static BufferedImage cargar(String ruta) {
        try (InputStream in = RecursosImagen.class.getResourceAsStream(ruta)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró el recurso de imagen: " + ruta);
            }
            return ImageIO.read(in);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer la imagen: " + ruta, ex);
        }
    }

    /// Reescalado de imágenes con buena calidad, usando un algoritmo de reducción progresiva
    public static BufferedImage escalar(BufferedImage origen, int ancho, int alto) {
        int w = Math.max(1, ancho);
        int h = Math.max(1, alto);
        BufferedImage actual = origen;
        int cw = origen.getWidth();
        int ch = origen.getHeight();
        while (cw != w || ch != h) {
            cw = cw > w * 2 ? cw / 2 : w;
            ch = ch > h * 2 ? ch / 2 : h;
            BufferedImage paso = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = paso.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(actual, 0, 0, cw, ch, null);
            g.dispose();
            actual = paso;
        }
        return actual;
    }

    // img cache 
    public static final class Escalado {
        private final BufferedImage origen;
        private BufferedImage cache;

        public Escalado(String ruta) {
            this.origen = cargar(ruta);
        }

        public Image obtener(int ancho, int alto) {
            if (cache == null || cache.getWidth() != Math.max(1, ancho) || cache.getHeight() != Math.max(1, alto)) {
                cache = escalar(origen, ancho, alto);
            }
            return cache;
        }
    }
}
