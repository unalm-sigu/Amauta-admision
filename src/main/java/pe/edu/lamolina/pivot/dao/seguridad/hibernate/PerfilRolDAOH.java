package pe.edu.lamolina.pivot.dao.seguridad.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.pivot.dao.seguridad.PerfilRolDAO;
import pe.edu.lamolina.pivot.model.general.PerfilCompania;
import pe.edu.lamolina.pivot.model.seguridad.PerfilRol;

@Repository
public class PerfilRolDAOH extends AbstractDAO<PerfilRol> implements PerfilRolDAO {

    public PerfilRolDAOH() {
        super();
        setClazz(PerfilRol.class);
    }

    @Override
    public List<PerfilRol> allByPerfilCompania(PerfilCompania perfilCompania) {

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("pr")
                .parents("rol r", "perfil p")
                .filter("p.id", perfilCompania);
        
        return this.all(sqlUtil);

    }

}
