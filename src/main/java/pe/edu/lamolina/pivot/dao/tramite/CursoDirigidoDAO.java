package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface CursoDirigidoDAO extends EasyDAO<CursoDirigido> {

    CursoDirigido findByTramite(Tramite tramite);

    List<CursoDirigido> allByfacultades(DynatableFilter filters, Docente docente);

    public void updateEstado(CursoDirigido cursoDirigido);

    public List<CursoDirigido> allByTramites(List<Tramite> tramites);

    public List<CursoDirigido> allByResolucion(DynatableFilter filter, Resolucion resolucion);

    public List<CursoDirigido> allByResolucion(Resolucion resolucion);
}
