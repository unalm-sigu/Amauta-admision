package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReadmisionDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.Readmision;

@Repository
public class ReadmisionDAOH extends AbstractEasyDAO<Readmision> implements ReadmisionDAO {

    public ReadmisionDAOH() {
        super();
        setClazz(Readmision.class);
    }

    @Override
    public List<Readmision> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Readmision.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReadmitido cr")
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
    public List<Readmision> allByTramite(Tramite tramite) {
        Octavia sql = Octavia.query()
                .from(Readmision.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReadmitido cr")
                .filter("tra.id", tramite);

        return all(sql);
    }

    @Override
    public List<Readmision> allByResolucion(Resolucion resolucion) {
        Octavia sql = Octavia.query()
                .from(Readmision.class, "rei")
                .left("tramite tra", "alumno al", "al.persona per", "per.tipoDocumento")
                .join("resolucion res", "facultad fac", "estadoTramite et", "cicloReadmitido cr")
                .filter("res.id", resolucion);

        return all(sql);
    }

    @Override
    public Readmision findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Readmision.class, "rei")
                .join("cicloReadmitido cr", "tramite tr", "tr.alumno al")
                .filter("al.id", alumno)
                .filter("cr.id", ciclo);
        return find(sql);
    }

    @Override
    public List<Readmision> allPendientesByCicloReadmision() {
        Octavia sql = Octavia.query()
                .from(Readmision.class, "rei")
                .join("tramite tr", "cicloReadmitido cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio me")
                .filter("me.codigo", PRE)
                .filter("rei.aceptado", 0);
        return all(sql);
    }

    @Override
    public void updateColumns(Readmision readmision, String... columns) {
        Octavia octavia = Octavia.update(Readmision.class);
        octavia.set(readmision, columns);
        this.update(octavia);
    }

    @Override
    public Readmision find(Long readmision) {

        Octavia sql = Octavia.query()
                .from(Readmision.class, "rei")
                .join("tramite tra", "facultad fac", "estadoTramite et", "cicloReadmitido cr")
                .filter("rei.id", readmision);

        return find(sql);
    }

    @Override
    public List<Readmision> allPendientes() {
        Octavia sql = Octavia.query()
                .from(Readmision.class, "rei")
                .join("tramite tr", "cicloReadmitido cr", "rei.alumno al", "al.persona")
                .join("al.cicloActivoRegular ", "al.modalidadEstudio me")
                .filter("me.codigo", PRE)
                .filter("rei.aceptado", 0);
        return all(sql);
    }

    @Override
    public Readmision findByEstadoTramiteAlumnoCiclo(Alumno alumno, CicloAcademico cicloReadmitido, EstadoTramite estadoTramite) {
        Octavia sql = Octavia.query()
                .from(Readmision.class, "rei")
                .join("cicloReadmitido cr", "tramite tr", "tr.alumno al","estadoTramite es")
                .filter("al.id", alumno)
                .filter("es.id", estadoTramite)
                .filter("cr.id", cicloReadmitido);
        return find(sql);
    }

}
