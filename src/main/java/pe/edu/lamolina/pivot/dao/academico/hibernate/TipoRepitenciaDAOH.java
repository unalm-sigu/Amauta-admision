package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.pivot.dao.academico.TipoRepitenciaDAO;

@Repository
public class TipoRepitenciaDAOH extends AbstractEasyDAO<TipoRepitencia> implements TipoRepitenciaDAO {

    public TipoRepitenciaDAOH() {
        super();
        setClazz(TipoRepitencia.class);
    }

}
