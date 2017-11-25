package pe.edu.lamolina.pivot.model.horario;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

@Entity
@Table(name = "hor_horario_cachimbos")
public class HorarioCachimbos implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "cursos")
    private Integer cursos;

    @Column(name = "capacidad")
    private Integer capacidad;

    @Column(name = "suscritos")
    private Integer suscritos;

    @Column(name = "matriculados")
    private Integer matriculados;

    @Column(name = "id_user_creacion")
    private Long idUserCreacion;

    @Column(name = "fecha_creacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_academico")
    private CicloAcademico cicloAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @OneToMany(mappedBy = "horarioCachimbos", fetch = FetchType.LAZY)
    private List<AlumnoHorario> alumnoHorario;

    @OneToMany(mappedBy = "horarioCachimbos", fetch = FetchType.LAZY)
    private List<SeccionHorarioCachimbos> seccionHorarioCachimbos;

    public HorarioCachimbos() {
    }

    public HorarioCachimbos(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CicloAcademico getCicloAcademico() {
        return cicloAcademico;
    }

    public void setCicloAcademico(CicloAcademico cicloAcademico) {
        this.cicloAcademico = cicloAcademico;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCursos() {
        return cursos;
    }

    public void setCursos(Integer cursos) {
        this.cursos = cursos;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Integer getSuscritos() {
        return suscritos;
    }

    public void setSuscritos(Integer suscritos) {
        this.suscritos = suscritos;
    }

    public Integer getMatriculados() {
        return matriculados;
    }

    public void setMatriculados(Integer matriculados) {
        this.matriculados = matriculados;
    }

    public Long getIdUserCreacion() {
        return idUserCreacion;
    }

    public void setIdUserCreacion(Long idUserCreacion) {
        this.idUserCreacion = idUserCreacion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<AlumnoHorario> getAlumnoHorario() {
        return alumnoHorario;
    }

    public void setAlumnoHorario(List<AlumnoHorario> alumnoHorario) {
        this.alumnoHorario = alumnoHorario;
    }

    public List<SeccionHorarioCachimbos> getSeccionHorarioCachimbos() {
        return seccionHorarioCachimbos;
    }

    public void setSeccionHorarioCachimbos(List<SeccionHorarioCachimbos> seccionHorarioCachimbos) {
        this.seccionHorarioCachimbos = seccionHorarioCachimbos;
    }

}

