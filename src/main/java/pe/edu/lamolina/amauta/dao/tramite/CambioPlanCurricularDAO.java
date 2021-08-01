package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.tramite.CambioPlanCurricular;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface CambioPlanCurricularDAO extends EasyDAO<CambioPlanCurricular> {

    List<CambioPlanCurricular> allByTramite(Tramite tramite);

    List<CambioPlanCurricular> allByDyna(DynatableFilter filter);

    List<CambioPlanCurricular> allByResolucion(Resolucion resolucion);

    CambioPlanCurricular findByTramiteEstadoTram(Tramite tramite, TramiteEstadoEnum estadoTramiteEnum);

    void updateEstado(CambioPlanCurricular cambioPlanEstudios);

    void updateAceptado(CambioPlanCurricular cambioPlanEstudios);

    List<CambioPlanCurricular> allAceptadasByAlumnoSinCiclo(Alumno alumno, CicloAcademico ciclo);

    List<CambioPlanCurricular> allAceptadasPendientesByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<CambioPlanCurricular> allAceptadosByAlumnosSinCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    List<CambioPlanCurricular> allAceptadasPendientesByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    CambioPlanCurricular findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<CambioPlanCurricular> allByCicloReincorporacion(CicloAcademico cicloAcademico);

    List<CambioPlanCurricular> allByTramitesCondicional(CicloAcademico cicloAcademico);

    List<CambioPlanCurricular> allByTramite(List<Tramite> tramite);

    List<CambioPlanCurricular> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<CambioPlanCurricular> allPendientesByCicloReincorporacion();

    List<CambioPlanCurricular> allByCicloReincorporacionByEstado(CicloAcademico ciclo, TramiteEstadoEnum tramiteEstadoEnum);

    void updateColumns(CambioPlanCurricular cambioPlanEstudios, String... columns);

    CambioPlanCurricular find(Long cambioPlanEstudios);

    public List<CambioPlanCurricular> allPendientes();

}
