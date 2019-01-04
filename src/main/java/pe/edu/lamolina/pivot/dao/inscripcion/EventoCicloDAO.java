package pe.edu.lamolina.pivot.dao.inscripcion;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;

public interface EventoCicloDAO extends EasyDAO<EventoCiclo> {

    EventoCiclo findByCicloFecha(CicloPostula ciclo, Date fecha);

    EventoCiclo findByInicioCiclo(CicloPostula ciclo, Date fecha);

    EventoCiclo findByFinCiclo(CicloPostula ciclo, Date fecha);

    List<EventoCiclo> allByFilterCiclo(DynatableFilter filter, CicloPostula ciclo);

    List<EventoCiclo> allByEventoCiclo(Evento evento, CicloPostula ciclo);

    List<EventoCiclo> allByCiclo(CicloPostula cicloAnterior);

    List<EventoCiclo> allInscripcionesByCiclo(CicloPostula ciclo);

    EventoCiclo findExtemporaneoByFecha(CicloPostula ciclo, Date fecha);

}

