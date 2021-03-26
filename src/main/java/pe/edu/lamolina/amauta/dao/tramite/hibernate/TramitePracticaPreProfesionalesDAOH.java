package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.amauta.dao.tramite.TramitePracticaPreProfesionalesDAO;
import pe.edu.lamolina.model.tramite.PracticasPreProfesional;
import pe.edu.lamolina.model.tramite.Resolucion;

@Repository
public class TramitePracticaPreProfesionalesDAOH extends AbstractEasyDAO<PracticasPreProfesional> implements TramitePracticaPreProfesionalesDAO {

    public TramitePracticaPreProfesionalesDAOH() {
        super();
        setClazz(PracticasPreProfesional.class);
    }

    @Override
    public List<PracticasPreProfesional> allByResolucion(Resolucion resolucionDB) {
        Octavia sql = new Octavia()
                .from(PracticasPreProfesional.class, "ppf")
                .join("resolucion re", "alumno", "curso")
                .filter("re.id", resolucionDB);

        return all(sql);
    }
}
