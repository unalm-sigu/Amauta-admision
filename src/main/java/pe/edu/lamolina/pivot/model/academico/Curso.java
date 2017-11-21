package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.math.BigDecimal;
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
import pe.edu.lamolina.pivot.model.tramite.RetiroCurso;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoCreditoEnum;
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

    @Column(name = "horas_teoria_verano")
    private Integer horasTeoriaVerano;

    @Column(name = "horas_practica_verano")
    private Integer horasPracticaVerano;

    @Column(name = "creditos")
    private Integer creditos;

    @Column(name = "creditos_variables")
    private Integer creditosVariables;

    @Column(name = "tipo_curso")
    private String tipoCurso;

    @Column(name = "tipo_credito")
    private String tipoCredito;

    @Column(name = "motivo_anulacion")
    private String motivoAnulacion;

    @Column(name = "tipo_curricula")
    private String tipoCurricula;

    @Column(name = "nivel")
    private Integer nivel;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "fecha_plan_calificacion")
    private Date fechaPlanCalificacion;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "fecha_anulacion")
    private Date fechaAnulacion;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modalidad_estudio")
    private ModalidadEstudio modalidadEstudio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

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

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<RetiroCurso> retiroCurso;

    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<PlanCalificacionCurso> planesCalificacionCursos;

    public Curso() {
    }

    public Curso(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public String requiereEspecialidad() {
        if (id == null) {
            return "INDEFINIDO";
        }
        if (modalidadEstudio.getCodigoEnum() == ModalidadEstudioEnum.EPG) {
            return "SI";
        }
        return "NO";
    }

    public String getTpc() {
        if (horasTeoria == null || horasPractica == null || (creditos == null && creditosVariables == null)) {
            return null;
        }

        StringBuilder tpc = new StringBuilder();
        tpc.append(horasTeoria).append("-");
        tpc.append(horasPractica).append("-");

        if (creditos != null) {
            tpc.append(creditos);
        } else {
            tpc.append("[1 a ").append(creditosVariables).append("]");
        }

        return tpc.toString();
    }

    public boolean isEstadoActive() {
        return this.getEstadoEnum() == EstadoEnum.ACT;
    }

    public boolean isTieneCreditosVariables() {
        if (this.getCreditosVariables() != null) {
            return true;
        }
        return false;
    }

    //Se pone solo a o d, mas no la cantidad de creditos
    public boolean isCreditosZero() {
        if (this.getCreditosVariables() == null) {
            if (this.getCreditos().compareTo(BigDecimal.ZERO.intValue()) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isPostgrado() {
        return (modalidadEstudio.getCodigoEnum() == ModalidadEstudioEnum.EPG);
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

    public EstadoEnum getEstadoEnum() {
        if (estado == null) {
            return null;
        }
        return EstadoEnum.valueOf(codigo);
    }

    public void setEstado(EstadoEnum estado) {
        this.estado = estado.name();
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

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public Integer getHorasTeoriaVerano() {
        return horasTeoriaVerano;
    }

    public void setHorasTeoriaVerano(Integer horasTeoriaVerano) {
        this.horasTeoriaVerano = horasTeoriaVerano;
    }

    public Integer getHorasPracticaVerano() {
        return horasPracticaVerano;
    }

    public void setHorasPracticaVerano(Integer horasPracticaVerano) {
        this.horasPracticaVerano = horasPracticaVerano;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public ModalidadEstudio getModalidadEstudio() {
        return modalidadEstudio;
    }

    public void setModalidadEstudio(ModalidadEstudio modalidadEstudio) {
        this.modalidadEstudio = modalidadEstudio;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public String getTipoCurricula() {
        return tipoCurricula;
    }

    public void setTipoCurricula(String tipoCurricula) {
        this.tipoCurricula = tipoCurricula;
    }

    public String getTipoCredito() {
        return tipoCredito;
    }

    public TipoCreditoEnum getTipoCreditoEnum() {
        if (tipoCredito == null) {
            return null;
        }
        return TipoCreditoEnum.valueOf(tipoCredito);
    }

    public void setTipoCredito(TipoCreditoEnum tipoCredito) {
        this.tipoCredito = tipoCredito.name();
    }

}
