package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.seguridad.MenuRolDAO;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.MenuRol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;
import pe.albatross.octavia.Octavia;

@Repository
public class MenuRolDAOH extends AbstractDAO<MenuRol> implements MenuRolDAO {

    public MenuRolDAOH() {
        super();
        setClazz(MenuRol.class);
    }

    @Override
    public MenuRol findByMenuRol(MenuRol menuRol) {
        Octavia sql = Octavia.query()
                .from(MenuRol.class, "mero")
                .join("menu me", "rol ro", "me.sistema s")
                .filter("me.id", menuRol.getMenu())
                .filter("ro.id", menuRol.getRol());
        return (MenuRol) sql.find(getCurrentSession());
    }

    @Override
    public List<MenuRol> allByMenu(Menu menu) {
        Octavia sql = Octavia.query()
                .from(MenuRol.class, "mero")
                .join("menu me", "rol ro", "me.sistema s")
                .filter("me.id", menu);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<MenuRol> allBySistema(Sistema sistema) {
        Octavia sql = Octavia.query()
                .from(MenuRol.class, "mero")
                .join("menu me", "rol ro", "me.sistema s")
                .filter("s.id", sistema);

        return sql.all(getCurrentSession());
    }

}
