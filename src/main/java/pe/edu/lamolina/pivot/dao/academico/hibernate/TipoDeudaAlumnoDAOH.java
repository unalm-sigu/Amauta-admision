package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoDeudaMaterial;
import pe.edu.lamolina.pivot.dao.academico.TipoDeudaAlumnoDAO;

@Repository
public class TipoDeudaAlumnoDAOH extends AbstractEasyDAO<TipoDeudaMaterial> implements TipoDeudaAlumnoDAO {

    public TipoDeudaAlumnoDAOH() {
        super();
        setClazz(TipoDeudaMaterial.class);
    }

}
