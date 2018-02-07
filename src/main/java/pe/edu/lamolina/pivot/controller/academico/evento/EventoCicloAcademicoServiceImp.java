package pe.edu.lamolina.pivot.controller.academico.evento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.calendar.EventCalendar;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EventoCicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.general.Color;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.EventoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.ColorDAO;

@Service
@Transactional(readOnly = true)
public class EventoCicloAcademicoServiceImp implements EventoCicloAcademicoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    EventoAcademicoDAO eventoAcademicoDAO;

    @Autowired
    ColorDAO colorDAO;

    @Override
    @Transactional
    public void delete(EventoCicloAcademico eventoCicloAcademico) {
        eventoCicloAcademicoDAO.delete(eventoCicloAcademico);
    }

    @Override
    public List<EventoCicloAcademico> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return eventoCicloAcademicoDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    public EventoCicloAcademico findEventoCicloAcademico(EventoCicloAcademico eventoCicloAcademico) {
        return eventoCicloAcademicoDAO.findEventoCicloAcademico(eventoCicloAcademico);
    }

    @Override
    @Transactional
    public void save(EventoCicloAcademico eventoCicloAcademico, Usuario usuario, CicloAcademico cicloAcademico) {
        ObjectUtil.eliminarAttrSinId(eventoCicloAcademico, "cicloAcademico");
        ObjectUtil.eliminarAttrSinId(eventoCicloAcademico, "eventoAcademico");
        eventoCicloAcademico.setCicloAcademico(cicloAcademico);
        if (eventoCicloAcademico.getEventoAcademico() == null) {
            throw new PhobosException("Tiene que especificar el evento académico.");
        }
        eventoCicloAcademico.setEstado(EventoCicloAcademicoEstadoEnum.CRE.name());
        eventoCicloAcademico.setFechaRegistro(new Date());
        eventoCicloAcademico.setUserRegistro(usuario);
        Color color = colorDAO.findLastColor();
        eventoCicloAcademico.setColor(color);
        eventoCicloAcademicoDAO.save(eventoCicloAcademico);
        color.setOrdenEvento(color.getOrdenEvento() + 1);
        colorDAO.update(color);
    }

    @Override
    @Transactional
    public void update(EventoCicloAcademico eventoCicloAcademico, Usuario usuario, CicloAcademico cicloAcademico) {
        ObjectUtil.eliminarAttrSinId(eventoCicloAcademico, "cicloAcademico");
        ObjectUtil.eliminarAttrSinId(eventoCicloAcademico, "eventoAcademico");
        if (eventoCicloAcademico.getEventoAcademico() == null) {
            throw new PhobosException("Tiene que especificar el evento académico.");
        }
        EventoCicloAcademico eventoCicloAcademicoDB = eventoCicloAcademicoDAO.findEventoCicloAcademico(eventoCicloAcademico);
        eventoCicloAcademicoDB.setEstado(EventoCicloAcademicoEstadoEnum.CRE.name());
        eventoCicloAcademicoDB.setFechaRegistro(new Date());
        eventoCicloAcademicoDB.setUserRegistro(usuario);
        eventoCicloAcademicoDB.setEventoAcademico(eventoCicloAcademico.getEventoAcademico());
        eventoCicloAcademicoDAO.update(eventoCicloAcademicoDB);
    }

    @Override
    public List<EventoAcademico> allEventoAcademicoByName(String nombre) {
        return eventoAcademicoDAO.allEventoAcademicoByName(this.forLike(nombre));
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    public List<EventCalendar> allcalendar(CicloAcademico ciclo) {
        List<EventCalendar> eventoss = new ArrayList<>();
        List<EventoCicloAcademico> eventos = eventoCicloAcademicoDAO.allcalendar(ciclo);
        for (EventoCicloAcademico evento : eventos) {
            EventCalendar eventCalendar = new EventCalendar();
            eventCalendar.setTitle(evento.getEventoAcademico().getNombre());
            String jFechaInicio = evento.getFechaInicio() != null ? new DateTime(evento.getFechaInicio()).toString("yyyy-MM-dd") : "";
            eventCalendar.setStart(jFechaInicio);
            String jFechaFin = evento.getFechaFin() != null ? new DateTime(evento.getFechaFin()).toString("yyyy-MM-dd") : "";
            eventCalendar.setEnd(jFechaFin);
            eventCalendar.setColor(evento.getColor().getCodigo());
            eventoss.add(eventCalendar);
        }
        return eventoss;
    }

}
