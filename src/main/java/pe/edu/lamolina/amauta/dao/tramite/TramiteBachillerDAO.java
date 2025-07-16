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
    TramiteBachiller findByResolucionCU(Resolucion resolucion);
    TramiteBachiller findByResolucionFacultad(Resolucion resolucion);

    List<TramiteBachiller> allByTramites(List<Tramite> tramites);

    TramiteBachiller findByAlumnoAct(Alumno alumno);

    TramiteBachiller findByAlumnoActFacultad(Alumno alumno);

    List<TramiteBachiller> allByResolucion(Resolucion resolucionDB);

    List<TramiteBachiller> allByResolucionFacultad(Resolucion resolucionDB);

    List<TramiteBachiller> allByDynatable(DynatableFilter filter);

    TramiteBachiller findByAlumnoACEP(Alumno alumno);

    List<TramiteBachiller> allBySolicitados();

    List<TramiteBachiller> allByAlumnosAct(List<Alumno> alumnos);

    public List<TramiteBachiller> allBySolicitadosFacultad(Resolucion resolucion);

    List<TramiteBachiller> allByFacultadSolicitados();

    public TramiteBachiller findByAlumnoFacultadACEP(Alumno alumno);
}
