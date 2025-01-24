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
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.amauta.dao.tramite.ReincorporacionDAO;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.EPG_TRAM_GRADO_SOL;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL_ACEP;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL_REI;

@Repository
public class ReincorporacionDAOH extends AbstractEasyDAO<Reincorporacion> implements ReincorporacionDAO {

    public ReincorporacionDAOH() {
        super();
        setClazz(Reincorporacion.class);
    }

    @Override
    public List<Reincorporacion> allByTramite(Tramite tramite) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .filter("tra.id", tramite);

        return all(sql);
    }

    @Override
    public List<Reincorporacion> allByTramite(List<Tramite> tramite) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .in("tra.id", tramite);

        return all(sql);
    }

    @Override
    public List<Reincorporacion> allByDyna(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "resolucion res", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .join("tra.persona traPer", "tra.tipoTramite")
                .filter("et.codigo", "!=", SOL_ACEP);
        return this.all(sql);
    }

    @Override
    public List<Reincorporacion> allByResolucion(Resolucion resolucion) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .left("tramite tra", "alumno al", "al.persona per", "per.tipoDocumento")
                .join("resolucion res", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .filter("res.id", resolucion);

        return all(sql);
    }

    @Override
    public List<Reincorporacion> allAceptadasByAlumnoSinCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("alumno alu", "estadoTramite et", "cicloReincorporacion cr")
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
    public List<Reincorporacion> allAceptadasPendientesByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("alumno alu", "estadoTramite et", "cicloReincorporacion cr")
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
    public List<Reincorporacion> allAceptadosByAlumnosSinCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("alumno alu", "estadoTramite et", "cicloReincorporacion cr")
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
    public List<Reincorporacion> allAceptadasPendientesByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("alumno alu", "estadoTramite et", "cicloReincorporacion cr")
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
    public Reincorporacion findByTramiteEstadoTram(Tramite tramite, TramiteEstadoEnum estadoTramiteEnum) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .filter("tra.id", tramite)
                .filter("et.id", estadoTramiteEnum.getId());

        return (Reincorporacion) sql.find(getCurrentSession());
    }

    @Override
    public void updateEstado(Reincorporacion reincorporacion) {
        Octavia octavia = Octavia.update(Reincorporacion.class);
        octavia.set(reincorporacion, "estadoTramite");
        this.update(octavia);
    }

    @Override
    public void updateAceptado(Reincorporacion reincorporacion) {
        Octavia octavia = Octavia.update(Reincorporacion.class);
        octavia.set(reincorporacion, "aceptado");
        octavia.set(reincorporacion, "resolucion.id");
        this.update(octavia);
    }

    @Override
    public Reincorporacion findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("cicloReincorporacion cr", "tramite tr", "tr.alumno al")
                .filter("al.id", alumno)
                .filter("cr.id", ciclo);
        return find(sql);
    }

    @Override
    public List<Reincorporacion> allByCicloReincorporacion(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite", "cicloReincorporacion cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio")
                .filter("cr.id", ciclo);
        return all(sql);
    }

    @Override
    public List<Reincorporacion> allByTramitesCondicional(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .join("tra.cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("esCondicional", 1);

        return all(sql);
    }

    @Override
    public List<Reincorporacion> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Reincorporacion.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReincorporacion cr")
                .join("tra.cicloAcademico ca", "tra.alumno al", "al.persona per")
                .left("resolucion","al.cicloActivo caa")
                .searchFields("cr.descripcion", "et.nombre", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", cicloAcademico)
                .orderBy("tra.serie desc","tra.numero desc");

        return all(sql);
    }

    @Override
    public List<Reincorporacion> allPendientesByCicloReincorporacion() {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite tr", "cicloReincorporacion cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio me")
                .filter("me.codigo", PRE)
                .filter("rei.aceptado", 0);
        return all(sql);
    }

    @Override
    public List<Reincorporacion> allByCicloReincorporacionByEstado(CicloAcademico ciclo, TramiteEstadoEnum tramiteEstadoEnum) {
        Octavia sql = Octavia.query()
                .from(Reincorporacion.class, "rei")
                .join("tramite", "cicloReincorporacion cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio","estadoTramite estra")
                .filter("cr.id", ciclo)
                .filter("estra.codigo", tramiteEstadoEnum);
        return all(sql);
    }

}
