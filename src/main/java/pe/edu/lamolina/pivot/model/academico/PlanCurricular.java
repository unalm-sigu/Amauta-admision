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
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Entity
@Table(name = "aca_plan_curricular")
public class PlanCurricular implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "estado")
    private String estado;

    @Column(name = "ciclos")
    private Integer ciclos;

    @Column(name = "fecha_aprobado")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaAprobado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo_inicio_vigencia")
    private CicloAcademico cicloInicioVigencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orientacion_carrera")
    private OrientacionCarrera orientacionCarrera;

    @OneToMany(mappedBy = "planCurricular", fetch = FetchType.LAZY)
    private List<CursoAdicionalCurricula> cursoAdicionalCurricula;

    @OneToMany(mappedBy = "planCurricular", fetch = FetchType.LAZY)
    private List<CursoCurricula> cursoCurricula;

    @OneToMany(mappedBy = "planCurricular", fetch = FetchType.LAZY)
    private List<CursoOpcionalCurricula> cursoOpcionalCurricula;

    @OneToMany(mappedBy = "planCurricular", fetch = FetchType.LAZY)
    private List<ResumenPlanCurricular> resumenPlanCurricular;

    @Transient
    private Integer cantidadCursosCurricula;

    @Transient
    private Integer cantidadCursosOpcionales;

    @Transient
    private Integer cantidadCursosAdicionales;

    public PlanCurricular() {
    }

    public void init() {
        this.setCicloInicioVigencia(new CicloAcademico());
        this.setCarrera(new Carrera());
    }

    public Integer getCursosByCiclo(int numeroCiclo) {
        if (cursoCurricula == null) {
            return 0;
        }
        int cant = 0;
        for (CursoCurricula cursoPlan : cursoCurricula) {
            if (cursoPlan.getNumeroCiclo() == numeroCiclo) {
                cant++;
            }
        }
        return cant;
    }

    public PlanCurricular(Object id) {
        this.id = TypesUtil.getLong(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public OrientacionCarrera getOrientacionCarrera() {
        return orientacionCarrera;
    }

    public void setOrientacionCarrera(OrientacionCarrera orientacionCarrera) {
        this.orientacionCarrera = orientacionCarrera;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setEstadoEnum(EstadoEnum estadoEnum) {
        this.estado = estadoEnum.name();
    }

    public Date getFechaAprobado() {
        return fechaAprobado;
    }

    public void setFechaAprobado(Date fechaAprobado) {
        this.fechaAprobado = fechaAprobado;
    }

    public CicloAcademico getCicloInicioVigencia() {
        return cicloInicioVigencia;
    }

    public void setCicloInicioVigencia(CicloAcademico cicloInicioVigencia) {
        this.cicloInicioVigencia = cicloInicioVigencia;
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

    public List<ResumenPlanCurricular> getResumenPlanCurricular() {
        return resumenPlanCurricular;
    }

    public void setResumenPlanCurricular(List<ResumenPlanCurricular> resumenPlanCurricular) {
        this.resumenPlanCurricular = resumenPlanCurricular;
    }

    public EstadoEnum getEstadoEnum() {
        if (StringUtils.isBlank(this.getEstado())) {
            return null;
        }
        return EstadoEnum.valueOf(this.getEstado());
    }

    public Integer getCantidadCursosCurricula() {
        return cantidadCursosCurricula;
    }

    public void setCantidadCursosCurricula(Integer cantidadCursosCurricula) {
        this.cantidadCursosCurricula = cantidadCursosCurricula;
    }

    public Integer getCantidadCursosOpcionales() {
        return cantidadCursosOpcionales;
    }

    public void setCantidadCursosOpcionales(Integer cantidadCursosOpcionales) {
        this.cantidadCursosOpcionales = cantidadCursosOpcionales;
    }

    public Integer getCantidadCursosAdicionales() {
        return cantidadCursosAdicionales;
    }

    public void setCantidadCursosAdicionales(Integer cantidadCursosAdicionales) {
        this.cantidadCursosAdicionales = cantidadCursosAdicionales;
    }

    public Integer getCiclos() {
        return ciclos;
    }

    public void setCiclos(Integer ciclos) {
        this.ciclos = ciclos;
    }

}
