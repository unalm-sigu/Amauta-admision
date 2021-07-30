package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.tramite.Readmision;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface ReadmisionDAO extends EasyDAO<Readmision> {

    List<Readmision> allByTramite(Tramite tramite);

    List<Readmision> allByDyna(DynatableFilter filter);

    List<Readmision> allByResolucion(Resolucion resolucion);

    Readmision findByTramiteEstadoTram(Tramite tramite, TramiteEstadoEnum estadoTramiteEnum);

    void updateEstado(Readmision readmision);

    void updateAceptado(Readmision readmision);

    List<Readmision> allAceptadasByAlumnoSinCiclo(Alumno alumno, CicloAcademico ciclo);

    List<Readmision> allAceptadasPendientesByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<Readmision> allAceptadosByAlumnosSinCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    List<Readmision> allAceptadasPendientesByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    Readmision findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<Readmision> allByCicloReincorporacion(CicloAcademico cicloAcademico);

    List<Readmision> allByTramitesCondicional(CicloAcademico cicloAcademico);

    List<Readmision> allByTramite(List<Tramite> tramite);

    List<Readmision> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<Readmision> allPendientesByCicloReincorporacion();

    List<Readmision> allByCicloReincorporacionByEstado(CicloAcademico ciclo, TramiteEstadoEnum tramiteEstadoEnum);

    void updateColumns(Readmision readmision, String... columns);

    Readmision find(Long readmision);

}
