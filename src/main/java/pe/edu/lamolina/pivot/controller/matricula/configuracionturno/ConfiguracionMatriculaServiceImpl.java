package pe.edu.lamolina.pivot.controller.matricula.configuracionturno;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.enums.TipoMatriculaEnum;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionMatriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;

@Service
public class ConfiguracionMatriculaServiceImpl implements ConfiguracionMatriculaService {

    @Autowired
    ConfiguracionMatriculaDAO configuracionMatriculaDAO;

    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcatemicoDAO;

    @Override
    @Transactional
    public Long saveConfiguracion(ConfiguracionTurnosAtencion config) throws ParseException {

        for (TipoMatriculaEnum d : TipoMatriculaEnum.values()) {
            if (config.getTipo().equals(d.getValue())) {
                config.setTipo(d.name());
            }
        };
        config.setIsGenerado(Boolean.FALSE);
        configuracionMatriculaDAO.save(config);

        DateTime inicio = new DateTime(config.getFechaInicio());
        DateTime fin = new DateTime(config.getFechaFin());

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        Integer dias = Days.daysBetween(inicio, fin).getDays() + 1;
        Integer prioridad = 0;
        Integer nroTurno = 1;
        for (int i = 0; i < dias; i++) {

            DateTime fecha = inicio.plusDays(i);
            DateTime fechaHora = format.parseDateTime(fecha.toString("yyyy-MM-dd") + " " + config.getHoraInicio());
            for (int j = 0; j < config.getTurnosDia(); j++) {
                prioridad += 1;
                DateTime fechaHoraTurnoInicio = fechaHora.plusMinutes(j * config.getDuracion());
                DateTime fechaHoraTurnoEspera = fechaHoraTurnoInicio.plusMinutes(config.getDuracion() - config.getEspera());
                DateTime fechaHoraTurnoFin = fechaHoraTurnoInicio.plusMinutes(config.getDuracion());

                TurnoAtencion turno = new TurnoAtencion();
                turno.setFecha(fecha.toDate());
                turno.setFechaHoraInicio(fechaHoraTurnoInicio.toDate());
                turno.setFechaHoraFin(fechaHoraTurnoFin.toDate());
                turno.setAlumnos(config.getAlumnos());
                turno.setConfiguracionTurnosAtencion(config);
                turno.setHoraInicio(fechaHoraTurnoInicio.toString("HH:mm"));
                turno.setHoraFinal(fechaHoraTurnoEspera.toString("HH:mm"));
                turno.setFechaHoraEspera(format.parseDateTime(fechaHoraTurnoEspera.toString("yyyy-MM-dd HH:mm")).toDate());
                turno.setTurno(nroTurno);
                turno.setConfiguracionTurnosAtencion(config);

                turno.setPrioridadInicio(prioridad);
                prioridad += config.getAlumnos() - 1;
                turno.setPrioridadFin(new BigDecimal(prioridad));

                turnoAtencionDAO.save(turno);
                nroTurno++;
            }
        }

        return config.getId();
    }

    @Override
    public List<EventoCicloAcademico> findEventoCiclo(CicloAcademico cicloAcademico) {

        return eventoCicloAcatemicoDAO.allEventoAcademicoByCicloAca(cicloAcademico);
    }

    @Override
    public List<ConfiguracionTurnosAtencion> allConfiguraciones(CicloAcademico cicloAcademico) {
        return configuracionMatriculaDAO.allByCiclo(cicloAcademico);
    }

    @Override
    public List<TurnoAtencion> allTurnosByConfiguracion(ConfiguracionTurnosAtencion config) {
        return turnoAtencionDAO.allByConfiguracion(config);
    }

    @Override
    @Transactional
    public ConfiguracionTurnosAtencion updateTurnos(Long id, String value) {
        TurnoAtencion objTurno = turnoAtencionDAO.findById(id);
        objTurno.setAlumnos(Integer.parseInt(value));
        objTurno.setPrioridadFin(new BigDecimal(objTurno.getPrioridadInicio() + Integer.parseInt(value) - 1));
        turnoAtencionDAO.update(objTurno);

        List<TurnoAtencion> lstTurno = turnoAtencionDAO.allByIdTurno(objTurno.getConfiguracionTurnosAtencion(), objTurno.getId());
        Integer inicial = objTurno.getPrioridadInicio() + Integer.parseInt(value);
        for (TurnoAtencion turnoAtencion : lstTurno) {
            Integer fin = (inicial + turnoAtencion.getAlumnos());
            turnoAtencion.setPrioridadInicio(inicial);
            turnoAtencion.setPrioridadFin(new BigDecimal(fin - 1));
            turnoAtencionDAO.update(turnoAtencion);
            inicial = fin;

        }
        return objTurno.getConfiguracionTurnosAtencion();

    }

    @Override
    @Transactional
    public void deleteConfiguracion(ConfiguracionTurnosAtencion config) {

        List<TurnoAtencion> lst = turnoAtencionDAO.allByConfiguracion(config);
        for (TurnoAtencion turnoAtencion : lst) {
            turnoAtencionDAO.delete(turnoAtencion);
        }
        configuracionMatriculaDAO.delete(config);

    }

}
