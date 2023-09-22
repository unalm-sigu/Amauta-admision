package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteRenunciaAlumnoDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TramiteRenunciaAlumno;

@Repository
public class TramiteRenunciaAlumnoDAOH extends AbstractEasyDAO<TramiteRenunciaAlumno> implements TramiteRenunciaAlumnoDAO {

    @Override
    public TramiteRenunciaAlumno findByAlumnoAct(Alumno alumno) {
        Octavia sql = new Octavia();
        sql.from(TramiteRenunciaAlumno.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona", "al.carrera car")
                .join("car.facultad")
                .filter("tb.estado", SOL)
                .filter("al.id", alumno);

        return find(sql);
    }

    @Override
    public List<TramiteRenunciaAlumno> allByRenunciaAlumno(Resolucion resolucion) {
        Octavia sql = new Octavia();
        sql.from(TramiteRenunciaAlumno.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .filter("tb.resolucion", resolucion);
        return all(sql);

    }

    @Override
    public List<TramiteRenunciaAlumno> allByRenunciaAlumnoEditar(Resolucion resolucion) {
        Octavia sql = new Octavia();
        sql.from(TramiteRenunciaAlumno.class)
                .join("resolucion res")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .filter("res.id", resolucion);
        return all(sql);

    }

    @Override
    public List<TramiteRenunciaAlumno> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TramiteRenunciaAlumno.class, "tb")
                .join("tramite tr", "tr.cicloAcademico ca")
                .join("tr.alumno al", "al.persona per", "tr.tipoTramite tt")
                .left("al.carrera car", "car.facultad ", "al.planCurricular", "al.situacionAcademica", "usuarioAnulaTramite uat", "uat.persona")
                .searchFields("al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("tb.id desc");
        return all(sql);
    }

    @Override
    public List<TramiteRenunciaAlumno> allBySolicitados() {
        Octavia sql = new Octavia();
        sql.from(TramiteRenunciaAlumno.class, "tt")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .join("al.carrera car", "per.tipoDocumento", "car.facultad")
                .filter("tt.estado", TramiteEstadoEnum.SOL)
                .filter("tr.tipoTramite", "42")
                .orderBy("per.paterno");

        return all(sql);
    }

    @Override
    public List<TramiteRenunciaAlumno> allBySolicitadosCarrera() {
        Octavia sql = new Octavia();
        sql.from(TramiteRenunciaAlumno.class, "tt")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .join("al.carrera car", "per.tipoDocumento", "car.facultad")
                .filter("tt.estado", TramiteEstadoEnum.SOL)
                .filter("tr.tipoTramite", "43")
                .orderBy("per.paterno");
        return all(sql);
    }

}
