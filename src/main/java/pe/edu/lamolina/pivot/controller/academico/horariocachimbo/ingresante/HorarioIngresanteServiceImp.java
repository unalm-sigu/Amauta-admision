package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.zelper.enums.EstadoAlumnoHorarioEnum;

@Service
@Transactional(readOnly = true)
public class HorarioIngresanteServiceImp implements HorarioIngresanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    HorarioCachimbosDAO horarioCachimbosDAO;

    @Override
    public List<AlumnoHorario> allAlumnoHorario(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return alumnoHorarioDAO.allByAlumnoHorario(filter, cicloAcademico);
    }

    @Override
    @Transactional
    public void addAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        logger.debug("alumno {} cicloAcademico {}", alumno.getId(), cicloAcademico.getId());
        AlumnoHorario alumnoHorario = alumnoHorarioDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        if (alumnoHorario == null) {
            alumnoHorario = new AlumnoHorario();
            alumnoHorario.setAlumno(alumno);
            alumnoHorario.setCicloAcademico(cicloAcademico);
            alumnoHorario.setEstado(EstadoAlumnoHorarioEnum.PEND.name());
            alumnoHorarioDAO.save(alumnoHorario);
        }
    }

    @Override
    @Transactional
    public void activarMatricula(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario.getId());
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.MATR.name());
        alumnoHorarioDAO.update(alumnoHorarioDb);
    }

    @Override
    @Transactional
    public void suspenderMatricula(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario.getId());
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.PEND.name());
        alumnoHorarioDAO.update(alumnoHorarioDb);
    }

    @Override
    @Transactional
    public void asignarHorario(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario.getId());
        if (alumnoHorarioDb == null) {
            return;
        }
        alumnoHorarioDb.setHorarioCachimbos(alumnoHorario.getHorarioCachimbos());
        alumnoHorarioDAO.update(alumnoHorarioDb);
    }

    @Override
    @Transactional
    public void retirarHorario(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario.getId());
        if (alumnoHorarioDb == null) {
            return;
        }
        HorarioCachimbos horarioCachimbos = alumnoHorarioDb.getHorarioCachimbos();
        if (horarioCachimbos != null && horarioCachimbos.getSuscritos() > 0) {
            horarioCachimbos.setSuscritos(horarioCachimbos.getSuscritos() - 1);
            horarioCachimbosDAO.update(horarioCachimbos);
        }
        alumnoHorarioDb.setHorarioCachimbos(null);
        alumnoHorarioDAO.update(alumnoHorarioDb);
    }

    @Override
    public void buscarHorario(Alumno alumno, CicloAcademico cicloAcademico) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre) {
        return alumnoDAO.allAlumnoByName(nombre);
    }

}
