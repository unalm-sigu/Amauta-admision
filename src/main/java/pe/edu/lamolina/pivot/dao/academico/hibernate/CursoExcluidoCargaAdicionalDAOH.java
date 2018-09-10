package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CursoExcluidoCargaAdicional;
import pe.edu.lamolina.pivot.dao.academico.CursoExcluidoCargaAdicionalDAO;

@Repository
public class CursoExcluidoCargaAdicionalDAOH extends AbstractEasyDAO<CursoExcluidoCargaAdicional> implements CursoExcluidoCargaAdicionalDAO {

    public CursoExcluidoCargaAdicionalDAOH() {
        super();
        setClazz(CursoExcluidoCargaAdicional.class);
    }

}
