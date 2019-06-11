package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.tramite.Resolucion;

public interface ResolucionDAO extends EasyDAO<Resolucion> {

    Resolucion find();

    List<Resolucion> allByDyna(DynatableFilter filter);

    void updateResolucion(Resolucion resolucion);

    void updateResolucionFile(Resolucion resolucion);

    void updateEstado(Resolucion resolucion);

    void updateEstadoCicloRei(Resolucion resolucion);

    Resolucion findById(Long resolucion);

    List<Resolucion> allByNombre(String nombre);
}
