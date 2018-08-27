package pe.edu.lamolina.pivot.dao.rrhh.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.pivot.dao.rrhh.ContratoDocenteDAO;

@Repository
public class ContratoDocenteDAOH extends AbstractEasyDAO<ContratoDocente> implements ContratoDocenteDAO {

    public ContratoDocenteDAOH() {
        super();
        setClazz(ContratoDocente.class);
    }

}
