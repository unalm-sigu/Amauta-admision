package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;

@Repository
public class ResolucionDAOH extends AbstractEasyDAO<Resolucion> implements ResolucionDAO {
    
    public ResolucionDAOH() {
        super();
        setClazz(Resolucion.class);
    }
    
    @Override
    public List<Resolucion> allByDyna(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Resolucion.class, "t")
                .join("oficina", "userRegistro ur")
                .join("ur.persona per");
        return this.all(sql);
    }
    
}
