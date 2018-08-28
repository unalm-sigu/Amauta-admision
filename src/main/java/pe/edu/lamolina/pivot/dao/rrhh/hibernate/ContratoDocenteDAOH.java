package pe.edu.lamolina.pivot.dao.rrhh.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.pivot.dao.rrhh.ContratoDocenteDAO;

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
                .join("categoria", "situacion", "dedicacion", "cicloInicioContrato", "docente d")
                .left("cicloFinContrato")
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

}
