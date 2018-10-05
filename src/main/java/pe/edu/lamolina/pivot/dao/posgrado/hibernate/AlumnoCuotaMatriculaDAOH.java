package pe.edu.lamolina.pivot.dao.posgrado.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.posgrado.AlumnoCuotaMatricula;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoCuotaMatriculaDAO;

@Repository
public class AlumnoCuotaMatriculaDAOH extends AbstractEasyDAO<AlumnoCuotaMatricula> implements AlumnoCuotaMatriculaDAO {

    public AlumnoCuotaMatriculaDAOH() {
        super();
        setClazz(AlumnoCuotaMatricula.class);
    }

}
