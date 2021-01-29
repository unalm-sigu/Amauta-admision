package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

@Repository
public class TramiteBachillerDAOH extends AbstractEasyDAO<TramiteBachiller> implements TramiteBachillerDAO {

    public TramiteBachillerDAOH() {
        super();
        setClazz(TramiteBachiller.class);
    }

    @Override
    public TramiteBachiller findByTramite(Tramite tramite) {
        Octavia sql = new Octavia();
        sql.from(TramiteBachiller.class)
                .join("tramite tr", "tr.alumno al", "al.persona")
                .left("al.consejero con", "con.colaborador cola", "cola.persona")
                .filter("tr.id", tramite);

        return find(sql);

    }

    @Override
    public List<TramiteBachiller> allByTramites(List<Tramite> tramites) {
        Octavia sql = new Octavia();
        sql.from(TramiteBachiller.class)
                .join("tramite tr", "tr.alumno al", "al.persona")
                .in("tr.id", tramites);

        return all(sql);
    }

    @Override
    public TramiteBachiller findByAlumnoAct(Alumno alumno) {
        Octavia sql = new Octavia();
        sql.from(TramiteBachiller.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona", "al.carrera car")
                .join("car.facultad")
                .filter("tb.estado", SOL)
                .filter("al.id", alumno);

        return find(sql);
    }

    @Override
    public TramiteBachiller findByAlumnoACEP(Alumno alumno) {
        Octavia sql = new Octavia();
        sql.from(TramiteBachiller.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona")
                .join("resolucion ")
                .filter("tb.estado", TramiteEstadoEnum.ACEP)
                .filter("al.id", alumno);

        return find(sql);
    }

    @Override
    public List<TramiteBachiller> allByResolucion(Resolucion resolucionDB) {
        Octavia sql = new Octavia();
        sql.from(TramiteBachiller.class)
                .join("resolucion res")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .join("per.tipoDocumento", "tr.cicloAcademico")
                .filter("res.id", resolucionDB);

        return all(sql);
    }

    @Override
    public List<TramiteBachiller> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TramiteBachiller.class, "tb")
                .join("tramite tr", "tr.cicloAcademico ca")
                .join("tr.compania", "tr.persona per", "tr.alumno al", "tr.tipoTramite tt")
                .left("al.carrera car", "car.facultad ", "al.planCurricular")
                .searchFields("al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .filter("ca.id", cicloAcademico);

        return all(sql);
    }

}
