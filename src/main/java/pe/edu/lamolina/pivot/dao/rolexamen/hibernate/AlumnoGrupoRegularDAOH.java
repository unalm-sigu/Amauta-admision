package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;

public class AlumnoGrupoRegularDAOH extends AbstractEasyDAO<AlumnoGrupoRegular> implements AlumnoGrupoRegularDAO {

    public AlumnoGrupoRegularDAOH() {
        super();
        setClazz(AlumnoGrupoRegular.class);
    }
}
