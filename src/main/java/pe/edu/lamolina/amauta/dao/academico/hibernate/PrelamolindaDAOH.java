package pe.edu.lamolina.amauta.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.academico.PrelamolinaDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.inscripcion.Prelamolina;

@Repository
public class PrelamolindaDAOH extends AbstractEasyDAO<Prelamolina> implements PrelamolinaDAO {

    public PrelamolindaDAOH() {
        super();
        setClazz(Prelamolina.class);
    }

    @Override
    public List<Prelamolina> allInscritosByCicloAcademico(CicloAcademico cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(Prelamolina.class, "pre")
                .join("cicloPostula ci", "ci.cicloAcademico ca", "postulante po")
                .filter("ca.id", cicloAcademico)
                .filter("estado", PostulanteEstadoEnum.INS.name());
        return sql.all(getCurrentSession());
    }

}
