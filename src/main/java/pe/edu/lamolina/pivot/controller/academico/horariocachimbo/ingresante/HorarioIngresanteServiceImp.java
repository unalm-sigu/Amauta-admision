package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

@Service
@Transactional(readOnly = true)
public class HorarioIngresanteServiceImp implements HorarioIngresanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Override
    public List<AlumnoHorario> allAlumnoHorario(CicloAcademico cicloAcademico) {
        return alumnoHorarioDAO.allByCicloAcademico(cicloAcademico);
    }

    @Override
    public List<Alumno> allAlumnoByAlumnoHorario(
            DynatableFilter filter,
            List<AlumnoHorario> alumnosHorario,
            CicloAcademico cicloAcademico) {
        if (alumnosHorario.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> alumnos = new ArrayList<>();
        for (AlumnoHorario alumnoHorario : alumnosHorario) {
            alumnos.add(alumnoHorario.getAlumno().getId());
        }
        return alumnoDAO.allByAlumnoHorario(filter, cicloAcademico, alumnos);
    }

}
