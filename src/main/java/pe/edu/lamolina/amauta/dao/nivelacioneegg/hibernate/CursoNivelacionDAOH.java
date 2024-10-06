package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Repository
public class CursoNivelacionDAOH extends AbstractEasyDAO<CursoNivelacion> implements CursoNivelacionDAO {

    public CursoNivelacionDAOH() {
        super();
        setClazz(CursoNivelacion.class);
    }

}
