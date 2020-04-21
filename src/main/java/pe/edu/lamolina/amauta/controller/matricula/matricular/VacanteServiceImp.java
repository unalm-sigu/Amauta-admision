package pe.edu.lamolina.amauta.controller.matricula.matricular;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NVAC;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSimultaneoDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.TurnoAtencionDAO;
import pe.edu.lamolina.amauta.dao.vacante.VacanteAlumnoDAO;

@Service
@Transactional(readOnly = true)
public class VacanteServiceImp implements VacanteService {

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

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Async
    @Override
    @Transactional
    public void enviarSeccion(Seccion seccion) {
        seccionDAO.updateMatriculados(seccion);
        logger.debug("update enviarSeccion");
    }

    @Async
    @Override
    @Transactional
    public void enviarMatriculaResuemn(MatriculaResumen matri) {
        matriculaResumenDAO.updateCreditos(matri);
        logger.debug("update enviarMatriculaResuemn");
        //matriculaResumenDAO.updateCreditos(noMatri);
    }

    @Async
    @Override
    @Transactional
    public void enviarMatriculaSeccion23(MatriculaSeccion matSecc) {
        matriculaSeccionDAO.update(matSecc);
        logger.debug("update enviarMatriculaSeccion23");
    }

    @Async
    @Override
    @Transactional
    public void enviarMatriculaCurso23(MatriculaCurso matriculaCurso) {
        matriculaCursoDAO.update(matriculaCurso);
        logger.debug("update enviarMatriculaCurso23");
    }

    @Async
    @Override
    @Transactional
    public void enviarMatriculaResumen23(MatriculaResumen resumene) {
        matriculaResumenDAO.update(resumene);
        logger.debug("update enviarMatriculaResumen23");
    }

    @Async
    @Override
    @Transactional
    public void enviarVacanteAlumno(List<VacanteAlumno> vacantesAlumnoTemp) {
        vacanteAlumnoDAO.updateEstado(vacantesAlumnoTemp);
        logger.debug("update enviarVacanteAlumno");
    }

    @Async
    @Override
    @Transactional
    public void enviarMatSeccionEstado(List<MatriculaSeccion> matriculaSeccionMatTemp) {
        matriculaSeccionDAO.updateEstado(matriculaSeccionMatTemp, MAT);
        logger.debug("update enviarMatSeccionEstado");
    }

    @Async
    @Override
    @Transactional
    public void enviarMatSeccionEstadoNVAC(List<MatriculaSeccion> matriculaSeccionNvacTemp) {
        matriculaSeccionDAO.updateEstado(matriculaSeccionNvacTemp, NVAC);
        logger.debug("update enviarMatSeccionEstadoNVAC");
    }

}
