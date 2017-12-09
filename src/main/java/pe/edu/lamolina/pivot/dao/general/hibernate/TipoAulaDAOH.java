package pe.edu.lamolina.pivot.dao.general.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.general.TipoAulaDAO;
import pe.edu.lamolina.pivot.model.general.TipoAula;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;

@Repository
public class TipoAulaDAOH extends AbstractEasyDAO<TipoAula> implements TipoAulaDAO {
    
    public TipoAulaDAOH() {
        super();
        setClazz(TipoAula.class);
    }
    
    @Override
    public List<TipoAula> all() {
        Octavia sql = Octavia.query()
                .from(TipoAula.class, "ta")
                .orderBy("ta.nombre");
        
        return all(sql);
    }
}
