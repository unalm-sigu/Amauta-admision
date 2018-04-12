package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;

public interface EventoCicloAcademicoDAO extends EasyDAO<EventoCicloAcademico> {

    List<EventoCicloAcademico> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    EventoCicloAcademico findEventoCicloAcademico(EventoCicloAcademico eventoCicloAcademico);

    List<EventoCicloAcademico> allcalendar(CicloAcademico ciclo);

    public List<EventoCicloAcademico> allEventosMatriculaByCiclo(CicloAcademico cicloAcademico);

    List<EventoCicloAcademico> allActivosByCicloEventos(CicloAcademico cicloAcademico, List<EventoAcademicoEnum> eventoAcademicos);

    List<EventoCicloAcademico> allEventoAcademicoByCicloAca(CicloAcademico cicloAcademico);

    EventoCicloAcademico findActivoByCicloTipoEvento(CicloAcademico cicloAcademico, EventoAcademicoEnum eventoAcademicoEnum);
}
