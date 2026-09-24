package modelo;

import java.util.Objects;

public class Usuario {
    private String nombre;
    private String apellido;
    private String cedula;
    private Rol rol;
    private String username;
    private String hashContrasena;

    /** Constructor usado por el registro completo de la aplicación. */
    public Usuario(String nombre, String apellido, String cedula, Rol rol,
                   String username, String passwordPersistido) {
        validar(nombre, apellido, rol, username, passwordPersistido);
        this.nombre = nombre.trim();
        this.apellido = apellido == null ? "" : apellido.trim();
        this.cedula = cedula == null ? "" : cedula.trim();
        this.rol = rol;
        this.username = normalizar(username);
        this.hashContrasena = passwordPersistido;
    }

    /** Constructor compatible con la primera versión del proyecto. */
    public Usuario(String nombreUsuario, String nombreCompleto, Rol rol, String hashContrasena) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio.");
        }
        if (rol == null) throw new IllegalArgumentException("El rol es obligatorio.");
        if (hashContrasena == null || hashContrasena.isBlank()) {
            throw new IllegalArgumentException("El hash de contraseña es obligatorio.");
        }
        String completo = nombreCompleto.trim();
        int espacio = completo.indexOf(' ');
        this.nombre = espacio > 0 ? completo.substring(0, espacio) : completo;
        this.apellido = espacio > 0 ? completo.substring(espacio + 1).trim() : "";
        this.cedula = "";
        this.rol = rol;
        this.username = normalizar(nombreUsuario);
        this.hashContrasena = hashContrasena;
    }

    private static void validar(String nombre, String apellido, Rol rol,
                                String username, String passwordPersistido) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (apellido == null) throw new IllegalArgumentException("El apellido es obligatorio.");
        if (rol == null) throw new IllegalArgumentException("El rol es obligatorio.");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        if (passwordPersistido == null || passwordPersistido.isBlank()) throw new IllegalArgumentException("La contraseña es obligatoria.");
    }

    private static String normalizar(String value) {
        return value.trim().toLowerCase();
    }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCedula() { return cedula; }
    public Rol getRol() { return rol; }
    public String getUsername() { return username; }
    public String getNombreUsuario() { return username; }
    public String getNombreCompleto() { return (nombre + " " + apellido).trim(); }
    public String getHashContrasena() { return hashContrasena; }
    public String getPassword() { return hashContrasena; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setRol(Rol rol) { this.rol = rol; }
    public void setUsername(String username) { this.username = normalizar(username); }
    public void setPassword(String password) { this.hashContrasena = password; }

    public boolean coincidePassword(String passwordIngresada) {
        if (passwordIngresada == null || hashContrasena == null) return false;
        return hashContrasena.equals(passwordIngresada)
                || hashContrasena.equals(AutenticacionServicio.hash(passwordIngresada));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario otro)) return false;
        return username.equalsIgnoreCase(otro.username);
    }

    @Override
    public int hashCode() { return username.toLowerCase().hashCode(); }

    @Override
    public String toString() {
        return "Usuario{" + "username='" + username + "', nombre='" + getNombreCompleto()
                + "', rol=" + rol + '}';
    }
}
