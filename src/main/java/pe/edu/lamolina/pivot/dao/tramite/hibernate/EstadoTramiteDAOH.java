package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;

@Repository
public class EstadoTramiteDAOH extends AbstractEasyDAO<EstadoTramite> implements EstadoTramiteDAO {

    public EstadoTramiteDAOH() {
        super();
        setClazz(EstadoTramite.class);
    }

}
