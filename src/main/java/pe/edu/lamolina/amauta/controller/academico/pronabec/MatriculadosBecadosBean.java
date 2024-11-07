package pe.edu.lamolina.amauta.controller.academico.pronabec;

public class MatriculadosBecadosBean {
    private String dni;
    private String codigo_estudiante;
    private String apellidos_nombres;
    private String tipo_beca;
    private String year_convocatoria;
    private String nombre_institucion;
    private String carrera;
    private String periodo_academico;
    private String ciclo;
    private String curso_matriculado;
    private String nota;
    private Long veces_desaprobado;
    private Long promedio_ponderado;
    private String condicion;

    public MatriculadosBecadosBean() {}

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCodigo_estudiante() {
        return codigo_estudiante;
    }

    public void setCodigo_estudiante(String codigo_estudiante) {
        this.codigo_estudiante = codigo_estudiante;
    }

    public String getApellidos_nombres() {
        return apellidos_nombres;
    }

    public void setApellidos_nombres(String apellidos_nombres) {
        this.apellidos_nombres = apellidos_nombres;
    }

    public String getTipo_beca() {
        return tipo_beca;
    }

    public void setTipo_beca(String tipo_beca) {
        this.tipo_beca = tipo_beca;
    }

    public String getYear_convocatoria() {
        return year_convocatoria;
    }

    public void setYear_convocatoria(String year_convocatoria) {
        this.year_convocatoria = year_convocatoria;
    }

    public String getNombre_institucion() {
        return nombre_institucion;
    }

    public void setNombre_institucion(String nombre_institucion) {
        this.nombre_institucion = nombre_institucion;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getPeriodo_academico() {
        return periodo_academico;
    }

    public void setPeriodo_academico(String periodo_academico) {
        this.periodo_academico = periodo_academico;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCurso_matriculado() {
        return curso_matriculado;
    }

    public void setCurso_matriculado(String curso_matriculado) {
        this.curso_matriculado = curso_matriculado;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Long getVeces_desaprobado() {
        return veces_desaprobado;
    }

    public void setVeces_desaprobado(Long veces_desaprobado) {
        this.veces_desaprobado = veces_desaprobado;
    }

    public Long getPromedio_ponderado() {
        return promedio_ponderado;
    }

    public void setPromedio_ponderado(Long promedio_ponderado) {
        this.promedio_ponderado = promedio_ponderado;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }
}
