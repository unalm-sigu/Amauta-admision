package pe.edu.lamolina.amauta.dao.general.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.ColaboradorEstado;
import pe.edu.lamolina.amauta.dao.general.ColaboradorEstadoDAO;

@Repository
public class ColaboradorEstadoDAOH extends AbstractEasyDAO<ColaboradorEstado> implements ColaboradorEstadoDAO {

    public ColaboradorEstadoDAOH() {
        super();
        setClazz(ColaboradorEstado.class);
    }

}
