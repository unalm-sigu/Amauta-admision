package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Repository
public class CursoDAOH extends AbstractEasyDAO<Curso> implements CursoDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CursoDAOH() {
        super();
        setClazz(Curso.class);
    }

    @Override
    public Curso find(long idCurso) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("modalidadEstudio")
                .leftJoin("planCalificacion pc", "departamentoAcademico da", "da.facultad")
                .filter("cu.id", idCurso);

        return find(sql);
    }

    @Override
    public List<Curso> allForSistemaCalificacion(String nombre, Long idDepartamentoAca, PlanCalificacion planCalificacion, Long idCiclo) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        StringBuilder sql = new StringBuilder();
        sql.append("  from ").append(Curso.class.getName()).append(" as cur ");
        sql.append(" left join fetch cur.departamentoAcademico da ");
        sql.append(" left join fetch cur.planCalificacion pc ");
        sql.append(" where 1=1 ");

        if (planCalificacion.isTipoCicloNivelacion()) {
            sql.append("  and   ( cur.planCalificacion.id != :PLAN_CAL or cur.planCalificacion is null) ");
        } else if (planCalificacion.isTipoCicloRegular()) {
            sql.append("  and   ( cur.planCalificacion.id != :PLAN_CAL or cur.planCalificacion is null) ");
        } else {
            throw new PhobosException("El plan calificacion no tiene tipo de ciclo");
        }

        sql.append("  and    cur.id in ( select cu.id ");
        sql.append("                       from ").append(GrupoSeccion.class.getSimpleName()).append(" as gs ");
        sql.append("                      inner join gs.curso cu ");
        sql.append("                      inner join gs.cicloAcademico ca ");
        sql.append("                      where ca.id = :CICLO ) ");
        sql.append("  and    da.id = :DEP_ACA ");
        sql.append("  and    cur.nombre like :NOMBRE ");
        sql.append(" order by cur.nombre ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("PLAN_CAL", planCalificacion.getId());
        query.setParameter("DEP_ACA", idDepartamentoAca);
        query.setParameter("CICLO", idCiclo);
        query.setString("NOMBRE", nombre);
        query.setMaxResults(15);

        return query.list();
    }

    @Override
    public List<Curso> allByPlan(PlanCalificacion plan) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("planCalificacion pc")
                .filter("pc.id", plan);

        return all(sql);
    }

    @Override
    public List<Curso> allByPlanRegular(PlanCalificacion plan) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("planCalificacionRegular pc")
                .filter("pc.id", plan);

        return all(sql);
    }

    @Override
    public List<Curso> allActiveByPlan(PlanCalificacion plan) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .leftJoin("planCalificacion pc", "planCalificacionRegular pcr")
                .filter("pc.estado", EstadoEnum.ACT);

        if (plan.isTipoCicloNivelacion()) {
            sql.filter("pc.id", plan);
        } else if (plan.isTipoCicloRegular()) {
            sql.filter("pcr.id", plan);
        } else {
            throw new PhobosException("PLan calificacion de tipo de ciclo");
        }

        return all(sql);
    }

    @Override
    public Curso findByCode(String codigo) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .leftJoin("planCalificacion pc", "departamentoAcademico da", "da.facultad")
                .filter("cu.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> departamentos) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Curso.class, "cu")
                .join("departamentoAcademico da", "da.facultad fa")
                .leftJoin("planCalificacion pc")
                .in("da.id", departamentos)
                .searchFields("cu.nombre", "cu.codigo", "fa.nombre")
                .orderBy("cu.id desc");

        return all(sql);
    }

    @Override
    public List<Curso> allByNombreFilter(String nombre, List<String> tiposCurriculaEnum, Integer limit) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur");
        if (StringUtils.isNotBlank(nombre)) {
            sql.like("cur.nombre", nombre);
        }
        sql.in("cur.tipoCurricula", tiposCurriculaEnum)
                .orderBy("cur.nombre")
                .limit(limit);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Curso> allByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cur")
                .like("cur.codigo", codigo)
                .orderBy("cur.nombre");
        return sql.all(getCurrentSession());
    }

    @Override
    public Curso findLastCodigoByCurCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(Curso.class, "cu")
                .join("departamentoAcademico da", "da.facultad fa")
                .filter("cu.codigo", "like", codigo)
                .orderBy("cu.codigo DESC")
                .limit(1);

        return find(sql);
    }

}
