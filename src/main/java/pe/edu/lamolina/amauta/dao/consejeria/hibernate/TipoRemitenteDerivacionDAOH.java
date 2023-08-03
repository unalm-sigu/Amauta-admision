package pe.edu.lamolina.amauta.dao.consejeria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.consejeria.TipoRemitenteDerivacionDAO;
import pe.edu.lamolina.model.enums.consejeria.NodoDerivacionEnum;
import pe.edu.lamolina.model.tutoria.TipoRemitenteDerivacion;

@Repository
public class TipoRemitenteDerivacionDAOH extends AbstractEasyDAO<TipoRemitenteDerivacion> implements TipoRemitenteDerivacionDAO {

    public TipoRemitenteDerivacionDAOH() {
        super();
        setClazz(TipoRemitenteDerivacion.class);
    }

    @Override
    public TipoRemitenteDerivacion findByCodigo(String codigo) {
        Octavia sql = new Octavia()
                .from(TipoRemitenteDerivacion.class, "tr")
                .filter("tr.codigo", codigo);

        return find(sql);
    }

    @Override
    public TipoRemitenteDerivacion findByCodigoNodo(NodoDerivacionEnum nodo) {
        Octavia sql = new Octavia()
                .from(TipoRemitenteDerivacion.class, "tr")
                .filter("tr.codigo", nodo);

        return find(sql);
    }

}
