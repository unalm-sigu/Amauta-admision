package pe.edu.lamolina.amauta.controller.consejeria.administracion;

import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.ClonarConsejerosDTO;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.FiltroReporteAgendaDTO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;
import pe.edu.lamolina.model.general.Colaborador;

public interface AdministracionConsejeriaService {

    List<ConsejeriaHistorial> allConsejeriaHistorialByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<CicloAcademico> allCiclo();

    void clonar(ClonarConsejerosDTO clonarDTO, DataSessionPivot ds);

    void eliminar(Long idConsejeriaHistorial, DataSessionPivot ds);

    List<AgendaConsejero> agendaDynatable(DynatableFilter filter);

    List<Carrera> buscarCarrera(String nombre);

    List<Consejero> buscarConsejero(String nombre);

    List<ReunionAlumnoConsejero> allReunionAlumnoConsejeroReporte(FiltroReporteAgendaDTO filtroReporteAgendaDTO);

    List<Alumno> buscarAlumno(String nombre);

    List<Colaborador> coordinadores(DynatableFilter filter);

    void actualizarEstudiantes(DataSessionPivot ds);

    List<AgendaConsejero> agendaDynatableCarrera(DynatableFilter filter, Long idCarreraSupervisor);

}
