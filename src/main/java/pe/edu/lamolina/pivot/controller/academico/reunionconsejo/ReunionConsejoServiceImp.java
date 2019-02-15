package pe.edu.lamolina.pivot.controller.academico.reunionconsejo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.calendar.EventCalendar;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReunionConsejoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.tramite.TramiteReunionConsejoDAO;

@Service
@Transactional(readOnly = true)
public class ReunionConsejoServiceImp implements ReunionConsejoService {

    @Autowired
    ReunionConsejoDAO reunionConsejoDAO;

    @Autowired
    TramiteReunionConsejoDAO alumnoReunionConsejoDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Override
    public ReunionConsejo findReunionConsejoByFechaAndOficina(Date fecha, Oficina oficina) {
        return reunionConsejoDAO.findByFechaAndOficina(fecha, oficina);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveReunionConsejo(ReunionConsejo reunionConsejo, Oficina oficina, DataSessionPivot ds) {
        DateTime today = new DateTime();
        reunionConsejo.setUsuarioRegistro(ds.getUsuario());
        reunionConsejo.setFechaRegistro(today.toDate());
        reunionConsejo.setUsuarioActualizacion(ds.getUsuario());
        reunionConsejo.setFechaActualizacion(today.toDate());
//        reunionConsejo.setOficina(oficina);
        reunionConsejoDAO.save(reunionConsejo);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateReunionConsejo(ReunionConsejo reunionConsejo, DataSessionPivot ds) {
        ReunionConsejo validateReunionConsejo = reunionConsejoDAO.findByFechaAndOficina(reunionConsejo.getFecha(), reunionConsejo.getOficina());

        if (validateReunionConsejo != null) {
            throw new PhobosException("El día %s no esta disponible.", TypesUtil.getStringDate(validateReunionConsejo.getFecha(), "dd/MM/yyyy"));
        }

        DateTime today = new DateTime();
        reunionConsejo.setUsuarioActualizacion(ds.getUsuario());
        reunionConsejo.setFechaActualizacion(today.toDate());
        reunionConsejoDAO.update(reunionConsejo);
    }

    @Override
    public List<EventCalendar> allcalendar(CicloAcademico ciclo, List<Oficina> oficinas) {
        List<EventCalendar> eventoss = new ArrayList<>();
        List<ReunionConsejo> eventos = reunionConsejoDAO.allByOficinas(oficinas);
        for (ReunionConsejo reunionConsejo : eventos) {
            EventCalendar eventCalendar = new EventCalendar();
            eventCalendar.setTitle("Reunión Programada");
            String jFechaInicio = reunionConsejo.getFecha() != null ? new DateTime(reunionConsejo.getFecha()).toString("yyyy-MM-dd") : "";
            eventCalendar.setStart(jFechaInicio);
            String jFechaFin = reunionConsejo.getFecha() != null ? new DateTime(reunionConsejo.getFecha()).toString("yyyy-MM-dd") : "";
            eventCalendar.setEnd(jFechaFin);
            eventCalendar.setColor("#7990b5");
            eventoss.add(eventCalendar);
        }
        return eventoss;
    }

    @Override
    public List<ReunionConsejo> allReunionConsejoByDyna(DynatableFilter filter, List<Oficina> oficina) {
        List<ReunionConsejo> reunionesConsejo = reunionConsejoDAO.allByDynatable(filter, oficina);
        return reunionesConsejo;
    }

    @Override
    public List<Oficina> allOficinaFac() {
        return oficinaDAO.allFac();
    }

}
