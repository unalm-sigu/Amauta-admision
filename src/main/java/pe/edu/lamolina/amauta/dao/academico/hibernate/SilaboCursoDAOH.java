package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.amauta.dao.academico.SilaboCursoDAO;

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
                .join("curso cu", "cu.departamentoAcademico da", "da.facultad fa")
                .join("sc.departamentoAcademico", "cu.modalidadEstudio mo")
                .searchFields("cu.nombre", "cu.codigo", "fa.nombre", "fa.nombre", "da.nombre", "mo.nombre")
                .orderBy("sc.id");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<SilaboCurso> allByIds(ArrayList<Long> silabus) {
        Octavia sql = new Octavia()
                .from(SilaboCurso.class, "sc")
                .join("curso cu", "cu.departamentoAcademico da", "da.facultad fa")
                .join("sc.departamentoAcademico", "cu.modalidadEstudio mo")
                .in("sc.id", silabus);
        return this.all(sql);
    }

}
