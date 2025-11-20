package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.tramite.DocenteResolucionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.tramite.DocenteResolucion;
import pe.edu.lamolina.model.tramite.Resolucion;

import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.PEND;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL;

import java.util.Collections;
import java.util.List;

@Repository
public class DocenteResolucionDAOH extends AbstractEasyDAO<DocenteResolucion> implements DocenteResolucionDAO {

    public DocenteResolucionDAOH() {
        super();
        setClazz(DocenteResolucion.class);
    }

    @Override
    public List<DocenteResolucion> allTramiteByFilter(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(DocenteResolucion.class, "docr")
                .join("docente doc","tramite tra")
                .join("doc.persona per")
                .left("resolucionConsejo","resolucionFacultad")
                .searchFields("per.numeroDocIdentidad","doc.codigo")
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .orderBy("docr.id desc");

        return all(sql);
    }

    @Override
    public DocenteResolucion findByCicloAndTipo(CicloAcademico cicloAcademico, Docente docente) {
        Octavia sql = new Octavia();
        sql.from(DocenteResolucion.class, "docr")
                .join("docente doc","tramite tra")
                .filter("docr.estadoConsejo",SOL)
                .filter("docr.estadoFacultad",SOL)
                .filter("docr.ciclo",cicloAcademico)
                .filter("doc.id",docente.getId());

        return find(sql);
    }

    @Override
    public List<DocenteResolucion> allDocenteResolucionConsejoByResolucion(Resolucion resolucion) {
        Octavia sql = new Octavia()
                .from(DocenteResolucion.class, "docr")
                .join("docente doc","doc.persona per")
                .join("resolucionConsejo rec")
                .filter("rec.id",resolucion.getId())
                .orderBy("per.paterno");
        return all(sql);
    }

    @Override
    public List<DocenteResolucion> allDocenteResolucionFacultadByResolucion(Resolucion resolucion) {
        Octavia sql = new Octavia()
                .from(DocenteResolucion.class, "docr")
                .join("docente doc","doc.persona per")
                .join("resolucionFacultad rec")
                .filter("rec.id",resolucion.getId())
                .orderBy("per.paterno");
        return all(sql);
    }

    @Override
    public List<DocenteResolucion> allDocenteResolucionConsejo() {
        Octavia sql = new Octavia()
                .from(DocenteResolucion.class, "docr")
                .join("tramite tr", "tr.docente doc","doc.persona per")
                .join("per.tipoDocumento")
                .filter("docr.estadoConsejo",SOL);
        return all(sql);
    }

    @Override
    public List<DocenteResolucion> allDocenteResolucionFacultad() {
        Octavia sql = new Octavia()
                .from(DocenteResolucion.class, "docr")
                .join("tramite tr", "tr.docente doc","doc.persona per")
                .join("per.tipoDocumento")
                .filter("docr.estadoFacultad",SOL);
        return all(sql);
    }

    @Override
    public DocenteResolucion findByDocenteConsejoAct(Docente docente) {
        Octavia sql = new Octavia()
                .from(DocenteResolucion.class, "docr")
                .join("tramite tr", "tr.docente doc","doc.persona per")
                .filter("docr.estadoConsejo",SOL)
                .filter("doc.id",docente.getId());
        return find(sql);
    }

    @Override
    public DocenteResolucion findByDocenteFacultadAct(Docente docente) {
        Octavia sql = new Octavia()
                .from(DocenteResolucion.class, "docr")
                .join("tramite tr", "tr.docente doc","doc.persona per")
                .filter("docr.estadoFacultad",SOL)
                .filter("doc.id",docente.getId());
        return find(sql);
    }
}
