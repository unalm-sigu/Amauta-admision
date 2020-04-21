package pe.edu.lamolina.amauta.dao.seguridad.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.seguridad.ColaboradorMenu;
import pe.edu.lamolina.amauta.dao.seguridad.ColaboradorMenuDAO;

@Repository
public class ColaboradorMenuDAOH extends AbstractEasyDAO<ColaboradorMenu> implements ColaboradorMenuDAO {

    public ColaboradorMenuDAOH() {
        super();
        setClazz(ColaboradorMenu.class);
    }

}
