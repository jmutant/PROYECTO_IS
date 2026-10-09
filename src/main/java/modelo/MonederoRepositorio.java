package modelo;

import java.math.BigDecimal;

//Almacenar El saldo de los n usuarios
public interface MonederoRepositorio {
    BigDecimal obtenerSaldo(String username);

    void guardarSaldo(String username, BigDecimal saldo);

    //Asegura que el usuario tenga un registro de saldo, iniciándolo en Bs 0,00
    default void asegurarUsuario(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        obtenerSaldo(username.trim());
    }
}
