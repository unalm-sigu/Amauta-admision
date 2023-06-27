package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

public interface TramiteTituloDAO extends EasyDAO<TramiteTitulo> {

    public TramiteTitulo findByTramite(Tramite tramite);

    public List<TramiteTitulo> allByTramites(List<Tramite> tramites);

    public TramiteTitulo findByAlumnoAct(Alumno alumno);

    public List<TramiteTitulo> allByResolucion(Resolucion resolucionDB);

    public List<TramiteTitulo> allByDynatable(DynatableFilter filter);

    TramiteTitulo findByAlumnoACEP(Alumno alumno);
    
    TramiteTitulo findByAlumnoFacultadACEP(Alumno alumno);

    public List<TramiteTitulo> allBySolicitados();

    public List<TramiteTitulo> allBySolicitadosFacultad();
    
    public List<TramiteTitulo> allByTituloFacultad(Resolucion resolucion);

}
