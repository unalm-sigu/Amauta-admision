package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;

@Repository
public class CursoCicloAcademicoDAOH extends AbstractEasyDAO<CursoCicloAcademico> implements CursoCicloAcademicoDAO {

    public CursoCicloAcademicoDAOH() {
        super();
        this.setClazz(CursoCicloAcademico.class);
    }

    
    @Override
    public List<CursoCicloAcademico> allByCiclo(CicloAcademico cicloDestino) {
        Octavia sql = Octavia.query(CursoCicloAcademico.class, "cca")
                .join("curso c", "cicloAcademico ca")
                .filter("ca.id", cicloDestino);
        
        return all(sql);
    }
    
}
