package pe.edu.lamolina.pivot.controller.academico.matricula.configuracion;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

        int cantDias = (int) ((fechaFinal.getTime() - fechaInicial.getTime()) / 86400000);

        Integer cantAlumnos = config.getAlumnos();
        TurnoAtencion objTurno = null;

        DateFormat formatter = new SimpleDateFormat("HH:mm");

        Integer prioridad = 1;
        for (int i = 0; i <= cantDias; i++) {
            Time duracion = new Time(formatter.parse(config.getDuracion()).getTime());
            Time espera = new Time(formatter.parse(config.getEspera()).getTime());
            Time horaInicio = new Time(formatter.parse(config.getHoraInicio()).getTime());
            Time horaFinal = new Time(formatter.parse(config.getHoraInicio()).getTime());

            for (int j = 1; j <= config.getTurnosDia(); j++) {
                objTurno = new TurnoAtencion();
                objTurno.setAlumnos(cantAlumnos);
                objTurno.setConfiguracionTurnosAtencion(config);
                objTurno.setFecha(fechaInicial);
                objTurno.setHoraInicio(horaInicio.toString());
                int min = ((duracion.getHours() * 60) + duracion.getMinutes()) - espera.getMinutes();
                horaFinal.setMinutes(horaFinal.getMinutes() + min);
                objTurno.setHoraFinal(horaFinal.toString());
                objTurno.setPrioridadInicio(prioridad);
                prioridad = prioridad + cantAlumnos;
                objTurno.setPrioridadFin(prioridad - 1);
                objTurno.setTurno(j);
                turnoAtencionDAO.save(objTurno);
                horaInicio.setHours(horaInicio.getHours() + duracion.getHours());

            }
            fechaInicial.setDate(fechaInicial.getDate() + 1);
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
