package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

public interface TramiteBachillerDAO extends EasyDAO<TramiteBachiller> {

    TramiteBachiller findByTramite(Tramite tramite);

    List<TramiteBachiller> allByTramites(List<Tramite> tramites);

    TramiteBachiller findByAlumnoAct(Alumno alumno);

    List<TramiteBachiller> allByResolucion(Resolucion resolucionDB);

    List<TramiteBachiller> allByDynatable(DynatableFilter filter);

    TramiteBachiller findByAlumnoACEP(Alumno alumno);

    List<TramiteBachiller> allBySolicitados();

    List<TramiteBachiller> allByAlumnosAct(List<Alumno> alumnos);

    public List<TramiteBachiller> allBySolicitadosFacultad(Resolucion resolucion);

    public TramiteBachiller findByAlumnoFacultadACEP(Alumno alumno);
}
