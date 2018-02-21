package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;

public interface EventoCicloAcademicoDAO extends EasyDAO<EventoCicloAcademico> {

    public List<EventoCicloAcademico> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    public EventoCicloAcademico findEventoCicloAcademico(EventoCicloAcademico eventoCicloAcademico);

    public List<EventoCicloAcademico> allcalendar(CicloAcademico ciclo);

    public List<EventoCicloAcademico> allEventoAcademicoByCicloAca(CicloAcademico cicloAcademico);

    List<EventoCicloAcademico> allActivosByCicloEventos(CicloAcademico cicloAcademico, List<EventoAcademicoEnum> eventoAcademicos);
}
