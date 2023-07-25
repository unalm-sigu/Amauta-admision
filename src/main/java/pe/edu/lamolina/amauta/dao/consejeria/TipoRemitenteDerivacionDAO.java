package pe.edu.lamolina.amauta.dao.consejeria;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum;
import pe.edu.lamolina.model.tutoria.TipoRemitenteDerivacion;

public interface TipoRemitenteDerivacionDAO extends EasyDAO<TipoRemitenteDerivacion> {

    TipoRemitenteDerivacion findByCodigo(String codigo);

    TipoRemitenteDerivacion findByCodigoNodo(NodoDerivacionEnum nodo);

}
