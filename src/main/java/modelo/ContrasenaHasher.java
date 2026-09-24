package modelo;

import java.util.Arrays;

/** Compatibilidad con la primera versión del proyecto; delega en SHA-256. */
public final class ContrasenaHasher {
    private ContrasenaHasher() {}

    public static String hashear(char[] password) {
        if (password == null) throw new IllegalArgumentException("La contraseña es obligatoria.");
        String texto = new String(password);
        try {
            return AutenticacionServicio.hash(texto);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public static boolean verificar(char[] password, String hash) {
        if (password == null || hash == null) return false;
        String texto = new String(password);
        try {
            return hash.equals(AutenticacionServicio.hash(texto));
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
