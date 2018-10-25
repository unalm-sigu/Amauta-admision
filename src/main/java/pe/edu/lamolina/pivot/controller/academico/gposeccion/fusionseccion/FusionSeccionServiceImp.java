package pe.edu.lamolina.pivot.controller.academico.gposeccion.fusionseccion;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class FusionSeccionServiceImp implements FusionSeccionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Override
    public List<Alumno> allAlumnoBySeccion(Seccion seccion) {
        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccion);
        Map<Long, Alumno> alumnos = TypesUtil.convertListToMap("matriculaResumen.alumno.id", "matriculaResumen.alumno", matriculasSeccion);
        return alumnos.values().stream().collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void trasladar(Fusion trasladoForm, CicloAcademico cicloAcademico, DataSessionPivot ds) {

        Seccion seccion = seccionDAO.find(trasladoForm.getSeccion());
        Seccion seccionSeleccionada = seccionDAO.find(trasladoForm.getSeccionSeleccionada());

        logger.debug("seccion  :  {}  seccion origen {}  ", seccion.getId(), seccionSeleccionada.getId());

        Long[] idAlumnos = trasladoForm.getAlumnosid();

        for (Long alumno : idAlumnos) {

            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(new Alumno(alumno), cicloAcademico);

            Alumno alumnoDb = matriculaResumen.getAlumno();

            logger.debug("alumno  :  {}  ", alumnoDb.getPersona().getNombreCompleto());
            logger.debug("matriculaResumen  :  {}  ", matriculaResumen.getId());
            logger.debug("seccion  :  {}  ", seccion.getId());
            MatriculaSeccion matriculaSeccion = matriculaSeccionDAO.findByMatriculaResumenSeccion(matriculaResumen, seccionSeleccionada);
            logger.debug("matriculaSeccion existe  :  {}  ", matriculaSeccion != null);

            if (matriculaSeccion != null) {
                logger.debug("matriculaSeccion  :  {}  ", matriculaSeccion.getId());
                matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.TRAS);
                matriculaSeccionDAO.update(matriculaSeccion);
            }

            MatriculaSeccion justMatriculadoOnSeccion = matriculaSeccionDAO.findByMatriculaResumenSeccion(matriculaResumen, seccion);

            if (justMatriculadoOnSeccion != null) {

                logger.debug("ya matriculado MatriculaSeccion  :  {}  ", justMatriculadoOnSeccion.getId());
                justMatriculadoOnSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
                matriculaSeccionDAO.update(justMatriculadoOnSeccion);

            } else {

                MatriculaSeccion newMatriculaSeccion = new MatriculaSeccion();
                newMatriculaSeccion.setVisible(null);
                newMatriculaSeccion.setMatriculaResumen(matriculaResumen);
                newMatriculaSeccion.setFechaRegistro(new Date());
                newMatriculaSeccion.setUserRegistro(ds.getUsuario());
                newMatriculaSeccion.setSeccion(seccion);
                newMatriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
                matriculaSeccionDAO.save(newMatriculaSeccion);
                logger.debug("creado  :  {}  ", newMatriculaSeccion.getId());
            }

            if (TipoSeccionEnum.PCUR.name().equalsIgnoreCase(seccionSeleccionada.getTipoSeccion())) {

                logger.debug(" ***** TipoSeccionEnum PCUR ***** ");

                logger.debug("alumno  :  {}  ", alumnoDb.getPersona().getNombreCompleto());
                logger.debug("matriculaResumen  :  {}  ", matriculaResumen.getId());
                logger.debug("seccion  :  {}  ", seccion.getSeccionSuperior().getId());
                MatriculaSeccion teoMatriculaSeccion = matriculaSeccionDAO.findByMatriculaResumenSeccion(matriculaResumen, seccionSeleccionada.getSeccionSuperior());
                logger.debug("MatriculaSeccion existe  :  {}  ", teoMatriculaSeccion != null);

                if (teoMatriculaSeccion != null) {
                    logger.debug("matriculaSeccion  :  {}  ", matriculaSeccion.getId());
                    teoMatriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.TRAS);
                    matriculaSeccionDAO.update(teoMatriculaSeccion);
                }

                MatriculaSeccion justTeoMatriculadoOnSeccion = matriculaSeccionDAO.findByMatriculaResumenSeccion(matriculaResumen, seccion.getSeccionSuperior());

                if (justTeoMatriculadoOnSeccion != null) {

                    logger.debug("ya matriculado MatriculaSeccion  :  {}  ", justTeoMatriculadoOnSeccion.getId());
                    justTeoMatriculadoOnSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaSeccionDAO.update(justTeoMatriculadoOnSeccion);

                } else {

                    MatriculaSeccion newTeoMatriculaSeccion = new MatriculaSeccion();
                    newTeoMatriculaSeccion.setVisible(null);
                    newTeoMatriculaSeccion.setMatriculaResumen(matriculaResumen);
                    newTeoMatriculaSeccion.setFechaRegistro(new Date());
                    newTeoMatriculaSeccion.setUserRegistro(ds.getUsuario());
                    newTeoMatriculaSeccion.setSeccion(seccion.getSeccionSuperior());
                    newTeoMatriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
                    matriculaSeccionDAO.save(newTeoMatriculaSeccion);
                    logger.debug("creado :  {}", newTeoMatriculaSeccion.getId());
                }
            }
        }
    }

    @Override
    public List<Seccion> allSeccionDisponible(Seccion seccionForm
    ) {
        Seccion seccion = seccionDAO.find(seccionForm);
        Curso curso = (Curso) ObjectUtil.getParentTree(seccion, "grupoSeccion.curso");
        return seccionDAO.allByCursoExceptSeccion(curso, seccion);
    }

    private MatriculaSeccion getTeoria(List<MatriculaSeccion> misMatriculaSecciones, Seccion seccion) {
        for (MatriculaSeccion misMatriculaSeccione : misMatriculaSecciones) {
            if (misMatriculaSeccione.getSeccion().getId().longValue() == seccion.getId()) {
                return misMatriculaSeccione;
            }
        }
        return null;
    }
}
