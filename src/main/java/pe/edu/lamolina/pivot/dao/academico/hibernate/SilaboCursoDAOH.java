package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.pivot.dao.academico.SilaboCursoDAO;

@Repository
public class SilaboCursoDAOH extends AbstractEasyDAO<SilaboCurso> implements SilaboCursoDAO {

    public SilaboCursoDAOH() {
        super();
        setClazz(SilaboCurso.class);
    }

    @Override
    public List<SilaboCurso> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(SilaboCurso.class, "sc")
                .join("curso cu", "cicloVigenciaInicio cvi")
                .leftJoin("cicloVigenciaFin cvf", "planCalificacion pc")
                .leftJoin("cu.departamentoAcademico da", "da.facultad fa")
                .searchFields("cu.nombre", "cu.codigo", "fa.nombre")
                .orderBy("sc.id");
        sql.beginRelativeFilters();
        return sql.all(getCurrentSession());
    }

}
