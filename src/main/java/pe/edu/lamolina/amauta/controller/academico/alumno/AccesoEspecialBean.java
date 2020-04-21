package pe.edu.lamolina.amauta.controller.academico.alumno;

import pe.edu.lamolina.model.academico.Alumno;

public class AccesoEspecialBean {

    Alumno alumno;
    String correo; // correo a remitir las credenciales generadas
    String dni; // userDni
    String contraseña; // userDniPass
 
    public AccesoEspecialBean() {
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
}
