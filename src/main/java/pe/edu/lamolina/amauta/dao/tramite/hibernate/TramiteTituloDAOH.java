package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTituloDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

@Repository
public class TramiteTituloDAOH extends AbstractEasyDAO<TramiteTitulo> implements TramiteTituloDAO {

    public TramiteTituloDAOH() {
        super();
        setClazz(TramiteTitulo.class);
    }

    @Override
    public TramiteTitulo findByTramite(Tramite tramite) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class)
                .join("tramite tr", "tr.alumno al", "al.persona")
                .left("al.consejero con", "con.colaborador cola", "cola.persona")
                .filter("tr.id", tramite);

        return find(sql);

    }

    @Override
    public List<TramiteTitulo> allByTramites(List<Tramite> tramites) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class)
                .join("tramite tr", "tr.alumno al", "al.persona")
                .in("tr.id", tramites);

        return all(sql);
    }

    @Override
    public TramiteTitulo findByAlumnoAct(Alumno alumno) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona", "al.carrera car")
                .join("car.facultad")
                .filter("tb.estado", TramiteEstadoEnum.SOL.name())
                .filter("al.id", alumno);

        return find(sql);
    }

    @Override
    public TramiteTitulo findByAlumnoACEP(Alumno alumno) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona")
                .join("resolucion ")
                .filter("tb.estado", TramiteEstadoEnum.ACEP.name())
                .filter("al.id", alumno);

        return find(sql);
    }

    @Override
    public TramiteTitulo findByAlumnoFacultadACEP(Alumno alumno) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona")
                .join("resolucionFacultad")
                .filter("tb.estadoTitulo", TramiteEstadoEnum.ACEP.name())
                .filter("al.id", alumno);
        return find(sql);
    }

    @Override
    public List<TramiteTitulo> allByResolucion(Resolucion resolucionDB) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class)
                .join("resolucion res")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .join("per.tipoDocumento", "tr.cicloAcademico")
                .filter("res.id", resolucionDB);

        return all(sql);
    }

    @Override
    public List<TramiteTitulo> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(TramiteTitulo.class, "tb")
                .join("tramite tr", "tr.cicloAcademico ca")
                .join("tr.compania", "tr.persona per", "tr.alumno al", "tr.tipoTramite tt")
                .left("al.carrera car", "car.facultad ", "resolucion", "al.planCurricular")
                .searchFields("al.estado", "al.codigo", "per.numeroDocIdentidad")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("ca.codigo desc", "tb.id desc");

        return all(sql);
    }

    @Override
    public List<TramiteTitulo> allBySolicitados() {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tt")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .join("al.carrera car", "per.tipoDocumento", "car.facultad")
                .filter("tt.estado", TramiteEstadoEnum.SOL.name())
                .orderBy("per.paterno");

        return all(sql);
    }

    @Override
    public List<TramiteTitulo> allBySolicitadosFacultad(Facultad facultad) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tt")
                .join("tramite tr", "tr.alumno al", "al.persona per")
                .join("al.carrera car", "per.tipoDocumento", "car.facultad", "al.cicloActivoRegular ci")
                .filter("ci.codigoAnterior",">=","20171")
                .filter("car.facultad",facultad)
                .filter("tt.estadoTitulo", TramiteEstadoEnum.SOL.name())
                .orderBy("per.paterno");

        return all(sql);
    }

    @Override
    public List<TramiteTitulo> allByResolucionFacultad(Resolucion resolucion) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona per", "tr.cicloAcademico")
                .filter("tb.resolucionFacultad", resolucion)
                .orderBy("per.paterno");
        return all(sql);
    }

    @Override
    public List<TramiteTitulo> allByTituloFacultad(Resolucion resolucion) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona per", "tr.cicloAcademico")
                .filter("tb.resolucionFacultad", resolucion)
                .orderBy("per.paterno");
        return all(sql);
    }

    @Override
    public List<TramiteTitulo> allByTituloFacultadRes(Resolucion resolucion) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona per", "tr.cicloAcademico")
                .filter("tb.resolucion", resolucion)
                .orderBy("per.paterno");
        return all(sql);
    }

    @Override
    public TramiteTitulo findByAlumnoActFacultad(Alumno alumno) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona", "al.carrera car")
                .join("car.facultad")
                .filter("tb.estadoTitulo", TramiteEstadoEnum.ACEP.name())
                .filter("al.id", alumno);

        return find(sql);
    }

    @Override
    public TramiteTitulo findByAlumnoSolFacultad(Alumno alumno) {
        Octavia sql = new Octavia();
        sql.from(TramiteTitulo.class, "tb")
                .join("tramite tr", "tr.alumno al", "al.persona", "al.carrera car")
                .join("car.facultad")
                .filter("tb.estadoTitulo", TramiteEstadoEnum.SOL.name())
                .filter("al.id", alumno);

        return find(sql);
    }

}
