package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.dao.tramite.CursoDirigidoDAO;

@Repository
public class CursoDirigidoDAOH extends AbstractEasyDAO<CursoDirigido> implements CursoDirigidoDAO {

    public CursoDirigidoDAOH() {
        super();
        setClazz(CursoDirigido.class);
    }

    @Override
    public CursoDirigido findByTramite(Tramite tramite) {
        Octavia sql = Octavia.query(CursoDirigido.class, "cd")
                .join("tramite tra")
                .filter("tra.id", tramite);
        
        return find(sql);
    }

}
