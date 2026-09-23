package ve.ucv.campusexpress.modelo.repositorio;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Utilidad del modelo para persistir texto sin problemas de separadores ni acentos. */
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
