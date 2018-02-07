/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.lamolina.pivot.controller.academico.matricula.configuracion;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionMatriculaDAO;

/**
 *
 * @author AlbatrossCloud
 */
@Service
@Transactional(readOnly = true)
public class ConfiguracionMatriculaServiceImpl implements ConfiguracionMatriculaService {

    @Autowired
    ConfiguracionMatriculaDAO serviceConfiguracionMatriculaDAO;

    @Override
    public ConfiguracionTurnosAtencion findConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion) {
        return serviceConfiguracionMatriculaDAO.findConfiguracion(configuracionTurnosAtencion);
    }

    @Override
    public void saveConfiguracion(ConfiguracionTurnosAtencion configuracionTurnosAtencion) {
        serviceConfiguracionMatriculaDAO.save(configuracionTurnosAtencion);
        Integer cantTurnos;
        cantTurnos = (configuracionTurnosAtencion.getFechaFin().getDay() - configuracionTurnosAtencion.getFechaInicio().getDay())
                * configuracionTurnosAtencion.getTurnosDia();

        Integer cantAlumnos = Math.round(configuracionTurnosAtencion.getAlumnos() / cantTurnos);
        Integer b = 1;
        Time horaInicio = (Time) configuracionTurnosAtencion.getHoraInicio();

        List<TurnoAtencion> turnoAtencion = new ArrayList<TurnoAtencion>();
        TurnoAtencion objTurno = new TurnoAtencion();
        for (int j = 1; j < cantTurnos; j++) {
            for (int i = 1; i < configuracionTurnosAtencion.getTurnosDia(); i++) {
                objTurno.setAlumnos(cantAlumnos);
                objTurno.setFecha(configuracionTurnosAtencion.getFechaInicio());
                objTurno.setHoraInicio(horaInicio);
                objTurno.setHoraFinal(new Time(horaInicio.getMinutes() + (configuracionTurnosAtencion.getDuracion() - configuracionTurnosAtencion.getEspera())));
                objTurno.setIdConfiguracionTurnosAtencion(configuracionTurnosAtencion);
                objTurno.setPrioridadInicio(b);
                objTurno.setPrioridadFin(b + (cantAlumnos - 1));
                objTurno.setTurno(i);
                turnoAtencion.add(objTurno);

                b = objTurno.getPrioridadFin() + 1;
                horaInicio = new Time(horaInicio.getMinutes() + (configuracionTurnosAtencion.getDuracion() - configuracionTurnosAtencion.getEspera()));
            }
            horaInicio = (Time) configuracionTurnosAtencion.getHoraInicio();
            b=1;
        }

    }

}
