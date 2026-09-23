package ve.ucv.campusexpress.modelo.seguridad;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ContrasenaHasherTest {

    private static final char[] CONTRASENA = "secreto123".toCharArray();

    @Test
    @DisplayName("El hash no contiene la contraseña en texto plano")
    void elHashNoContieneLaContrasena() {
        String hash = ContrasenaHasher.hashear(CONTRASENA);

        assertFalse(hash.contains("secreto123"));
    }

    @Test
    @DisplayName("La misma contraseña genera hashes distintos (sal aleatoria)")
    void hashesDistintosParaLaMismaContrasena() {
        assertNotEquals(ContrasenaHasher.hashear(CONTRASENA), ContrasenaHasher.hashear(CONTRASENA));
    }

    @Test
    void verificaUnaContrasenaCorrecta() {
        String hash = ContrasenaHasher.hashear(CONTRASENA);

        assertTrue(ContrasenaHasher.verificar("secreto123".toCharArray(), hash));
    }

    @Test
    void rechazaUnaContrasenaIncorrecta() {
        String hash = ContrasenaHasher.hashear(CONTRASENA);

        assertFalse(ContrasenaHasher.verificar("Secreto123".toCharArray(), hash));
        assertFalse(ContrasenaHasher.verificar("otra-clave".toCharArray(), hash));
    }

    @Test
    void rechazaContrasenaVaciaONula() {
        String hash = ContrasenaHasher.hashear(CONTRASENA);

        assertFalse(ContrasenaHasher.verificar(new char[0], hash));
        assertFalse(ContrasenaHasher.verificar(null, hash));
    }

    @Test
    void rechazaHashNulo() {
        assertFalse(ContrasenaHasher.verificar(CONTRASENA, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "sin-separadores", "1:2", "abc:AAAA:AAAA", "0:AAAA:AAAA", "-5:AAAA:AAAA", "10:%%%:AAAA"})
    @DisplayName("Un hash almacenado con formato inválido nunca verifica")
    void rechazaHashConFormatoInvalido(String hashInvalido) {
        assertFalse(ContrasenaHasher.verificar(CONTRASENA, hashInvalido));
    }
}
