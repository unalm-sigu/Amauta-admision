package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TramiteRenunciaAlumno;

public interface TramiteRenunciaAlumnoDAO extends EasyDAO<TramiteRenunciaAlumno> {

    public List<TramiteRenunciaAlumno> allByRenunciaAlumno(Resolucion resolucion);

    public List<TramiteRenunciaAlumno> allByRenunciaAlumnoEditar(Resolucion resolucion);

    public TramiteRenunciaAlumno findByAlumnoAct(Alumno alumno);

    public List<TramiteRenunciaAlumno> allByDynatable(DynatableFilter filter);

    public List<TramiteRenunciaAlumno> allBySolicitados();

}
