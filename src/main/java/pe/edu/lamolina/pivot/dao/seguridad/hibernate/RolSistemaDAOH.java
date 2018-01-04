package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.seguridad.RolSistemaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.RolSistema;
import pe.edu.lamolina.model.seguridad.Sistema;

@Repository
public class RolSistemaDAOH extends AbstractEasyDAO<RolSistema> implements RolSistemaDAO {

    public RolSistemaDAOH() {
        super();
        setClazz(RolSistema.class);
    }

    @Override
    public RolSistema findByRolSistema(Rol rol, Sistema sistema) {
        Octavia sql = Octavia.query()
                .from(RolSistema.class, "rm")
                .join("rol rol", "sistema s")
                .filter("rol.id", rol)
                .filter("s.id", sistema);

        return find(sql);
    }

}
