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
 * Almacén local persistente del sistema.
 *
 * <p>Por defecto utiliza la carpeta {@code data/} ubicada en el directorio
 * desde el que se ejecuta la aplicación. Así, todas las partes del sistema
 * utilizan una única ubicación de datos.</p>
 */
public final class AlmacenDatosLocal {

    private final Path directorio;

    public AlmacenDatosLocal(Path directorio) {
        this.directorio = Objects.requireNonNull(directorio, "El directorio es obligatorio")
                .toAbsolutePath().normalize();
        crearDirectorioSiEsNecesario();
    }

    /**
     * Directorio predeterminado: data/ junto al proyecto/JAR desde el que se
     * ejecuta la aplicación. También puede cambiarse con
     * -Dcampus.express.data=... si en el futuro se necesita otra ubicación.
     */
    public static AlmacenDatosLocal porDefecto() {
        String configuracion = System.getProperty("campus.express.data");
        if (configuracion != null && !configuracion.isBlank()) {
            return new AlmacenDatosLocal(Path.of(configuracion));
        }
        return new AlmacenDatosLocal(Path.of("data"));
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

    /** Reemplaza un archivo usando una escritura temporal para evitar archivos a medio escribir. */
    public synchronized void reemplazar(String nombreArchivo, List<String> lineas) {
        Objects.requireNonNull(nombreArchivo, "El nombre del archivo es obligatorio");
        Objects.requireNonNull(lineas, "Las líneas son obligatorias");
        crearDirectorioSiEsNecesario();

        Path archivo = directorio.resolve(nombreArchivo);
        Path temporal = null;
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
        } finally {
            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException ignored) {
                    // El archivo temporal ya no afecta al funcionamiento del sistema.
                }
            }
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
