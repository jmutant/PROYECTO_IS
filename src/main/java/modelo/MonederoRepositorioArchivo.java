package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


//Persistencia local de los saldos para cada Usuario en formato [username|saldoConDosDecimales]
public class MonederoRepositorioArchivo implements MonederoRepositorio {

    private static final String ARCHIVO = "monederos.db";
    private static final BigDecimal SALDO_INICIAL = BigDecimal.ZERO.setScale(2);
    private final AlmacenDatosLocal almacen;

    public MonederoRepositorioArchivo(AlmacenDatosLocal almacen) {
        if (almacen == null) {
            throw new IllegalArgumentException("El almacén de datos es obligatorio.");
        }
        this.almacen = almacen;
        if (!almacen.leer(ARCHIVO).isEmpty()) {
            return;
        }
        almacen.reemplazar(ARCHIVO, new ArrayList<>());
    }

    @Override
    public synchronized BigDecimal obtenerSaldo(String username) {
        String clave = normalizarUsuario(username);
        List<String> lineas = almacen.leer(ARCHIVO);
        for (String linea : lineas) {
            if (linea == null || linea.isBlank()) {
                continue;
            }
            String[] campos = linea.trim().split("\\|", -1);
            if (campos.length != 2) {
                continue;
            }
            try {
                String usuario = decodificar(campos[0]);
                if (usuario.equalsIgnoreCase(clave)) {
                    return normalizarSaldo(new BigDecimal(campos[1]));
                }
            } catch (RuntimeException ignored) {
                // Una línea corrupta no debe impedir el acceso al monedero.
            }
        }

        // Primer acceso: el usuario comienza con Bs 0,00 y queda persistido.
        guardarSaldo(clave, SALDO_INICIAL);
        return SALDO_INICIAL;
    }

    @Override
    public synchronized void guardarSaldo(String username, BigDecimal saldo) {
        String clave = normalizarUsuario(username);
        BigDecimal saldoNormalizado = normalizarSaldo(saldo);

        List<String> lineas = almacen.leer(ARCHIVO);
        boolean encontrado = false;
        List<String> nuevas = new ArrayList<>();

        for (String linea : lineas) {
            if (linea == null || linea.isBlank()) {
                continue;
            }

            String[] campos = linea.trim().split("\\|", -1);
            if (campos.length != 2) {
                // Conservamos líneas que no tengan el formato del monedero.
                nuevas.add(linea);
                continue;
            }

            try {
                String usuario = decodificar(campos[0]);
                if (usuario.equalsIgnoreCase(clave)) {
                    nuevas.add(escribirLinea(clave, saldoNormalizado));
                    encontrado = true;
                } else {
                    nuevas.add(linea);
                }
            } catch (RuntimeException ex) {
                nuevas.add(linea);
            }
        }

        if (!encontrado) {
            nuevas.add(escribirLinea(clave, saldoNormalizado));
        }

        almacen.reemplazar(ARCHIVO, nuevas);
    }

    private static String normalizarUsuario(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        return username.trim().toLowerCase();
    }

    private static BigDecimal normalizarSaldo(BigDecimal saldo) {
        if (saldo == null) {
            throw new IllegalArgumentException("El saldo es obligatorio.");
        }
        if (saldo.scale() > 2) {
            saldo = saldo.setScale(2, RoundingMode.UNNECESSARY);
        } else {
            saldo = saldo.setScale(2, RoundingMode.UNNECESSARY);
        }
        if (saldo.signum() < 0) {
            throw new IllegalArgumentException("El saldo no puede ser negativo.");
        }
        return saldo;
    }

    private static String escribirLinea(String username, BigDecimal saldo) {
        return codificar(username) + "|" + saldo.toPlainString();
    }

    private static String codificar(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private static String decodificar(String texto) {
        return new String(Base64.getDecoder().decode(texto), java.nio.charset.StandardCharsets.UTF_8);
    }
}
