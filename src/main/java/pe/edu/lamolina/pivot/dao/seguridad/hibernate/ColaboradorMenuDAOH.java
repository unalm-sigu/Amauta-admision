package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.seguridad.ColaboradorMenuDAO;
import pe.edu.lamolina.pivot.model.seguridad.ColaboradorMenu;

@Repository
public class ColaboradorMenuDAOH extends AbstractDAO<ColaboradorMenu> implements ColaboradorMenuDAO {

    public ColaboradorMenuDAOH() {
        super();
        setClazz(ColaboradorMenu.class);
    }

}
