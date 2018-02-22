package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.TipoDeudaAlumno;
import pe.edu.lamolina.pivot.dao.academico.TipoDeudaAlumnoDAO;

@Repository
public class TipoDeudaAlumnoDAOH extends AbstractEasyDAO<TipoDeudaAlumno> implements TipoDeudaAlumnoDAO {

    public TipoDeudaAlumnoDAOH() {
        super();
        setClazz(TipoDeudaAlumno.class);
    }

}
