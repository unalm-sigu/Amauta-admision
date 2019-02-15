package pe.edu.lamolina.pivot.controller.academico.reunionconsejo;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.calendar.EventCalendar;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ReunionConsejoService {

    void saveReunionConsejo(ReunionConsejo reunionConsejo, Oficina oficina, DataSessionPivot ds);

    void updateReunionConsejo(ReunionConsejo reunionConsejo, DataSessionPivot ds);

    List<EventCalendar> allcalendar(CicloAcademico ciclo, List<Oficina> oficina);

    ReunionConsejo findReunionConsejoByFechaAndOficina(Date fecha, Oficina oficina);

    List<ReunionConsejo> allReunionConsejoByDyna(DynatableFilter filter, List<Oficina> oficina);

    List<Oficina> allOficinaFac();
}
