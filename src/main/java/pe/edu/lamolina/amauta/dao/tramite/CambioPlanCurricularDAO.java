package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.tramite.CambioPlanCurricular;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.Tramite;

public interface CambioPlanCurricularDAO extends EasyDAO<CambioPlanCurricular> {

    List<CambioPlanCurricular> allByResolucion(Resolucion resolucion);

    CambioPlanCurricular findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<CambioPlanCurricular> allByDynatableCiclo(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<CambioPlanCurricular> allPendientesByCicloAcademico();

    CambioPlanCurricular find(Long cambioPlanEstudios);

    List<CambioPlanCurricular> allPendientes();

    CambioPlanCurricular findByEstadoTramiteAlumnoCiclo(Alumno alumnoDB, CicloAcademico cicloAcademico, EstadoTramite estadoTramite);

    List<CambioPlanCurricular> allByTramite(Tramite tramite);

    public List<CambioPlanCurricular> allPendienteByEstado(EstadoTramite estadoTramite);

}
