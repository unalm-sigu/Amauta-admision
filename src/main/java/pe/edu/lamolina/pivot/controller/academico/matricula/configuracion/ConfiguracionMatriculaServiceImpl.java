package pe.edu.lamolina.pivot.controller.academico.matricula.configuracion;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static javassist.CtMethod.ConstParameter.string;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.EventoAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionMatriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;

@Service
@Transactional(readOnly = true)
public class ConfiguracionMatriculaServiceImpl implements ConfiguracionMatriculaService {

    @Autowired
    ConfiguracionMatriculaDAO serviceConfiguracionMatriculaDAO;

    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcatemicoDAO;

    @Override
    public Long saveConfiguracion(ConfiguracionTurnosAtencion config) throws ParseException {

        serviceConfiguracionMatriculaDAO.save(config);
        Date fechaInicial = new Date(config.getFechaInicio().getTime());
        Date fechaFinal = config.getFechaFin();

        TurnoAtencion objTurno = null;
        Integer cantAlumnos = config.getAlumnos();
        DateTime inicio = new DateTime(config.getFechaInicio());
        DateTime fin = new DateTime(config.getFechaFin());

        int cantDias = (int) ((fechaFinal.getTime() - fechaInicial.getTime()) / 86400000);

        int dias = Days.daysBetween(inicio, fin).getDays() + 1;

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

        Integer duracion = Integer.valueOf(config.getDuracion());
        Integer espera = Integer.valueOf(config.getEspera());
        Integer prioridad = 1;

        for (int i = 0; i < dias; i++) {
            DateTime fecha = inicio.plusDays(i);
            DateTime fechaHora = formatter.parseDateTime(fecha.toString("yyyy-MM-dd") + " " + config.getHoraInicio());
            fechaHora = fechaHora.minusMinutes(Integer.valueOf(config.getDuracion()));
            for (int j = 1; j <= config.getTurnosDia(); j++) {
                fechaHora = fechaHora.plusMinutes(duracion);
                String horaInicio = fechaHora.toString("HH:mm");
                DateTime fechaHoraFin = fechaHora.plusMinutes(duracion - espera);
                String horaFin = fechaHoraFin.toString("HH:mm");
                objTurno = new TurnoAtencion();
                objTurno.setAlumnos(cantAlumnos);
                objTurno.setConfiguracionTurnosAtencion(config);
                objTurno.setFecha(fecha.toDate());
                objTurno.setHoraInicio(horaInicio);
                objTurno.setHoraFinal(horaFin);
                objTurno.setPrioridadInicio(prioridad);
                prioridad = prioridad + cantAlumnos;
                objTurno.setPrioridadFin(prioridad - 1);
                objTurno.setTurno(j);
                turnoAtencionDAO.save(objTurno);
            }
        }
        return config.getId();
    }

    @Override
    public List<EventoAcademico> findEventoCiclo(CicloAcademico cicloAcademico) {
        List<EventoCicloAcademico> lstEventoCiclo = eventoCicloAcatemicoDAO.allEventoAcademicoByCicloAca(cicloAcademico);
        Map<Long, EventoAcademico> mapConfiguracionTurno = TypesUtil.convertListToMap("eventoAcademico.id", "eventoAcademico", lstEventoCiclo);

        List<EventoAcademico> evento = new ArrayList(mapConfiguracionTurno.values());
        return evento;
    }

    @Override
    public List<ConfiguracionTurnosAtencion> allConfiguraciones(CicloAcademico cicloAcademico) {

        return serviceConfiguracionMatriculaDAO.allByCicloAcad(cicloAcademico);
    }

    @Override
    public List<TurnoAtencion> allTurnosByConfiguracion(ConfiguracionTurnosAtencion config) {
        return turnoAtencionDAO.findConfiguracion(config);
    }

    @Override
    @Transactional
    public ConfiguracionTurnosAtencion updateTurnos(Long id, String value) {
        TurnoAtencion objTurno = turnoAtencionDAO.findTurnosById(id);
        objTurno.setAlumnos(Integer.parseInt(value));
        objTurno.setPrioridadFin(objTurno.getPrioridadInicio() + Integer.parseInt(value) - 1);
        turnoAtencionDAO.update(objTurno);

        List<TurnoAtencion> lstTurno = turnoAtencionDAO.findTurnos(objTurno.getConfiguracionTurnosAtencion(), objTurno.getId());
        Integer inicial = objTurno.getPrioridadInicio() + Integer.parseInt(value);
        for (TurnoAtencion turnoAtencion : lstTurno) {
            Integer fin = (inicial + turnoAtencion.getAlumnos());
            turnoAtencion.setPrioridadInicio(inicial);
            turnoAtencion.setPrioridadFin(fin - 1);
            turnoAtencionDAO.update(turnoAtencion);
            inicial = fin;

        }
        return objTurno.getConfiguracionTurnosAtencion();

    }

    @Override
    @Transactional
    public void deleteConfiguracion(ConfiguracionTurnosAtencion config) {

        List<TurnoAtencion> lst = turnoAtencionDAO.findConfiguracion(config);
        for (TurnoAtencion turnoAtencion : lst) {
            turnoAtencionDAO.delete(turnoAtencion);
        }
        serviceConfiguracionMatriculaDAO.delete(config);

    }

}
