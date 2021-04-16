package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.PracticasPreProfesional;
import pe.edu.lamolina.model.tramite.Resolucion;

public interface TramitePracticaPreProfesionalesDAO extends EasyDAO<PracticasPreProfesional> {

    public List<PracticasPreProfesional> allByResolucion(Resolucion resolucionDB);

    public List<PracticasPreProfesional> allBySolicitados();

}
