package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoExcluidoCargaAdicional;
import pe.edu.lamolina.amauta.dao.academico.CursoExcluidoCargaAdicionalDAO;

@Repository
public class CursoExcluidoCargaAdicionalDAOH extends AbstractEasyDAO<CursoExcluidoCargaAdicional> implements CursoExcluidoCargaAdicionalDAO {

    public CursoExcluidoCargaAdicionalDAOH() {
        super();
        setClazz(CursoExcluidoCargaAdicional.class);
    }

    @Override
    public List<CursoExcluidoCargaAdicional> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(CursoExcluidoCargaAdicional.class, "ce")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        
        return all(sql);
    }

}
