package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.CambioPlanCurricular;
import pe.edu.lamolina.amauta.dao.tramite.CambioPlanCurricularDAO;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Tramite;

@Repository
public class CambioPlanCurricularDAOH extends AbstractEasyDAO<CambioPlanCurricular> implements CambioPlanCurricularDAO {

    public CambioPlanCurricularDAOH() {
        super();
        setClazz(CambioPlanCurricular.class);
    }

    @Override
    public List<CambioPlanCurricular> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico) {

        DynatableSql sql = new DynatableSql(filter)
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloAcademico cr")
                .join("tra.cicloAcademico ca", "tra.alumno al", "al.persona per")
                .left("resolucion")
                .searchFields("cr.descripcion", "et.nombre", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", cicloAcademico)
                .orderBy("rei.id desc");

        return all(sql);
    }

    @Override
    public List<CambioPlanCurricular> allByResolucion(Resolucion resolucion) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .left("tramite tra", "alumno al", "al.persona per", "per.tipoDocumento")
                .join("resolucion res", "facultad fac", "estadoTramite et", "cicloAcademico cr")
                .filter("res.id", resolucion);

        return all(sql);
    }

    @Override
    public CambioPlanCurricular findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("cicloAcademico cr", "tramite tr", "tr.alumno al")
                .filter("al.id", alumno)
                .filter("cr.id", ciclo);
        return find(sql);
    }

    @Override
    public List<CambioPlanCurricular> allByTramite(Tramite tramite) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloAcademico cr")
                .filter("tra.id", tramite);

        return all(sql);
    }

    @Override
    public List<CambioPlanCurricular> allPendientesByCicloAcademico() {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite tr", "cicloAcademico cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio me")
                .filter("me.codigo", PRE)
                .filter("rei.aceptado", 0);
        return all(sql);
    }

    @Override
    public void updateColumns(CambioPlanCurricular readmision, String... columns) {
        Octavia octavia = Octavia.update(CambioPlanCurricular.class);
        octavia.set(readmision, columns);
        this.update(octavia);
    }

    @Override
    public CambioPlanCurricular find(Long readmision) {

        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloAcademico cr")
                .filter("rei.id", readmision);

        return find(sql);
    }

    @Override
    public List<CambioPlanCurricular> allPendientes() {

        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("planCurricularOrigen pco", "planCurricularDestino pcd", "pco.cicloInicioVigencia", "pcd.cicloInicioVigencia")
                .join("tramite tr", "cicloAcademico cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio me")
                .filter("me.codigo", PRE)
                .filter("rei.aceptado", 0);
        return all(sql);

    }

    @Override
    public CambioPlanCurricular findByEstadoTramiteAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico, EstadoTramite estadoTramite) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("cicloAcademico cr", "tramite tr", "tr.alumno al", "estadoTramite es")
                .filter("al.id", alumno)
                .filter("cr.id", cicloAcademico)
                .filter("es.id", estadoTramite);
        return find(sql);
    }

    @Override
    public List<CambioPlanCurricular> allPendienteByEstado(EstadoTramite estadoTramite) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("planCurricularOrigen pco", "planCurricularDestino pcd", "pco.cicloInicioVigencia", "pcd.cicloInicioVigencia")
                .join("tramite tr", "cicloAcademico cr", "rei.alumno al", "al.persona" ,"estadoTramite es")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio me")
                .filter("es.id", estadoTramite)
                .filter("me.codigo", PRE)
                .filter("rei.aceptado", 0);
        return all(sql);
    }

}
