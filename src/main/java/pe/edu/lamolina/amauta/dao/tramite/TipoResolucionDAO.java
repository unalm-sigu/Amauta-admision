package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import pe.edu.lamolina.model.tramite.TipoResolucion;

public interface TipoResolucionDAO extends EasyDAO<TipoResolucion> {

    TipoResolucion finByCodigo(TipoResolucionEnum tipoResolucionEnum);

    List<TipoResolucion> allByCodigo(List<String> codigos);

    TipoResolucion find(Long id);
    
}
