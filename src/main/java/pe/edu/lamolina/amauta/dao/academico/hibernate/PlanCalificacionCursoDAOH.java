package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.amauta.dao.academico.PlanCalificacionCursoDAO;

@Repository
public class PlanCalificacionCursoDAOH extends AbstractEasyDAO<PlanCalificacionCurso> implements PlanCalificacionCursoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PlanCalificacionCursoDAOH() {
        super();
        setClazz(PlanCalificacionCurso.class);
    }

    @Override
    public PlanCalificacionCurso findByFilter(PlanCalificacion planCalificacion, Curso curso, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(PlanCalificacionCurso.class, "pc")
                .join("planCalificacion pln", "curso cur");

        if (planCalificacion != null) {
            sql.filter("pln.id", planCalificacion);
        }
        if (curso != null) {
            sql.filter("cur.id", curso);
        }
        if (estadoEnum != null) {
            sql.filter("pc.estado", estadoEnum);
        }

        return find(sql);
    }

    @Override
    public List<PlanCalificacionCurso> allByFilter(PlanCalificacion planCalificacion, TipoCicloEnum tipoCicloEnum, Curso curso, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query()
                .from(PlanCalificacionCurso.class, "pc")
                .join("planCalificacion pln", "curso cur")
                .left("pln.sistemaNotas");

        if (planCalificacion != null) {
            sql.filter("pln.id", planCalificacion.getId());
        }
        if (curso != null) {
            sql.filter("cur.id", curso);
        }
        if (estadoEnum != null) {
            sql.filter("pc.estado", estadoEnum);
        }
        if (tipoCicloEnum != null) {
            sql.filter("pln.tipoCiclo", tipoCicloEnum);
        }

        return all(sql);
    }

    @Override
    public List<PlanCalificacionCurso> allByFilterDyna(DynatableFilter filter, PlanCalificacion planCalificacion, EstadoEnum estadoPlanCurdo) {
        DynatableSql sql = new DynatableSql(filter)
                .from(PlanCalificacionCurso.class, "plncur")
                .join("planCalificacion pc", "curso cur", "cur.departamentoAcademico da")
                .searchFields("cur.nombre", "cur.codigo", "cur.fechaPlanCalificacion")
                .orderBy("plncur.id desc");

        if (planCalificacion != null) {
            sql.filter("pc.id", planCalificacion);
        }
        if (estadoPlanCurdo != null) {
            sql.filter("plncur.estado", estadoPlanCurdo);
        }

        return all(sql);
    }

    @Override
    public List<PlanCalificacionCurso> allActivosByPLanes(List<PlanCalificacion> planes) {
        Octavia sql = Octavia.query()
                .from(PlanCalificacionCurso.class, "pc")
                .join("planCalificacion pln", "curso cur")
                .left("pln.sistemaNotas")
                .filter("pc.estado", EstadoEnum.ACT)
                .in("pln.id", planes);

        return all(sql);
    }

}
