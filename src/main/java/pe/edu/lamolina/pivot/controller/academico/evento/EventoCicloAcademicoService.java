package pe.edu.lamolina.pivot.controller.academico.evento;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.calendar.EventCalendar;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface EventoCicloAcademicoService {

    List<EventoCicloAcademico> allByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    EventoCicloAcademico findEventoCicloAcademico(EventoCicloAcademico eventoCicloAcademico);

    void save(EventoCicloAcademico eventoCicloAcademico, Usuario usuario, CicloAcademico cicloAcademico);

    void update(EventoCicloAcademico eventoCicloAcademico, Usuario usuario, CicloAcademico cicloAcademico);

    void delete(EventoCicloAcademico eventoCicloAcademico);

    List<EventoAcademico> allEventoAcademicoByName(String nombre);

    List<EventCalendar> allcalendar(CicloAcademico ciclo);

}
