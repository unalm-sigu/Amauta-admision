package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.pivot.dao.academico.PrecioCursoEstructuraDAO;

@Repository
public class PrecioCursoEstructuraDAOH extends AbstractEasyDAO<PrecioCursoEstructura> implements PrecioCursoEstructuraDAO {

    public PrecioCursoEstructuraDAOH() {
        super();
        setClazz(PrecioCursoEstructura.class);
    }
 
    @Override
    public List<PrecioCursoEstructura> allByCiclo(CicloAcademico cicloDestino) {
         Octavia sql = Octavia.query(PrecioCursoEstructura.class, "cca")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloDestino);
        
        return all(sql);
    }
    
}
