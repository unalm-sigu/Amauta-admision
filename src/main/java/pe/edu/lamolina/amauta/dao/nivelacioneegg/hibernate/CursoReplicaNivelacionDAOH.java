package pe.edu.lamolina.amauta.dao.nivelacioneegg.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoReplicaNivelacionDAO;
import pe.edu.lamolina.model.nivelacioneegg.CursoReplicaNivelacion;

@Repository
public class CursoReplicaNivelacionDAOH extends AbstractEasyDAO<CursoReplicaNivelacion> implements CursoReplicaNivelacionDAO {

    public CursoReplicaNivelacionDAOH() {
        super();
        setClazz(CursoReplicaNivelacion.class);
    }

}
