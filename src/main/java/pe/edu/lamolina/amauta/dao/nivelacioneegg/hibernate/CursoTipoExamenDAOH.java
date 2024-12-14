package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoTipoExamenDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.nivelacioneegg.CursoTipoExamen;

@Repository
public class CursoTipoExamenDAOH extends AbstractEasyDAO<CursoTipoExamen> implements CursoTipoExamenDAO {

    public CursoTipoExamenDAOH() {
        super();
        setClazz(CursoTipoExamen.class);
    }

    @Override
    public List<CursoTipoExamen> allByCurso(Curso curso) {
        Octavia sql = Octavia.query()
                .from(CursoTipoExamen.class, "cte")
                .join("curso cu", "tipoExamenNivelacion te")
                .filter("cu.id", curso)
                .orderBy("te.orden");

        return all(sql);
    }

}
