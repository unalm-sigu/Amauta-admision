package pe.edu.lamolina.pivot.controller.academico.matricular;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;

@Service
@Transactional(readOnly = true)
public class MatricularServiceImp implements MatricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Override
    public TurnoAtencion findTurnoAtencion(Long turnoAtencion) {
        return turnoAtencionDAO.findById(turnoAtencion);
    }

    @Override
    public void matricular(TurnoAtencion turnoAtencion, CicloAcademico cicloAcademico) {

    }

    @Override
    public Long countAllAlumnoPrematriculado(CicloAcademico cicloAcademico) {
        return matriculaCursoDAO.countAllAlumnoPrematriculado(cicloAcademico);
    }

    @Override
    public Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico) {
        return matriculaSeccionDAO.countAllSeccionPrematriculado(cicloAcademico);
    }
}
