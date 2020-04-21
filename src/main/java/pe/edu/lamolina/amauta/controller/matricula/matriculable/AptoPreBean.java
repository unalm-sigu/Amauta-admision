package pe.edu.lamolina.amauta.controller.matricula.matriculable;

public class AptoPreBean {

    private String matricula;
    private String apellidos_nombres;
    private String especialidad;
    private String facultad;
    private Long creditos_matriculados;
    private Long creditos_aprobados;
    private Long ciclos_estudiados;
    private String codigo_facultad;
    private Long nivel;
    private String es_3cio_super;

    public AptoPreBean() {
    }

    public AptoPreBean(String matricula, String apellidos_nombres, String especialidad, String facultad, Long creditos_matriculados, Long creditos_aprobados, Long ciclos_estudiados, String codigo_facultad, Long nivel, String es_3cio_super) {
        this.matricula = matricula;
        this.apellidos_nombres = apellidos_nombres;
        this.especialidad = especialidad;
        this.facultad = facultad;
        this.creditos_matriculados = creditos_matriculados;
        this.creditos_aprobados = creditos_aprobados;
        this.ciclos_estudiados = ciclos_estudiados;
        this.codigo_facultad = codigo_facultad;
        this.nivel = nivel;
        this.es_3cio_super = es_3cio_super;
    }

    public AptoPreBean(String matricula, String apellidos_nombres, String especialidad, String facultad, Long creditos_matriculados, Long creditos_aprobados, Long ciclos_estudiados, String codigo_facultad, Long nivel) {
        this.matricula = matricula;
        this.apellidos_nombres = apellidos_nombres;
        this.especialidad = especialidad;
        this.facultad = facultad;
        this.creditos_matriculados = creditos_matriculados;
        this.creditos_aprobados = creditos_aprobados;
        this.ciclos_estudiados = ciclos_estudiados;
        this.codigo_facultad = codigo_facultad;
        this.nivel = nivel;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getApellidos_nombres() {
        return apellidos_nombres;
    }

    public void setApellidos_nombres(String apellidos_nombres) {
        this.apellidos_nombres = apellidos_nombres;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public Long getCreditos_matriculados() {
        return creditos_matriculados;
    }

    public void setCreditos_matriculados(Long creditos_matriculados) {
        this.creditos_matriculados = creditos_matriculados;
    }

    public Long getCreditos_aprobados() {
        return creditos_aprobados;
    }

    public void setCreditos_aprobados(Long creditos_aprobados) {
        this.creditos_aprobados = creditos_aprobados;
    }

    public Long getCiclos_estudiados() {
        return ciclos_estudiados;
    }

    public void setCiclos_estudiados(Long ciclos_estudiados) {
        this.ciclos_estudiados = ciclos_estudiados;
    }

    public String getCodigo_facultad() {
        return codigo_facultad;
    }

    public void setCodigo_facultad(String codigo_facultad) {
        this.codigo_facultad = codigo_facultad;
    }

    public Long getNivel() {
        return nivel;
    }

    public void setNivel(Long nivel) {
        this.nivel = nivel;
    }

    public String getEs_3cio_super() {
        return es_3cio_super;
    }

    public void setEs_3cio_super(String es_3cio_super) {
        this.es_3cio_super = es_3cio_super;
    }

}
