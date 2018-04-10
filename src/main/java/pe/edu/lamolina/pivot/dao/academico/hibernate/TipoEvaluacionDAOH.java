package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.TipoEvaluacionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoEvaluacion;

@Repository
public class TipoEvaluacionDAOH extends AbstractEasyDAO<TipoEvaluacion> implements TipoEvaluacionDAO {
    
    public TipoEvaluacionDAOH() {
        super();
        setClazz(TipoEvaluacion.class);
    }
    
    public List<TipoEvaluacion> all() {
        Octavia sql = Octavia.query()
                .from(TipoEvaluacion.class, "te")
                .orderBy("te.orden");
        
        return all(sql);
    }
}
