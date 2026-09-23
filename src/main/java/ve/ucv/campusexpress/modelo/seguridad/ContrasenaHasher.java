package ve.ucv.campusexpress.modelo.seguridad;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Calcula y verifica hashes de contraseñas con PBKDF2 (incluido en el JDK, sin dependencias).
 *
 * <p>Formato almacenado: {@code iteraciones:sal:hash} (sal y hash en Base64). Cada contraseña usa
 * una sal aleatoria, por lo que la misma contraseña produce hashes distintos.</p>
 *
 * <p>Recibe {@code char[]} (lo que entrega {@code JPasswordField.getPassword()}) para poder
 * borrar la contraseña de memoria apenas se usa.</p>
 */
public final class ContrasenaHasher {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 65_536;
    private static final int LONGITUD_HASH_BITS = 256;
    private static final int LONGITUD_SAL_BYTES = 16;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private ContrasenaHasher() {
    }

    /** Devuelve el hash de la contraseña con una sal nueva. No modifica el arreglo recibido. */
    public static String hashear(char[] contrasena) {
        byte[] sal = new byte[LONGITUD_SAL_BYTES];
        ALEATORIO.nextBytes(sal);
        byte[] hash = derivar(contrasena, sal, ITERACIONES);
        Base64.Encoder codificador = Base64.getEncoder();
        return ITERACIONES + ":" + codificador.encodeToString(sal) + ":" + codificador.encodeToString(hash);
    }

    /**
     * Indica si la contraseña corresponde al hash almacenado.
     *
     * @return {@code false} si no coincide o si el hash almacenado tiene un formato inválido
     */
    public static boolean verificar(char[] contrasena, String hashAlmacenado) {
        if (contrasena == null || contrasena.length == 0 || hashAlmacenado == null) {
            return false;
        }
        String[] partes = hashAlmacenado.split(":");
        if (partes.length != 3) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[0]);
            byte[] sal = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);
            byte[] calculado = derivar(contrasena, sal, iteraciones);
            // Comparación en tiempo constante
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException formatoInvalido) {
            return false;
        }
    }

    private static byte[] derivar(char[] contrasena, byte[] sal, int iteraciones) {
        PBEKeySpec especificacion = new PBEKeySpec(contrasena, sal, iteraciones, LONGITUD_HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacion).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}
