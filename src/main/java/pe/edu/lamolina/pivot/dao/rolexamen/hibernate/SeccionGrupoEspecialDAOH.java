package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;

@Repository
public class SeccionGrupoEspecialDAOH extends AbstractEasyDAO<SeccionGrupoEspecial> implements SeccionGrupoEspecialDAO {

    public SeccionGrupoEspecialDAOH() {
        super();
        setClazz(SeccionGrupoEspecial.class);
    }

}
