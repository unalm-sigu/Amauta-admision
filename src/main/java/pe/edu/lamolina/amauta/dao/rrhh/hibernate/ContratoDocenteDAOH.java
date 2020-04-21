package pe.edu.lamolina.amauta.dao.rrhh.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.amauta.dao.rrhh.ContratoDocenteDAO;

@Repository
public class ContratoDocenteDAOH extends AbstractEasyDAO<ContratoDocente> implements ContratoDocenteDAO {

    public ContratoDocenteDAOH() {
        super();
        setClazz(ContratoDocente.class);
    }

    @Override
    public List<ContratoDocente> allByDynatableProfesor(DynatableFilter filter, Docente docente) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ContratoDocente.class, "cd")
                .join("cicloInicioContrato", "docente d")
                .leftJoin("resolucionFacultad ref", "resolucionConsejo rec")
                .leftJoin("ref.oficina", "rec.oficina")
                .left("categoria", "situacion", "dedicacion", "cicloFinContrato")
                .filter("d.id", docente)
                .orderBy("cd.id desc");

        return all(sql);
    }

    @Override
    public List<ContratoDocente> allByPeriodoDocente(CicloAcademico cicloInicio, CicloAcademico cicloFin, Docente docente) {
        Octavia sql = Octavia.query(ContratoDocente.class, "cd")
                .join("docente d")
                .join("cicloInicioContrato ci", "cicloFinContrato cf")
                .filter("d.id", docente)
                .beginBlock()
                .__().beginBlock()
                .__().__().filter("cf.codigo", "<=", cicloFin.getCodigo())
                .__().__().filter("ci.codigo", ">", cicloInicio.getCodigo())
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("cf.codigo", "<=", cicloFin.getCodigo())
                .__().__().filter("ci.codigo", ">", cicloFin.getCodigo())
                .__().endBlock()
                .__().beginBlock()
                .__().__().filter("ci.codigo", "<=", cicloInicio.getCodigo())
                .__().__().filter("cf.codigo", ">", cicloInicio.getCodigo())
                .__().endBlock()
                .endBlock();

        return all(sql);
    }

    @Override
    public List<ContratoDocente> allByDocente(List<Long> idsDoc) {
        Octavia sql = Octavia.query()
                .from(ContratoDocente.class, "cd")
                .join("docente d", "categoria", "dedicacion", "situacion")
                .join("cicloInicioContrato", "cicloFinContrato")
                .in("d.id", idsDoc);
        return all(sql);
    }

}
