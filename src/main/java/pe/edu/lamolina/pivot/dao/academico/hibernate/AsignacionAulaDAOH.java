package pe.edu.lamolina.pivot.dao.academico.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.AsignacionAula;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.dao.academico.AsignacionAulaDAO;

@Repository
public class AsignacionAulaDAOH extends AbstractEasyDAO<AsignacionAula> implements AsignacionAulaDAO {

    public AsignacionAulaDAOH() {
        super();
        setClazz(AsignacionAula.class);
    }

    @Override
    public AsignacionAula findByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AsignacionAula.class, "sec")
                .join("cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

}
