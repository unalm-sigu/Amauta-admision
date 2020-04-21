package pe.edu.lamolina.amauta.dao.seguridad.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.MenuRol;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.amauta.dao.seguridad.MenuDAO;

@Repository
public class MenuDAOH extends AbstractEasyDAO<Menu> implements MenuDAO {

    public MenuDAOH() {
        super();
        setClazz(Menu.class);
    }

    @Override
    public Menu find(long id) {
        Octavia sql = Octavia.query()
                .from(Menu.class, "me")
                .join("sistema sisi")
                .leftJoin("me.menuSuperior ms", "ms.menuSuperior mms", "mms.menuSuperior")
                .filter("me.id", id);

        return find(sql);
    }

    @Override
    public List<Menu> allByRolSistema(List<Rol> roles, Sistema sistema) {
        if (roles.isEmpty()) {
            return new ArrayList();
        }

        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct me ");
        sql.append(" from ").append(MenuRol.class.getName()).append(" as mero ");
        sql.append(" inner join mero.menu me ");
        sql.append(" inner join fetch me.sistema ss ");
        sql.append("  left join fetch me.menuSuperior mes ");
        sql.append("  left join fetch mes.menuSuperior mss ");
        sql.append("  left join fetch mss.menuSuperior  ");
        sql.append(" inner join mero.rol ro ");
        sql.append(" where ro.id in :ROLES ");
        sql.append("   and ss.id = :SISTEMA ");
        sql.append(" order by me.orden ");

        List<Long> ids = new ArrayList(roles.stream().collect(Collectors.toMap(x -> x.getId(), x -> x.getId())).values());
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameterList("ROLES", ids);
        query.setLong("SISTEMA", sistema.getId());

        return query.list();
    }

    @Override
    public List<Menu> allMenuSystem(Sistema sistema) {
        Octavia sql = Octavia.query()
                .from(Menu.class, "me")
                .join("sistema sisi")
                .leftJoin("me.menuSuperior ms", "ms.menuSuperior mms", "mms.menuSuperior")
                .filter("sisi.id", sistema);

        return all(sql);
    }

    @Override
    public Integer getMayorOrden(Sistema sistema) {

        StringBuilder sql = new StringBuilder();
        sql.append("  select me.orden ");
        sql.append("  from ").append(Menu.class.getName()).append(" as me ");
        sql.append("  inner join me.sistema si ");
        sql.append("  where 1 = 1 ");
        sql.append("  and si.id = :SISTEMA ");
        sql.append("  order by me.orden desc");
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("SISTEMA", sistema.getId());
        query.setMaxResults(1);

        return (Integer) query.uniqueResult();

    }

    @Override
    public List<Menu> allByTipo(MenuTipoEnum menuTipoEnum, Sistema sistema) {
        Octavia sql = Octavia.query()
                .from(Menu.class, "me")
                .join("sistema sisi")
                .leftJoin("me.menuSuperior ms", "ms.menuSuperior mms", "mms.menuSuperior")
                .filter("sisi.id", sistema)
                .filter("me.tipo", menuTipoEnum)
                .orderBy("me.orden asc");

        return all(sql);
    }

    @Override
    public List<Menu> allBySuperMenu(Sistema sistema, Menu menuSuperior) {
        Octavia sql = Octavia.query()
                .from(Menu.class, "me")
                .join("sistema sisi")
                .leftJoin("me.menuSuperior ms", "ms.menuSuperior mms", "mms.menuSuperior")
                .filter("sisi.id", sistema)
                .filter("ms.id", menuSuperior)
                .orderBy("me.orden asc");

        return all(sql);
    }

    @Override
    public Menu findByTipoOrden(MenuTipoEnum menuTipoEnum, Sistema sistema, Integer orden) {
        Octavia sql = Octavia.query()
                .from(Menu.class, "me")
                .join("sistema sisi")
                .leftJoin("me.menuSuperior ms", "ms.menuSuperior mms", "mms.menuSuperior")
                .filter("sisi.id", sistema)
                .filter("me.orden", orden)
                .filter("me.tipo", menuTipoEnum);

        return find(sql);
    }

    @Override
    public Menu findBySuperMenuOrden(Sistema sistema, Menu menuSuperior, Integer orden) {
        Octavia sql = Octavia.query()
                .from(Menu.class, "me")
                .join("sistema sisi")
                .leftJoin("me.menuSuperior ms", "ms.menuSuperior mms", "mms.menuSuperior")
                .filter("sisi.id", sistema)
                .filter("me.orden", orden)
                .filter("ms.id", menuSuperior);

        return find(sql);
    }

    @Override
    public Integer getMayorOrdenGrupo(Sistema sistema, Menu menuSuperior) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select me.orden ");
        sql.append("  from ").append(Menu.class.getName()).append(" as me ");
        sql.append("  inner join me.sistema si ");
        sql.append("  inner join me.menuSuperior sup ");
        sql.append("  where 1 = 1 ");
        sql.append("  and si.id = :SISTEMA ");
        sql.append("  and sup.id = :MENUSUPER ");
        sql.append("  order by me.orden desc");
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("SISTEMA", sistema.getId());
        query.setLong("MENUSUPER", menuSuperior.getId());
        query.setMaxResults(1);
        return (Integer) query.uniqueResult();
    }

    @Override
    public Integer getMayorOrdenTipo(Sistema sistema, MenuTipoEnum menuTipoEnum) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select me.orden ");
        sql.append("  from ").append(Menu.class.getName()).append(" as me ");
        sql.append("  inner join me.sistema si ");
        sql.append("  where 1 = 1 ");
        sql.append("  and si.id = :SISTEMA ");
        sql.append("  and me.tipo = :TIPOMENU ");
        sql.append("  order by me.orden desc");
        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("SISTEMA", sistema.getId());
        query.setString("TIPOMENU", menuTipoEnum.name());
        query.setMaxResults(1);
        return (Integer) query.uniqueResult();
    }

    @Override
    public List<Menu> allMenuSystemByRol(Sistema sistema, Long idRol) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select me ");
        sql.append(" from ").append(MenuRol.class.getName()).append(" as mero ");
        sql.append(" inner join mero.menu me ");
        sql.append("  left join fetch me.menuSuperior mes ");
        sql.append("  left join fetch mes.menuSuperior mss ");
        sql.append("  left join fetch mss.menuSuperior  ");
        sql.append(" inner join mero.rol ro ");
        sql.append(" inner join me.sistema sis ");
        sql.append(" where ro.id = ").append(idRol);
        sql.append(" and sis.id = ").append(sistema.getId());
        sql.append(" order by me.orden ");

        Query query = getCurrentSession().createQuery(sql.toString());
        return query.list();
    }

    @Override
    public Menu findByRuta(String ruta) {
        Octavia sql = Octavia.query()
                .from(Menu.class, "me")
                .filter("me.ruta", ruta);

        return find(sql);
    }

}
