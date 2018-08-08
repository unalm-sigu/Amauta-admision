package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.tramite.AlumnoReunionConsejo;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.pivot.dao.tramite.AlumnoReunionConsejoDAO;

@Repository
public class AlumnoReunionConsejoDAOH extends AbstractEasyDAO<AlumnoReunionConsejo> implements AlumnoReunionConsejoDAO {

    @Override
    public List<AlumnoReunionConsejo> allByReunionConsejo(ReunionConsejo reunionConsejo) {
        Octavia sql = Octavia.query()
                .from(AlumnoReunionConsejo.class, "rc")
                .join("reunionConsejo rc")
                .filter("rc.id", reunionConsejo)
                .filter("rc.estado", EstadoEnum.ACT);
        return all(sql);
    }

}
