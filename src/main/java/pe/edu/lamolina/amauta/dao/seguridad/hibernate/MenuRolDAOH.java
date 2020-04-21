package pe.edu.lamolina.amauta.dao.seguridad.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.amauta.dao.seguridad.MenuRolDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.MenuRol;
import pe.edu.lamolina.model.seguridad.Sistema;

@Repository
public class MenuRolDAOH extends AbstractEasyDAO<MenuRol> implements MenuRolDAO {

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

        return find(sql);
    }

    @Override
    public List<MenuRol> allByMenu(Menu menu) {
        Octavia sql = Octavia.query()
                .from(MenuRol.class, "mero")
                .join("menu me", "rol ro", "me.sistema s")
                .filter("me.id", menu);

        return all(sql);
    }

    @Override
    public List<MenuRol> allBySistema(Sistema sistema) {
        Octavia sql = Octavia.query()
                .from(MenuRol.class, "mero")
                .join("menu me", "rol ro", "me.sistema s")
                .filter("s.id", sistema);

        return all(sql);
    }

}
