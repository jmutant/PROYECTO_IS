package modelo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Almacén local sencillo para el MVP. Los repositorios del modelo guardan sus
 * registros en archivos UTF-8 separados y no dependen de Swing ni del controlador.
 */
public final class AlmacenDatosLocal {

    private final Path directorio;

    public AlmacenDatosLocal(Path directorio) {
        this.directorio = Objects.requireNonNull(directorio, "El directorio es obligatorio").toAbsolutePath();
        crearDirectorioSiEsNecesario();
    }

    /** Directorio de datos persistentes de la instalación del usuario. */
    public static AlmacenDatosLocal porDefecto() {
        String configuracion = System.getProperty("campus.express.data");
        if (configuracion != null && !configuracion.isBlank()) {
            return new AlmacenDatosLocal(Path.of(configuracion));
        }
        String casa = System.getProperty("user.home", ".");
        return new AlmacenDatosLocal(Path.of(casa, ".campus-express", "datos"));
    }

    public Path getDirectorio() {
        return directorio;
    }

    public synchronized List<String> leer(String nombreArchivo) {
        Objects.requireNonNull(nombreArchivo, "El nombre del archivo es obligatorio");
        Path archivo = directorio.resolve(nombreArchivo);
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try {
            return new ArrayList<>(Files.readAllLines(archivo, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("No se pudieron leer los datos de " + archivo, e);
        }
    }

    /** Reemplaza el archivo de forma segura usando un archivo temporal. */
    public synchronized void reemplazar(String nombreArchivo, List<String> lineas) {
        Objects.requireNonNull(nombreArchivo, "El nombre del archivo es obligatorio");
        Objects.requireNonNull(lineas, "Las líneas son obligatorias");
        crearDirectorioSiEsNecesario();

        Path archivo = directorio.resolve(nombreArchivo);
        Path temporal;
        try {
            temporal = Files.createTempFile(directorio, nombreArchivo + ".", ".tmp");
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudieron guardar los datos en " + archivo, e);
        }
    }

    private void crearDirectorioSiEsNecesario() {
        try {
            Files.createDirectories(directorio);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de datos " + directorio, e);
        }
    }
}
