package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.seguridad.PerfilRol;
import pe.edu.lamolina.pivot.dao.seguridad.PerfilRolDAO;

@Repository
public class PerfilRolDAOH extends AbstractEasyDAO<PerfilRol> implements PerfilRolDAO {

    public PerfilRolDAOH() {
        super();
        setClazz(PerfilRol.class);
    }

    @Override
    public List<PerfilRol> allByPerfilCompania(PerfilCompania perfilCompania) {

        Octavia sql = Octavia.query()
                .from(PerfilRol.class, "pr")
                .join("rol r", "perfil p")
                .filter("p.id", perfilCompania);

        return all(sql);

    }

}
