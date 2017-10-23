package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.dao.seguridad.MenuDAO;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.MenuRol;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.zelper.enums.MenuTipoEnum;


@Repository
public class MenuDAOH extends AbstractDAO<Menu> implements MenuDAO {

    public MenuDAOH() {
        super();
        setClazz(Menu.class);
    }

    @Override
    public Menu find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("me")
                .parents("sistema sisi")
                .parents("left _me.menuSuperior ms", "left _ms.menuSuperior mms", "left _mms.menuSuperior")
                .filter("me.id", id);
        return find(sqlUtil);
    }

    @Override
    public List<Menu> allMenuRolActivo(Rol rolAsignar) {

        StringBuilder sql = new StringBuilder();

        sql.append(" select distinct me ");
        sql.append(" from ").append(MenuRol.class.getName()).append(" as mero ");
        sql.append(" inner join mero.menu me ");
        sql.append("  left join fetch me.menuSuperior mes ");
        sql.append("  left join fetch mes.menuSuperior mss ");
        sql.append("  left join fetch mss.menuSuperior  ");
        sql.append(" inner join mero.rol ro ");
        sql.append(" where ro.id = :ROLACTIVO ");
        sql.append(" order by me.orden ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("ROLACTIVO", rolAsignar.getId());

        return query.list();
    }

    @Override
    public List<Menu> allMenuSystem(Sistema sistema) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("me")
                .parents("sistema sisi")
                .parents("left _me.menuSuperior ms", "left _ms.menuSuperior mms", "left _mms.menuSuperior")
                .filter("sisi.id", sistema);
        return all(sqlUtil);
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

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("me")
                .parents("sistema sisi")
                .parents("left _me.menuSuperior ms", "left _ms.menuSuperior mms", "left _mms.menuSuperior")
                .filter("sisi.id", sistema.getId())
                .filter("me.tipo", menuTipoEnum.name())
                .orderBy("me.orden asc");
        return all(sqlUtil);
    }

    @Override
    public List<Menu> allBySuperMenu(Sistema sistema, Menu menuSuperior) {

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("me")
                .parents("sistema sisi")
                .parents("left _me.menuSuperior ms", "left _ms.menuSuperior mms", "left _mms.menuSuperior")
                .filter("sisi.id", sistema.getId())
                .filter("ms.id", menuSuperior.getId())
                .orderBy("me.orden asc");
        return all(sqlUtil);
    }

    @Override
    public Menu findByTipoOrden(MenuTipoEnum menuTipoEnum, Sistema sistema, Integer orden) {

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("me")
                .parents("sistema sisi")
                .parents("left _me.menuSuperior ms", "left _ms.menuSuperior mms", "left _mms.menuSuperior")
                .filter("sisi.id", sistema.getId())
                .filter("me.orden", orden)
                .filter("me.tipo", menuTipoEnum.name());
        return find(sqlUtil);
    }

    @Override
    public Menu findBySuperMenuOrden(Sistema sistema, Menu menuSuperior, Integer orden) {

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("me")
                .parents("sistema sisi")
                .parents("left _me.menuSuperior ms", "left _ms.menuSuperior mms", "left _mms.menuSuperior")
                .filter("sisi.id", sistema.getId())
                .filter("me.orden", orden)
                .filter("ms.id", menuSuperior.getId());
        return find(sqlUtil);
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
        return (Menu) sql.find(getCurrentSession());
    }

}
