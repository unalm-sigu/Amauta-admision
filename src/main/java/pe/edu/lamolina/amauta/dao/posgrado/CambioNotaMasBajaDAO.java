package pe.edu.lamolina.amauta.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.tramite.CambioNotaMasBaja;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface CambioNotaMasBajaDAO extends EasyDAO<CambioNotaMasBaja> {

    public List<CambioNotaMasBaja> allByTramite(List<Tramite> tramites);

    public List<CambioNotaMasBaja> allByEstadoTramite(EstadoTramite estadoSolicitado);

    public List<CambioNotaMasBaja> allByAlumnosEstadoTramite(List<Alumno> alumnos, EstadoTramite estadoAgendado);

    public void updateColumns(CambioNotaMasBaja cambioNotaMasBaja, String... columns);

    public List<CambioNotaMasBaja> allByResolucion(Resolucion resolucion);

    public List<CambioNotaMasBaja> allPendienteByAlumnoCurso(Alumno alumno, Curso curso);

    CambioNotaMasBaja findByTramite(Tramite tramite);

}
