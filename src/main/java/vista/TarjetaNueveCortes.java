package vista;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

// cuadro blnaco
public final class TarjetaNueveCortes {
    private static final int CORTE = 130; // píxeles de cada esquina en la imagen original

    private final BufferedImage origen;
    private BufferedImage[] partes;
    private int cacheW;
    private int cacheH;
    private int cacheEsquina;

    public TarjetaNueveCortes(String ruta) {
        this.origen = RecursosImagen.cargar(ruta);
    }


    public void dibujar(Graphics2D g2, int x, int y, int w, int h, int esquina) {
        int d = Math.max(1, Math.min(esquina, Math.min(w, h) / 2));
        if (partes == null || cacheW != w || cacheH != h || cacheEsquina != d) {
            construir(w, h, d);
        }
        int[] dx = {0, d, w - d, w};
        int[] dy = {0, d, h - d, h};
        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                g2.drawImage(partes[fila * 3 + col], x + dx[col], y + dy[fila], null);
            }
        }
    }

    private void construir(int w, int h, int d) {
        int sw = origen.getWidth();
        int sh = origen.getHeight();
        int[] xs = {0, CORTE, sw - CORTE, sw};
        int[] ys = {0, CORTE, sh - CORTE, sh};
        int[] dx = {0, d, w - d, w};
        int[] dy = {0, d, h - d, h};
        partes = new BufferedImage[9];
        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {
                BufferedImage trozo = origen.getSubimage(xs[col], ys[fila],
                        xs[col + 1] - xs[col], ys[fila + 1] - ys[fila]);
                partes[fila * 3 + col] = RecursosImagen.escalar(trozo,
                        Math.max(1, dx[col + 1] - dx[col]), Math.max(1, dy[fila + 1] - dy[fila]));
            }
        }
        cacheW = w;
        cacheH = h;
        cacheEsquina = d;
    }
}




// :D
