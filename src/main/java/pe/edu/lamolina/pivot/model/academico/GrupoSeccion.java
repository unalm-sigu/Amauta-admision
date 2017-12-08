package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;

@Entity
@Table(name = "aca_grupo_seccion")
public class GrupoSeccion implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    String codigo;

    @Column(name = "orden")
    private Integer orden;

    @NotNull
    @Column(name = "version")
    private String version;

    @NotNull
    @Column(name = "estado_plan")
    private String estadoPlan;

    @NotNull
    @Column(name = "estado_grupo")
    private String estadoGrupo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_cierre_acta")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaCierreActa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo")
    private CicloAcademico cicloAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anexo_boletin")
    private AnexoBoletin anexoBoletin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso")
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan_calificacion")
    private PlanCalificacion planCalificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_cierre_acta")
    private Usuario usuarioCierraActa;

    @OneToMany(mappedBy = "grupoSeccion", fetch = FetchType.LAZY)
    private List<EvaluacionSeccion> evaluacionSecciones;

    @OneToMany(mappedBy = "grupoSeccion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Seccion> secciones;

    @Transient
    private String codigoCurso;

    @Transient
    private Docente docenteResponsable;

    public GrupoSeccion() {
    }

    public GrupoSeccion(String codigo, String codigoCurso) {
        this.codigo = codigo;
        this.codigoCurso = codigoCurso;
    }

    public GrupoSeccion(Object id) {
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

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<Seccion> secciones) {
        this.secciones = secciones;
    }

    public String getEstadoPlan() {
        return estadoPlan;
    }

    public void setEstadoPlan(String estadoPlan) {
        this.estadoPlan = estadoPlan;
    }

    public EstadoPlanCalificaEnum getEstadoPlanEnum() {
        if (StringUtils.isEmpty(estadoPlan)) {
            return null;
        }
        return EstadoPlanCalificaEnum.valueOf(estadoPlan);
    }

    public void setEstadoPlanEnum(EstadoPlanCalificaEnum estadoPlanCalificaEnum) {
        this.estadoPlan = estadoPlanCalificaEnum.name();
    }

    public PlanCalificacion getPlanCalificacion() {
        return planCalificacion;
    }

    public void setPlanCalificacion(PlanCalificacion planCalificacion) {
        this.planCalificacion = planCalificacion;
    }

    public boolean isEstadoSolicitado() {
        if (EstadoPlanCalificaEnum.SOL.name().equals(estadoPlan)) {
            return true;
        }
        return false;
    }

    public boolean isEstadoExpandido() {
        if (EstadoPlanCalificaEnum.EXP.name().equals(estadoPlan)) {
            return true;
        }
        return false;
    }

    public boolean isEstadoExpandir() {
        if (EstadoPlanCalificaEnum.EXPR.name().equals(estadoPlan)) {
            return true;
        }
        return false;
    }

    public boolean isEstadoReenviado() {
        if (EstadoPlanCalificaEnum.REE.name().equals(estadoPlan)) {
            return true;
        }
        return false;
    }

    public boolean isEstadoCreado() {
        if (EstadoPlanCalificaEnum.CRE.name().equals(estadoPlan)) {
            return true;
        }
        return false;
    }

    public boolean isEstadoPropuesto() {
        if (EstadoPlanCalificaEnum.PRO.name().equals(estadoPlan)) {
            return true;
        }
        return false;
    }

    public boolean isEstadoActivado() {
        if (EstadoPlanCalificaEnum.ACT.name().equals(estadoPlan)) {
            return true;
        }
        return false;
    }

    public boolean isEstadoObservado() {
        if (EstadoPlanCalificaEnum.OBS.name().equals(estadoPlan)) {
            return true;
        }
        return false;
    }

    public boolean isEstadoAceptado() {
        if (EstadoPlanCalificaEnum.ACEP.name().equals(estadoPlan)) {
            return true;
        }
        return false;
    }

    public List<EvaluacionSeccion> getEvaluacionSecciones() {
        return evaluacionSecciones;
    }

    public void setEvaluacionSecciones(List<EvaluacionSeccion> evaluacionSecciones) {
        this.evaluacionSecciones = evaluacionSecciones;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEstadoGrupo() {
        return estadoGrupo;
    }

    public void setEstadoGrupo(String estadoGrupo) {
        this.estadoGrupo = estadoGrupo;
    }

    public EstadoGrupoSeccionEnum getEstadoGrupoEnum() {

        return EstadoGrupoSeccionEnum.valueOf(estadoGrupo);
    }

    public void setEstadoGrupoEnum(EstadoGrupoSeccionEnum estadoGrupoEnum) {
        this.estadoGrupo = estadoGrupoEnum.name();
    }

    public Usuario getUsuarioCierraActa() {
        return usuarioCierraActa;
    }

    public void setUsuarioCierraActa(Usuario usuarioCierraActa) {
        this.usuarioCierraActa = usuarioCierraActa;
    }

    public Date getFechaCierreActa() {
        return fechaCierreActa;
    }

    public void setFechaCierreActa(Date fechaCierreActa) {
        this.fechaCierreActa = fechaCierreActa;
    }

    public boolean isEstadoGrupoAbierto() {
        if (EstadoGrupoSeccionEnum.ABI.equals(this.getEstadoGrupoEnum())) {
            return true;
        }
        return false;
    }

    public boolean isEstadoGrupoCerrado() {
        if (EstadoGrupoSeccionEnum.CER.equals(this.getEstadoGrupoEnum())) {
            return true;
        }
        return false;
    }

    public boolean isEstadoGrupoReabierto() {
        if (EstadoGrupoSeccionEnum.RAB.equals(this.getEstadoGrupoEnum())) {
            return true;
        }
        return false;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Docente getDocenteResponsable() {
        return docenteResponsable;
    }

    public void setDocenteResponsable(Docente docenteResponsable) {
        this.docenteResponsable = docenteResponsable;
    }

    public AnexoBoletin getAnexoBoletin() {
        return anexoBoletin;
    }

    public void setAnexoBoletin(AnexoBoletin anexoBoletin) {
        this.anexoBoletin = anexoBoletin;
    }

}
