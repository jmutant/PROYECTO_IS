package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepositorioArchivo implements UsuarioRepositorio {
    private final Path rutaArchivo;

    public UsuarioRepositorioArchivo(Path rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        inicializar();
    }

    /** Compatibilidad con la arquitectura anterior basada en AlmacenDatosLocal. */
    public UsuarioRepositorioArchivo(AlmacenDatosLocal almacen) {
        this(almacen.getDirectorio().resolve("usuarios.db"));
    }

    private void inicializar() {
        try {
            if (rutaArchivo.getParent() != null) {
                Files.createDirectories(rutaArchivo.getParent());
            }
            if (!Files.exists(rutaArchivo)) {
                Files.createFile(rutaArchivo);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo preparar " + rutaArchivo, e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        if (username == null) return Optional.empty();
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
        try (BufferedReader reader = Files.newBufferedReader(rutaArchivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] campos = linea.split(",", -1);
                if (campos.length < 6) continue;

                String nombre = limpiarCampo(campos[0]);
                String apellido = limpiarCampo(campos[1]);
                String cedula = limpiarCampo(campos[2]);
                Rol rol = Rol.desdeCadena(limpiarCampo(campos[3]));
                String username = limpiarCampo(campos[4]);
                String passwordPersistida = limpiarCampo(campos[5]);

                usuarios.add(new Usuario(nombre, apellido, cedula, rol, username, passwordPersistida));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error al leer usuarios de " + rutaArchivo, e);
        }
        return usuarios;
    }

    @Override
    public synchronized void guardar(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("El usuario es obligatorio.");
        if (existePorUsername(usuario.getUsername())) {
            throw new UsuarioDuplicadoException(
                    "El nombre de usuario '" + usuario.getUsername() + "' ya está registrado.");
        }

        // Formato: nombre_,apellido_,cedula_,rol_,username_,password_
        String linea = String.format("%s_,%s_,%s_,%s_,%s_,%s_%n",
                proteger(usuario.getNombre()),
                proteger(usuario.getApellido()),
                proteger(usuario.getCedula()),
                usuario.getRol().name(),
                proteger(usuario.getUsername()),
                proteger(usuario.getContrasena()));

        try {
            Files.writeString(rutaArchivo, linea, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Error al guardar usuario en " + rutaArchivo, e);
        }
    }

    private String limpiarCampo(String campo) {
        String c = campo.trim();
        return c.endsWith("_") ? c.substring(0, c.length() - 1) : c;
    }

    private String proteger(String campo) {
        return campo == null ? "" : campo.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }
}
