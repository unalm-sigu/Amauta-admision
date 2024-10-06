package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoTemaExamenDAO;
import pe.edu.lamolina.model.nivelacioneegg.CursoTemaExamen;

@Repository
public class CursoTemaExamenDAOH extends AbstractEasyDAO<CursoTemaExamen> implements CursoTemaExamenDAO {

    public CursoTemaExamenDAOH() {
        super();
        setClazz(CursoTemaExamen.class);
    }

}
