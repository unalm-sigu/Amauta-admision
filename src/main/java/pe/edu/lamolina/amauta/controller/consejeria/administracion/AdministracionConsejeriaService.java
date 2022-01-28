package pe.edu.lamolina.amauta.controller.consejeria.administracion;

import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.ClonarConsejerosDTO;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.FiltroReporteAgendaDTO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;

public interface AdministracionConsejeriaService {

    public List<ConsejeriaHistorial> allConsejeriaHistorialByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    public List<CicloAcademico> allCiclo();

    public void clonar(ClonarConsejerosDTO clonarDTO, DataSessionPivot ds);

    public void eliminar(Long idConsejeriaHistorial, DataSessionPivot ds);

    public List<ReunionAlumnoConsejero> agendaDynatable(DynatableFilter filter, DataSessionPivot ds);

    public void verificarVencimiento(List<AgendaConsejero> agendaConsejeros);

    public List<Carrera> buscarCarrera(String nombre);

    public List<Consejero> buscarConsejero(String nombre);

    public List<ReunionAlumnoConsejero> allReunionAlumnoConsejeroReporte(FiltroReporteAgendaDTO filtroReporteAgendaDTO, CicloAcademico cicloAcademico);

}
