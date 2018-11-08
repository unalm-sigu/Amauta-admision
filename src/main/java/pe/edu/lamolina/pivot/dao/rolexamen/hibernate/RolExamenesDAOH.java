package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;

@Repository
public class RolExamenesDAOH extends AbstractEasyDAO<RolExamenes> implements RolExamenesDAO {

    public RolExamenesDAOH() {
        super();
        setClazz(RolExamenes.class);
    }

    @Override
    public List<RolExamenes> allActiveByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(RolExamenes.class, "sec")
                .join("cicloAcademico ca", "eventoCicloAcademico eca", "userRegistro ur")
                .join("eca.eventoAcademico ea")
                .filter("ca.id", cicloAcademico);
        return all(sql);
    }

}
