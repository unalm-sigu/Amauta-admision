package pe.edu.lamolina.amauta.dao.auditoria.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.auditoria.ClonarCicloNivelacionDAO;
import pe.edu.lamolina.model.auditoria.ClonarCicloNivelacion;

@Repository
public class ClonarCicloNivelacionDAOH extends AbstractEasyDAO<ClonarCicloNivelacion> implements ClonarCicloNivelacionDAO {

    public ClonarCicloNivelacionDAOH() {
        super();
        setClazz(ClonarCicloNivelacion.class);
    }

}
