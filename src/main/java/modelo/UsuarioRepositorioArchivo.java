package modelo;

import java.io.IOException;
//import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Usuarios persistidos en un archivo de texto plano, uno por línea, con campos separados por '|'
public class UsuarioRepositorioArchivo implements UsuarioRepositorio {
    private static final String ARCHIVO = "usuarios.db";
    private final AlmacenDatosLocal almacen;

    public UsuarioRepositorioArchivo(AlmacenDatosLocal almacen) {
        this.almacen = almacen;
        inicializar();
    }

//Compatibilidad con otros metodos que reciben una ruta de archivo específica, por ejemplo para pruebas unitarias. Así funcionan en unisono
    public UsuarioRepositorioArchivo(Path rutaArchivo) {
        this.almacen = new AlmacenDatosLocal(
                rutaArchivo.toAbsolutePath().normalize().getParent() == null
                        ? Path.of("data")
                        : rutaArchivo.toAbsolutePath().normalize().getParent());
        inicializarEnRuta(rutaArchivo.toAbsolutePath().normalize());
    }

    private void inicializar() {
        Path archivo = almacen.getDirectorio().resolve(ARCHIVO);
        inicializarEnRuta(archivo);
    }

    private void inicializarEnRuta(Path archivo) {
        try {
            if (archivo.getParent() != null) {
                Files.createDirectories(archivo.getParent());
            }
            if (!Files.exists(archivo)) {
                Files.createFile(archivo);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo preparar " + archivo, e);
        }
    }
/* 
    private Path rutaArchivo() {
        return almacen.getDirectorio().resolve(ARCHIVO);
    } */

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        String buscado = username.trim();
        return listarTodos().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(buscado))
                .findFirst();
    }

    @Override
    public boolean existePorUsername(String username) {
        return buscarPorUsername(username).isPresent();
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        for (String linea : almacen.leer(ARCHIVO)) {
            if (linea == null || linea.isBlank()) {
                continue;
            }
            Usuario usuario = leerLinea(linea);
            if (usuario != null) {
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    @Override
    public synchronized void guardar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        if (existePorUsername(usuario.getUsername())) {
            throw new UsuarioDuplicadoException(
                    "El nombre de usuario '" + usuario.getUsername() + "' ya está registrado.");
        }

        List<String> lineas = almacen.leer(ARCHIVO);
        lineas.add(escribirLinea(usuario));
        almacen.reemplazar(ARCHIVO, lineas);
    }

    private Usuario leerLinea(String linea) {
        String[] campos = linea.trim().split("\\|", -1);

        // Formato nuevo:
        // nombreBase64|apellidoBase64|cedulaBase64|ROL|usernameBase64|passwordPlano
        if (campos.length == 6) {
            try {
                return new Usuario(
                        TextoSeguro.decodificar(campos[0]),
                        TextoSeguro.decodificar(campos[1]),
                        TextoSeguro.decodificar(campos[2]),
                        Rol.desdeCadena(campos[3]),
                        TextoSeguro.decodificar(campos[4]),
                        campos[5]);
            } catch (RuntimeException ignored) {
                // Puede ser un archivo antiguo. Se intenta el formato legacy abajo.
            }
        }

        // Formato anterior: nombre_,apellido_,cedula_,rol_,username_,password_
        String[] antiguos = linea.split(",", -1);
        if (antiguos.length >= 6) {
            try {
                return new Usuario(
                        limpiarCampo(antiguos[0]),
                        limpiarCampo(antiguos[1]),
                        limpiarCampo(antiguos[2]),
                        Rol.desdeCadena(limpiarCampo(antiguos[3])),
                        limpiarCampo(antiguos[4]),
                        limpiarCampo(antiguos[5]));
            } catch (RuntimeException ignored) {
                // Línea inválida: se ignora para que un registro corrupto no bloquee todo el login.
            }
        }
        return null;
    }

    private String escribirLinea(Usuario usuario) {
        return String.join("|",
                TextoSeguro.codificar(usuario.getNombre()),
                TextoSeguro.codificar(usuario.getApellido()),
                TextoSeguro.codificar(usuario.getCedula()),
                usuario.getRol().name(),
                TextoSeguro.codificar(usuario.getUsername()),
                protegerPassword(usuario.getContrasena()));
    }

    private String limpiarCampo(String campo) {
        String c = campo == null ? "" : campo.trim();
        return c.endsWith("_") ? c.substring(0, c.length() - 1) : c;
    }

    private String protegerPassword(String password) {
        if (password == null) {
            return "";
        }
        // La contraseña se mantiene en texto plano. Solo se evita que una línea en contraseña pueda romper el archivo por saltos de línea o '|'.
        return password.replace("|", " ").replace("\n", " ").replace("\r", " ");
    }
}
