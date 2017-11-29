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

    @Override
    public void addAlumno(Alumno alumno, CicloAcademico cicloAcademico) {

        AlumnoHorario alumnoHorario = alumnoHorarioDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        if (alumnoHorario != null) {
            alumnoHorario = new AlumnoHorario();
            alumnoHorario.setAlumno(alumno);
            alumnoHorario.setCicloAcademico(cicloAcademico);
            alumnoHorarioDAO.save(alumnoHorario);
        }

    }

    @Override
    public void activarMatricula(Alumno alumno, CicloAcademico cicloAcademico) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suspenderMatricula(Alumno alumno, CicloAcademico cicloAcademico) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void buscarHorario(Alumno alumno, CicloAcademico cicloAcademico) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void asignarHorario(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.findByAlumnoCiclo(alumnoHorario.getAlumno(), alumnoHorario.getCicloAcademico());
        if (alumnoHorarioDb == null) {
            alumnoHorarioDAO.save(alumnoHorario);
            return;
        }
        alumnoHorarioDb.setHorarioCachimbos(alumnoHorario.getHorarioCachimbos());
        alumnoHorarioDAO.update(alumnoHorarioDb);
    }

    @Override
    @Transactional
    public void retirarHorario(Alumno alumno, CicloAcademico cicloAcademico) {
        AlumnoHorario alumnoHorario = alumnoHorarioDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        if (alumnoHorario == null) {
            return;
        }
        alumnoHorario.setHorarioCachimbos(null);
        alumnoHorarioDAO.update(alumnoHorario);
    }

}
