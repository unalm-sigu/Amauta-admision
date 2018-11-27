package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;

@Repository
public class AlumnoGrupoEspecialDAOH extends AbstractEasyDAO<AlumnoGrupoEspecial> implements AlumnoGrupoEspecialDAO {

    public AlumnoGrupoEspecialDAOH() {
        super();
        setClazz(AlumnoGrupoEspecial.class);
    }

}
