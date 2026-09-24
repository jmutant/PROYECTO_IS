package modelo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

final class TextoSeguro {

    private TextoSeguro() {
    }

    static String codificar(String valor) {
        return Base64.getEncoder().encodeToString(valor.getBytes(StandardCharsets.UTF_8));
    }

    static String decodificar(String valor) {
        return new String(Base64.getDecoder().decode(valor), StandardCharsets.UTF_8);
    }
}
