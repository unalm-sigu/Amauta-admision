package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

public interface TramiteTrasladoDAO extends EasyDAO<TramiteTraslado> {

    List<TramiteTraslado> allByResolucion(Resolucion resolucion);

    List<TramiteTraslado> allByAlumno(Alumno alumno);

    public List<TramiteTraslado> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico);

    public TramiteTraslado findByAlumnoCiclo(Alumno alumnoDB, CicloAcademico cicloAcademico);

    public TramiteTraslado findByTramite(Tramite tramite);

    public List<TramiteTraslado> findByCiclo(CicloAcademico cicloAcademico);
}
