package pe.edu.lamolina.pivot.controller.academico.matricular;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.matricula.MatriculaSimultaneo;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSimultaneoDAO;
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

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSimultaneoDAO matriculaSimultaneoDAO;

    @Override
    public TurnoAtencion findTurnoAtencion(Long turnoAtencion) {
        return turnoAtencionDAO.findById(turnoAtencion);
    }

    @Override
    public void matricular(TurnoAtencion turnoAtencion, CicloAcademico cicloAcademico) {

        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allNoMatriculadoByCiclo(cicloAcademico);
        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allPrematriculadoByMatriculaResumen(matriculaResumens);
        Map<Long, List<MatriculaCurso>> matriculaCursosMap = TypesUtil.convertListToMapList("matriculaResumen.id", matriculaCursos);
        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allPrematriculadoByMatriculaResumen(matriculaResumens);
        Map<Long, List<MatriculaSeccion>> matriculaSeccionsMap = TypesUtil.convertListToMapList("matriculaResumen.id", matriculaSeccions);
        List<MatriculaSimultaneo> matriculaSimultaneos = matriculaSimultaneoDAO.allByMatriculaCurso(matriculaCursos);
        Map<Long, List<MatriculaSimultaneo>> matriculaSimultaneosMap = TypesUtil.convertListToMapList("matriculaCurso.id", matriculaSeccions);
        for (MatriculaResumen matriculaResumen : matriculaResumens) {
            List<MatriculaCurso> misMatriculaCurso = matriculaCursosMap.get(matriculaResumen.getId());
            List<Long> pendientesMatriculaCurso = new ArrayList();
            Map<Long, MatriculaCurso> misMatriculaCursoMap = TypesUtil.convertListToMapList("id", misMatriculaCurso);
            for (MatriculaCurso matriculaCurso : misMatriculaCursoMap.values()) {
                List<MatriculaSimultaneo> misMatriculaSimultaneo = matriculaSimultaneosMap.get(matriculaCurso.getId());
                Map<Long, MatriculaSimultaneo> misMatriculaSimultaneoMap = TypesUtil.convertListToMapList("id", misMatriculaSimultaneo);
            }
        }
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
