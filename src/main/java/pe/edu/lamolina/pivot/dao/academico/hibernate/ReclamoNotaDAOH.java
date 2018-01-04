package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.lamolina.pivot.dao.academico.ReclamoNotaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.ReclamoNota;

@Repository
public class ReclamoNotaDAOH extends AbstractEasyDAO<ReclamoNota> implements ReclamoNotaDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ReclamoNotaDAOH() {
        super();
        setClazz(ReclamoNota.class);
    }

    @Override
    public List<ReclamoNota> allByFilter(Evaluacion evaluacion) {
        Octavia sql = Octavia.query()
                .from(ReclamoNota.class, "rn")
                .join("alumno alu", "evaluacion eva")
                .filter("eva.id", evaluacion);

        return all(sql);
    }

    @Override
    public void deleteByEvaluacion(Evaluacion evaluacion) {
        String sql = "delete from ReclamoNota rn where rn.evaluacion.id=:prm_evaluacion";
        Query query = getCurrentSession().createQuery(sql);
        query.setLong("prm_evaluacion", evaluacion.getId());
        query.executeUpdate();
    }

}
