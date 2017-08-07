package pe.edu.lamolina.pivot.model.academico;

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
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.model.tramite.RetiroCurso;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCursoEnum;

@Entity
@Table(name = "aca_curso")
public class Curso implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "codigo_anterior1")
    private String codigoAnterior1;

    @Column(name = "codigo_anterior2")
    private String codigoAnterior2;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "horas_teoria")
    private Integer horasTeoria;

    @Column(name = "horas_practica")
    private Integer horasPractica;

    @Column(name = "creditos")
    private Integer creditos;

    @Column(name = "creditos_variables")
    private Integer creditosVariables;

    @Column(name = "tipo_curso")
    private String tipoCurso;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "fecha_plan_calificacion")
    private Date fechaPlanCalificacion;

    @Column(name = "user_plan_calificacion")
    private Long userPlanCalificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento_academico")
    private DepartamentoAcademico departamentoAcademico;

    @ManyToOne(fetch = FetchType.LAZY) //nivelacion
    @JoinColumn(name = "id_plan_calificacion")
    private PlanCalificacion planCalificacion;

    @ManyToOne(fetch = FetchType.LAZY) //regular
    @JoinColumn(name = "id_plan_calificacion_regular")
    private PlanCalificacion planCalificacionRegular;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coordinador")
    private Docente coordinador;

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<AlumnoCicloCurso> alumnoCicloCurso;

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<CursoAdicionalCurricula> cursoAdicionalCurricula;

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<CursoCurricula> cursoCurricula;

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<CursoOpcionalCurricula> cursoOpcionalCurricula;

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<FormatoCurso> formatoCurso;

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<MatriculaCurso> matriculaCurso;

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<NombreCurso> nombreCurso;
    /*
    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<Seccion> seccion;
     */
    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<RetiroCurso> retiroCurso;

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<PlanCalificacionCurso> planesCalificacionCursos;

    public Curso() {
    }

    public Curso(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public DepartamentoAcademico getDepartamentoAcademico() {
        return departamentoAcademico;
    }

    public void setDepartamentoAcademico(DepartamentoAcademico departamentoAcademico) {
        this.departamentoAcademico = departamentoAcademico;
    }

    public PlanCalificacion getPlanCalificacion() {
        return planCalificacion;
    }

    public void setPlanCalificacion(PlanCalificacion planCalificacion) {
        this.planCalificacion = planCalificacion;
    }

    public Docente getCoordinador() {
        return coordinador;
    }

    public void setCoordinador(Docente coordinador) {
        this.coordinador = coordinador;
    }

    public String getCodigoAnterior1() {
        return codigoAnterior1;
    }

    public void setCodigoAnterior1(String codigoAnterior1) {
        this.codigoAnterior1 = codigoAnterior1;
    }

    public String getCodigoAnterior2() {
        return codigoAnterior2;
    }

    public void setCodigoAnterior2(String codigoAnterior2) {
        this.codigoAnterior2 = codigoAnterior2;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCreditos() {
        if (creditos == null) {
            return 0;
        }
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public String getTipoCurso() {
        return tipoCurso;
    }

    public void setTipoCurso(String tipoCurso) {
        this.tipoCurso = tipoCurso;
    }

    public TipoCursoEnum getTipoCursoEnum() {
        return TipoCursoEnum.valueOf(this.getTipoCurso());
    }

    public List<AlumnoCicloCurso> getAlumnoCicloCurso() {
        return alumnoCicloCurso;
    }

    public void setAlumnoCicloCurso(List<AlumnoCicloCurso> alumnoCicloCurso) {
        this.alumnoCicloCurso = alumnoCicloCurso;
    }

    public List<CursoAdicionalCurricula> getCursoAdicionalCurricula() {
        return cursoAdicionalCurricula;
    }

    public void setCursoAdicionalCurricula(List<CursoAdicionalCurricula> cursoAdicionalCurricula) {
        this.cursoAdicionalCurricula = cursoAdicionalCurricula;
    }

    public List<CursoCurricula> getCursoCurricula() {
        return cursoCurricula;
    }

    public void setCursoCurricula(List<CursoCurricula> cursoCurricula) {
        this.cursoCurricula = cursoCurricula;
    }

    public List<CursoOpcionalCurricula> getCursoOpcionalCurricula() {
        return cursoOpcionalCurricula;
    }

    public void setCursoOpcionalCurricula(List<CursoOpcionalCurricula> cursoOpcionalCurricula) {
        this.cursoOpcionalCurricula = cursoOpcionalCurricula;
    }

    public List<FormatoCurso> getFormatoCurso() {
        return formatoCurso;
    }

    public void setFormatoCurso(List<FormatoCurso> formatoCurso) {
        this.formatoCurso = formatoCurso;
    }

    public List<MatriculaCurso> getMatriculaCurso() {
        return matriculaCurso;
    }

    public void setMatriculaCurso(List<MatriculaCurso> matriculaCurso) {
        this.matriculaCurso = matriculaCurso;
    }

    public List<NombreCurso> getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(List<NombreCurso> nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public List<RetiroCurso> getRetiroCurso() {
        return retiroCurso;
    }

    public void setRetiroCurso(List<RetiroCurso> retiroCurso) {
        this.retiroCurso = retiroCurso;
    }

    public Date getFechaPlanCalificacion() {
        return fechaPlanCalificacion;
    }

    public void setFechaPlanCalificacion(Date fechaPlanCalificacion) {
        this.fechaPlanCalificacion = fechaPlanCalificacion;
    }

    public Long getUserPlanCalificacion() {
        return userPlanCalificacion;
    }

    public void setUserPlanCalificacion(Long userPlanCalificacion) {
        this.userPlanCalificacion = userPlanCalificacion;
    }

    public Integer getHorasTeoria() {
        return horasTeoria;
    }

    public void setHorasTeoria(Integer horasTeoria) {
        this.horasTeoria = horasTeoria;
    }

    public Integer getHorasPractica() {
        return horasPractica;
    }

    public void setHorasPractica(Integer horasPractica) {
        this.horasPractica = horasPractica;
    }

    public PlanCalificacion getPlanCalificacionRegular() {
        return planCalificacionRegular;
    }

    public void setPlanCalificacionRegular(PlanCalificacion planCalificacionRegular) {
        this.planCalificacionRegular = planCalificacionRegular;
    }

    public List<PlanCalificacionCurso> getPlanesCalificacionCursos() {
        return planesCalificacionCursos;
    }

    public void setPlanesCalificacionCursos(List<PlanCalificacionCurso> planesCalificacionCursos) {
        this.planesCalificacionCursos = planesCalificacionCursos;
    }

    public Integer getCreditosVariables() {
        return creditosVariables;
    }

    public void setCreditosVariables(Integer creditosVariables) {
        this.creditosVariables = creditosVariables;
    }

    public String getTpc() {
        StringBuilder tpc = new StringBuilder();
        if (horasTeoria != null) {
            tpc.append(horasTeoria).append("-");
        }
        if (horasPractica != null) {
            tpc.append(horasPractica).append("-");
        }
        if (creditos != null) {
            tpc.append(creditos);
        }
        return tpc.toString();
    }

    public boolean isEstadoActive() {
        if (this.getEstado().equals(EstadoEnum.ACT.name())) {
            return true;
        }
        return false;
    }

    public boolean isTieneCreditosVariables() {
        if (this.getCreditosVariables() != null) {
            return true;
        }
        return false;
    }

    public boolean isPostgrado() {
        String caracter = this.getCodigo().charAt(2) + "";
        if (Integer.parseInt(caracter) >= 7) {
            return true;
        }
        return false;
    }

}
