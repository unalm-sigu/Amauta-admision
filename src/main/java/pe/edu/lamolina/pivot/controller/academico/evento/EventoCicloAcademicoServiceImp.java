package pe.edu.lamolina.pivot.controller.academico.evento;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EventoCicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.EventoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;

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
        eventoCicloAcademicoDAO.save(eventoCicloAcademico);
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

}
