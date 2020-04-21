package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface CambioNotaDAO extends EasyDAO<CambioNota> {

    public CambioNota findByTramite(Tramite tramiteForm);

    public List<CambioNota> allByTramites(List<Tramite> tramites);

    public List<CambioNota> allByResolucion(Resolucion resolucionDB);

    public List<CambioNota> allByCicloRegistro(CicloAcademico ciclo);

}
