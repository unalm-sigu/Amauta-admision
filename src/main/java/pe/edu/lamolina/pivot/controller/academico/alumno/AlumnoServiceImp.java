package pe.edu.lamolina.pivot.controller.academico.alumno;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;

@Service
@Transactional(readOnly = true)
public class AlumnoServiceImp implements AlumnoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Override
    public List<Alumno> allAlumnosByCicloDynatable(DynatableFilter filter, String codigo, List<Long> filtros) {
        return alumnoDAO.allByRolDynatable(filter, codigo, filtros);
    }

    @Override
    public AlumnoResumen findResumen() {
        return alumnoDAO.findResumen();
    }

    @Override
    public List<MatriculaCurso> allMatriculaCursoByAlumno(Long idAlumno) {
        return matriculaCursoDAO.allByAlumno(idAlumno);
    }

    @Override
    public Alumno findAlumno(Alumno alumno, CicloAcademico academico) {
        return alumnoDAO.find(alumno, academico);
    }

}
