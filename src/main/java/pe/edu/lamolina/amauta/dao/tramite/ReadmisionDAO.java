package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Readmision;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface ReadmisionDAO extends EasyDAO<Readmision> {

    Readmision find(Long readmision);

    List<Readmision> allByTramite(Tramite tramite);

    List<Readmision> allByResolucion(Resolucion resolucion);

    Readmision findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<Readmision> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<Readmision> allPendientesByCicloReadmision();

    List<Readmision> allPendientes();

    Readmision findByEstadoTramiteAlumnoCiclo(Alumno alumno, CicloAcademico cicloReadmitido, EstadoTramite estadoTramite);

    public List<Readmision> allPendienteByEstado(EstadoTramite estadoTramite);

}
