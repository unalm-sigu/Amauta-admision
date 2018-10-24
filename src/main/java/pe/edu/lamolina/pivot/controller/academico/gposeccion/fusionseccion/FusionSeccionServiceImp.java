package pe.edu.lamolina.pivot.controller.academico.gposeccion.fusionseccion;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;

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

    @Override
    public List<Alumno> allAlumnoBySeccion(Seccion seccion) {
        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccion);
        Map<Long, Alumno> alumnos = TypesUtil.convertListToMap("matriculaResumen.alumno.id", "matriculaResumen.alumno", matriculasSeccion);
        return alumnos.values().stream().collect(Collectors.toList());
    }

    @Override
    public void trasladar(Fusion trasladoForm) {
        Seccion seccion = seccionDAO.find(trasladoForm.getSeccion());
        logger.debug("trasladar a seccion {} {} los alumnos ", seccion.getId(),seccion.getCodigo());
        Long[] idAlumnos = trasladoForm.getAlumnos();
        logger.debug("cuyos ids son {}  ... ", idAlumnos);
        List<Alumno> alumnos =alumnoDAO.allByIds(idAlumnos);
        for (Alumno alumno : alumnos) {
            logger.debug("alumno {}", alumno.getPersona().getNombreCompleto());
        }
    }

}
