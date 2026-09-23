package ve.ucv.campusexpress.util;

import java.util.ResourceBundle;

/**
 * Acceso centralizado a los textos de la aplicación, definidos en
 * {@code src/main/resources/mensajes.properties}.
 *
 * <p>Así los mensajes que exigen los criterios de aceptación (por ejemplo
 * "Usuario o contraseña incorrectos") viven en un solo lugar.</p>
 */
public final class Mensajes {

    private static final ResourceBundle TEXTOS = ResourceBundle.getBundle("mensajes");

    private Mensajes() {
    }

    /** Devuelve el texto asociado a la clave. */
    public static String get(String clave) {
        return TEXTOS.getString(clave);
    }

    /** Devuelve el texto asociado a la clave, reemplazando los {@code %s} por los argumentos. */
    public static String get(String clave, Object... argumentos) {
        return String.format(TEXTOS.getString(clave), argumentos);
    }
}
