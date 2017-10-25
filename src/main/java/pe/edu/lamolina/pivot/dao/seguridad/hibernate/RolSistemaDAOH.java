package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.pivot.dao.seguridad.RolSistemaDAO;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.RolSistema;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;
import pe.albatross.octavia.Octavia;

@Repository
public class RolSistemaDAOH extends AbstractDAO<RolSistema> implements RolSistemaDAO {

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

        return (RolSistema) sql.find(getCurrentSession());
    }

}
