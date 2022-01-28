package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.amauta.controller.consejeria.administracion.view.FiltroReporteAgendaDTO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.AgendaConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.consejeria.ReunionAlumnoConsejero;

public interface ReunionAlumnoConsejeroDAO extends EasyDAO<ReunionAlumnoConsejero> {

    int saveList(List<ReunionAlumnoConsejero> reunionAlumnoConsejeros);

    public List<ReunionAlumnoConsejero> allByAgendaConsejero(AgendaConsejero agendaConsejeroForm);

    public List<ReunionAlumnoConsejero> allDynatableByConsejero(DynatableFilter filter, Consejero consejero, CicloAcademico cicloAcademico);

    public void deleteByCiclo(CicloAcademico cicloAcademico);

    public List<ReunionAlumnoConsejero> allDynatableByCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico);

    public List<ReunionAlumnoConsejero> allReunionAlumnoConsejeroReporte( FiltroReporteAgendaDTO filtroReporteAgendaDTO,CicloAcademico cicloAcademico);
}
