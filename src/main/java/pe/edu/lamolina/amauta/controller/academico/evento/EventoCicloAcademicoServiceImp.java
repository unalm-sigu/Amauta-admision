package pe.edu.lamolina.amauta.controller.academico.evento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.calendar.EventCalendar;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EventoCicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.general.Color;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.academico.EventoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.general.ColorDAO;

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
        Assert.isNotNull(eventoCicloAcademico.getEventoAcademico(), "Tiene que especificar el evento académico.");

        Color color = colorDAO.findLastColor();
        eventoCicloAcademico.setColor(color);
        eventoCicloAcademico.setEstado(EventoCicloAcademicoEstadoEnum.CRE.name());
        eventoCicloAcademico.setFechaRegistro(new Date());
        eventoCicloAcademico.setUserRegistro(usuario);

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
        eventoCicloAcademicoDB.setEventoAcademico(eventoCicloAcademico.getEventoAcademico());
        eventoCicloAcademicoDB.setFechaFin(eventoCicloAcademico.getFechaFin());
        eventoCicloAcademicoDB.setFechaInicio(eventoCicloAcademico.getFechaInicio());
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
            eventCalendar.setStart(TypesUtil.getStringDate(evento.getFechaInicio(), "yyyy-MM-dd"));
            eventCalendar.setEnd(TypesUtil.getStringDate(evento.getFechaFin(), "yyyy-MM-dd"));
            eventCalendar.setColor(evento.getColor().getCodigo());
            eventoss.add(eventCalendar);
        }
        return eventoss;
    }

}
