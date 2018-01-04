package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.seguridad.SistemaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.seguridad.MenuRol;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;

@Repository
public class SistemaDAOH extends AbstractEasyDAO<Sistema> implements SistemaDAO {

    public SistemaDAOH() {
        super();
        setClazz(Sistema.class);
    }

    @Override
    public Sistema findByRolSistema(Rol rol, Sistema sys) {
        Octavia sql = Octavia.query()
                .selectDistinct("sys")
                .from(MenuRol.class, "mr")
                .join("rol rol", "menu me", "me.sistema sys")
                .filter("rol.id", rol)
                .filter("sys.id", sys);

        return find(sql);
    }

}
