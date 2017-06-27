package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.ReclamoNotaDAO;
import pe.edu.lamolina.pivot.model.academico.ReclamoNota;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.pivot.model.academico.Evaluacion;

@Repository
public class ReclamoNotaDAOH extends AbstractDAO<ReclamoNota> implements ReclamoNotaDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ReclamoNotaDAOH() {
        super();
        setClazz(ReclamoNota.class);
    }

    @Override
    public List<ReclamoNota> allByFilter(Evaluacion evaluacion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("rn");
        sqlUtil.parents("alumno alu", "evaluacion eva");
        sqlUtil.filter("eva.id", evaluacion.getId());
        return this.all(sqlUtil);
    }

    @Override
    public void deleteByEvaluacion(Evaluacion evaluacion) {
        String strQuery = "delete from ReclamoNota rn where rn.evaluacion.id=:prm_evaluacion";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("prm_evaluacion", evaluacion.getId());
        query.executeUpdate();
    }

}
