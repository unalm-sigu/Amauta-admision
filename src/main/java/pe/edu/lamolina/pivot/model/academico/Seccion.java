package pe.edu.lamolina.pivot.model.academico;

import java.io.Serializable;
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
import javax.persistence.Transient;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.model.general.Aula;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.model.tramite.RetiroCurso;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;

@Entity
@Table(name = "aca_seccion")
public class Seccion implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "codigo2")
    private String codigo2;

    @Column(name = "tipo_seccion")
    private String tipoSeccion;

    @Column(name = "es_principal")
    private Integer esPrincipal;

    @Column(name = "horas_teoria")
    private Integer horasTeoria;

    @Column(name = "horas_practica")
    private Integer horasPractica;

    @Column(name = "horas_semanales")
    private Integer horasSemanales;

    @Column(name = "vacantes")
    private Integer vacantes;

    @Column(name = "matriculados")
    private Integer matriculados;

    @Column(name = "retirados")
    private Integer retirados;

    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seccion_superior")
    private Seccion seccionSuperior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aula")
    private Aula aula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grupo_horas")
    private GrupoHoras grupoHoras;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grupo_seccion")
    private GrupoSeccion grupoSeccion;

    @OneToMany(mappedBy = "seccion", fetch = FetchType.LAZY)
    private List<DocenteSeccion> docenteSeccion;

    @OneToMany(mappedBy = "seccionResponsable", fetch = FetchType.LAZY)
    private List<Evaluacion> evaluacion;

    @OneToMany(mappedBy = "seccion", fetch = FetchType.LAZY)
    private List<LoggerMatricula> loggerMatricula;

    @OneToMany(mappedBy = "seccion", fetch = FetchType.LAZY)
    private List<MatriculaSeccion> matriculaSeccion;

    @OneToMany(mappedBy = "seccionSuperior", fetch = FetchType.LAZY)
    private List<Seccion> seccion;

    @OneToMany(mappedBy = "seccion", fetch = FetchType.LAZY)
    private List<HorarioSeccion> horarioSeccion;

    @OneToMany(mappedBy = "seccion", fetch = FetchType.LAZY)
    private List<RetiroCurso> retiroCurso;

    @Transient
    private String codigoGrupoHorario;
    @Transient
    private String codigoAula;
    @Transient
    private String codigoGrupoSeccion;
    @Transient
    private String codigoTipoSeccion;

    public Seccion() {
    }

    public Seccion(String codigo, String codigo2, String codigoGrupoHorario, String codigoAula, String codigoGrupoSeccion, String codigoTipoSeccion) {
        this.codigo = codigo;
        this.codigo2 = codigo2;
        this.codigoGrupoHorario = codigoGrupoHorario;
        this.codigoAula = codigoAula;
        this.codigoGrupoSeccion = codigoGrupoSeccion;
        this.codigoTipoSeccion = codigoTipoSeccion;
    }

    public Seccion(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Seccion getSeccionSuperior() {
        return seccionSuperior;
    }

    public void setSeccionSuperior(Seccion seccionSuperior) {
        this.seccionSuperior = seccionSuperior;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo2() {
        return codigo2;
    }

    public void setCodigo2(String codigo2) {
        this.codigo2 = codigo2;
    }

    public String getTipoSeccion() {
        return tipoSeccion;
    }

    public void setTipoSeccion(String tipoSeccion) {
        this.tipoSeccion = tipoSeccion;
    }

    public TipoSeccionEnum getTipoSeccionEnum() {
        return TipoSeccionEnum.valueOf(tipoSeccion);
    }

    public void setTipoSeccionEnum(TipoSeccionEnum tipoSeccionEnum) {
        this.tipoSeccion = tipoSeccionEnum.name();
    }

    public Integer getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Integer esPrincipal) {
        this.esPrincipal = esPrincipal;
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

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public GrupoHoras getGrupoHoras() {
        return grupoHoras;
    }

    public void setGrupoHoras(GrupoHoras grupoHoras) {
        this.grupoHoras = grupoHoras;
    }

    public List<DocenteSeccion> getDocenteSeccion() {
        return docenteSeccion;
    }

    public void setDocenteSeccion(List<DocenteSeccion> docenteSeccion) {
        this.docenteSeccion = docenteSeccion;
    }

    public List<Evaluacion> getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(List<Evaluacion> evaluacion) {
        this.evaluacion = evaluacion;
    }

    public List<LoggerMatricula> getLoggerMatricula() {
        return loggerMatricula;
    }

    public void setLoggerMatricula(List<LoggerMatricula> loggerMatricula) {
        this.loggerMatricula = loggerMatricula;
    }

    public List<MatriculaSeccion> getMatriculaSeccion() {
        return matriculaSeccion;
    }

    public void setMatriculaSeccion(List<MatriculaSeccion> matriculaSeccion) {
        this.matriculaSeccion = matriculaSeccion;
    }

    public List<Seccion> getSeccion() {
        return seccion;
    }

    public void setSeccion(List<Seccion> seccion) {
        this.seccion = seccion;
    }

    public List<HorarioSeccion> getHorarioSeccion() {
        return horarioSeccion;
    }

    public void setHorarioSeccion(List<HorarioSeccion> horarioSeccion) {
        this.horarioSeccion = horarioSeccion;
    }

    public List<RetiroCurso> getRetiroCurso() {
        return retiroCurso;
    }

    public void setRetiroCurso(List<RetiroCurso> retiroCurso) {
        this.retiroCurso = retiroCurso;
    }

    public GrupoSeccion getGrupoSeccion() {
        return grupoSeccion;
    }

    public void setGrupoSeccion(GrupoSeccion grupoSeccion) {
        this.grupoSeccion = grupoSeccion;
    }

    public Integer getVacantes() {
        return vacantes;
    }

    public void setVacantes(Integer vacantes) {
        this.vacantes = vacantes;
    }

    public Integer getMatriculados() {
        return matriculados;
    }

    public void setMatriculados(Integer matriculados) {
        this.matriculados = matriculados;
    }

    public Integer getRetirados() {
        return retirados;
    }

    public void setRetirados(Integer retirados) {
        this.retirados = retirados;
    }

    public Integer getHorasSemanales() {
        return horasSemanales;
    }

    public void setHorasSemanales(Integer horasSemanales) {
        this.horasSemanales = horasSemanales;
    }

    public boolean isTipoSeccionTCUR() {
        if (TipoSeccionEnum.TCUR.equals(this.getTipoSeccionEnum())) {
            return true;
        }
        return false;
    }

    public boolean isTipoSeccionTEO() {
        if (TipoSeccionEnum.TEO.equals(this.getTipoSeccionEnum())) {
            return true;
        }
        return false;
    }

    public boolean isTipoSeccionPRA() {
        if (TipoSeccionEnum.PRA.equals(this.getTipoSeccionEnum())) {
            return true;
        }
        return false;
    }

    public String getCodigoGrupoHorario() {
        return codigoGrupoHorario;
    }

    public void setCodigoGrupoHorario(String codigoGrupoHorario) {
        this.codigoGrupoHorario = codigoGrupoHorario;
    }

    public String getCodigoAula() {
        return codigoAula;
    }

    public void setCodigoAula(String codigoAula) {
        this.codigoAula = codigoAula;
    }

    public String getCodigoGrupoSeccion() {
        return codigoGrupoSeccion;
    }

    public void setCodigoGrupoSeccion(String codigoGrupoSeccion) {
        this.codigoGrupoSeccion = codigoGrupoSeccion;
    }

    public String getCodigoTipoSeccion() {
        return codigoTipoSeccion;
    }

    public void setCodigoTipoSeccion(String codigoTipoSeccion) {
        this.codigoTipoSeccion = codigoTipoSeccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
