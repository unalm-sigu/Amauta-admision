package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

public interface TramiteBachillerDAO extends EasyDAO<TramiteBachiller> {

    public TramiteBachiller findByTramite(Tramite tramite);

    public List<TramiteBachiller> allByTramites(List<Tramite> tramites);

    public TramiteBachiller findByAlumnoAct(Alumno alumno);

    public List<TramiteBachiller> allByResolucion(Resolucion resolucionDB);

    public List<TramiteBachiller> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

}
