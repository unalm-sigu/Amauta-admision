package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.EPG_TRAM_GRADO_SOL;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL_ACEP;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL_REI;
import pe.edu.lamolina.model.tramite.CambioPlanCurricular;
import pe.edu.lamolina.amauta.dao.tramite.CambioPlanCurricularDAO;

@Repository
public class CambioPlanCurricularDAOH extends AbstractEasyDAO<CambioPlanCurricular> implements CambioPlanCurricularDAO {

    public CambioPlanCurricularDAOH() {
        super();
        setClazz(CambioPlanCurricular.class);
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
    public List<CambioPlanCurricular> allByTramite(List<Tramite> tramite) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloAcademico cr")
                .in("tra.id", tramite);

        return all(sql);
    }

    @Override
    public List<CambioPlanCurricular> allByDyna(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite tra", "resolucion res", "facultad fac", "estadoTramite et", "cicloAcademico cr")
                .join("tra.persona traPer", "tra.tipoTramite")
                .filter("et.codigo", "!=", SOL_ACEP);
        return this.all(sql);
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
    public List<CambioPlanCurricular> allAceptadasByAlumnoSinCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("alumno alu", "estadoTramite et", "cicloAcademico cr")
                .left("tramite tra", "facultad fac", "resolucion res")
                .filter("cr.codigo", "<", ciclo.getCodigo())
                .filter("alu.id", alumno)
                .beginBlock()
                .__().in("et.codigo", Arrays.asList(SOL_ACEP, EPG_TRAM_GRADO_SOL))
                .__().filter("rei.aceptado", 1)
                .endBlock();
        return all(sql);
    }

    @Override
    public List<CambioPlanCurricular> allAceptadasPendientesByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("alumno alu", "estadoTramite et", "cicloAcademico cr")
                .left("tramite tra", "facultad fac", "resolucion res")
                .filter("cr.id", ciclo)
                .filter("alu.id", alumno)
                .beginBlock()
                .__().in("et.codigo", Arrays.asList(SOL_ACEP, SOL_REI, EPG_TRAM_GRADO_SOL))
                .__().filter("rei.aceptado", 1)
                .endBlock();
        return all(sql);
    }

    @Override
    public List<CambioPlanCurricular> allAceptadosByAlumnosSinCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("alumno alu", "estadoTramite et", "cicloAcademico cr")
                .left("tramite tra", "facultad fac", "resolucion res")
                .filter("cr.codigo", "<", ciclo.getCodigo())
                .in("alu.id", alumnos)
                .beginBlock()
                .__().in("et.codigo", Arrays.asList(SOL_ACEP, EPG_TRAM_GRADO_SOL))
                .__().filter("rei.aceptado", 1)
                .endBlock();
        return all(sql);
    }

    @Override
    public List<CambioPlanCurricular> allAceptadasPendientesByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("alumno alu", "estadoTramite et", "cicloAcademico cr")
                .left("tramite tra", "facultad fac", "resolucion res")
                .filter("cr.id", ciclo)
                .in("alu.id", alumnos)
                .beginBlock()
                .__().in("et.codigo", Arrays.asList(SOL_ACEP, SOL_REI, EPG_TRAM_GRADO_SOL))
                .__().filter("rei.aceptado", 1)
                .endBlock();
        return all(sql);
    }

    @Override
    public CambioPlanCurricular findByTramiteEstadoTram(Tramite tramite, TramiteEstadoEnum estadoTramiteEnum) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloAcademico cr")
                .filter("tra.id", tramite)
                .filter("et.id", estadoTramiteEnum.getId());

        return (CambioPlanCurricular) sql.find(getCurrentSession());
    }

    @Override
    public void updateEstado(CambioPlanCurricular readmision) {
        Octavia octavia = Octavia.update(CambioPlanCurricular.class);
        octavia.set(readmision, "estadoTramite");
        this.update(octavia);
    }

    @Override
    public void updateAceptado(CambioPlanCurricular readmision) {
        Octavia octavia = Octavia.update(CambioPlanCurricular.class);
        octavia.set(readmision, "aceptado");
        octavia.set(readmision, "resolucion.id");
        this.update(octavia);
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
    public List<CambioPlanCurricular> allByCicloReincorporacion(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite", "cicloAcademico cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio")
                .filter("cr.id", ciclo);
        return all(sql);
    }

    @Override
    public List<CambioPlanCurricular> allByTramitesCondicional(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloAcademico cr")
                .join("tra.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("esCondicional", 1);

        return all(sql);
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
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

    @Override
    public List<CambioPlanCurricular> allPendientesByCicloReincorporacion() {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite tr", "cicloAcademico cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio me")
                .filter("me.codigo", PRE)
                .filter("rei.aceptado", 0);
        return all(sql);
    }

    @Override
    public List<CambioPlanCurricular> allByCicloReincorporacionByEstado(CicloAcademico ciclo, TramiteEstadoEnum tramiteEstadoEnum) {
        Octavia sql = Octavia.query()
                .from(CambioPlanCurricular.class, "rei")
                .join("tramite", "cicloAcademico cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio", "estadoTramite estra")
                .filter("cr.id", ciclo)
                .filter("estra.codigo", tramiteEstadoEnum);
        return all(sql);
    }

    @Override
    public void updateColumns(CambioPlanCurricular readmision, String... columns){
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

}
