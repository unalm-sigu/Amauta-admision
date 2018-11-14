package pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario;

import java.util.List;
import org.jfree.data.time.Week;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Weeks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;

@Service
@Transactional(readOnly = true)
public class PlantillaHorarioServiceImp implements PlantillaHorarioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Override
    public RolExamenes findRolExamenes(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        DateTime dateTime1 = new DateTime(rolExamenes.getEventoCicloAcademico().getFechaInicio());
        DateTime dateTime2 = new DateTime(rolExamenes.getEventoCicloAcademico().getFechaFin());

        int dias = Days.daysBetween(dateTime1, dateTime2).getDays();
        if (dias % dateTime1.dayOfWeek().withMaximumValue().getDayOfWeek() != 0) {
            throw new PhobosException("La fecha inicio y fin programadas al rol examen, deben ser semanas contabilizables.");
        }
        int weeks = Weeks.weeksBetween(dateTime1, dateTime2).getWeeks();
        rolExamenes.setSemanas(weeks);
        return rolExamenes;
    }

}
