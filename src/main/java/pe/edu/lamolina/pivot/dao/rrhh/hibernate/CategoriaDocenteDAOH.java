package pe.edu.lamolina.pivot.dao.rrhh.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rrhh.CategoriaDocente;
import pe.edu.lamolina.pivot.dao.rrhh.CategoriaDocenteDAO;

@Repository
public class CategoriaDocenteDAOH extends AbstractEasyDAO<CategoriaDocente> implements CategoriaDocenteDAO {

    public CategoriaDocenteDAOH() {
        super();
        setClazz(CategoriaDocente.class);
    }

}
