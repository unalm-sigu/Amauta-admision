package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;

@Repository
public class TipoResolucionDAOH extends AbstractEasyDAO<TipoResolucion> implements TipoResolucionDAO {

    public TipoResolucionDAOH() {
        super();
        setClazz(TipoResolucion.class);
    }
    @Override
    public TipoResolucion finByCodigo(TipoResolucionEnum tipoResolucionEnum){
        Octavia sql = new Octavia()
                .from(TipoResolucion.class ,"tr")
                .filter("codigo", tipoResolucionEnum);
        return find(sql);
    }
}
