package pe.edu.lamolina.amauta.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.AreaPosgrado;
import pe.edu.lamolina.amauta.dao.academico.AreaPosgradoDAO;

@Repository
public class AreaPosgradoDAOH extends AbstractEasyDAO<AreaPosgrado> implements AreaPosgradoDAO {

    public AreaPosgradoDAOH() {
        super();
        setClazz(AreaPosgrado.class);
    }

}
