package pe.edu.lamolina.amauta.controller.matricula.prioridadseccion;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class SeccionPrioridadServiceImp implements SeccionPrioridadService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Override
    public List<MatriculaSeccion> allMatMatriculaSeccion(String codigo, String seccion, CicloAcademico cicloAcademico) {

        List<MatriculaSeccion> matriculaSeccions = matriculaSeccionDAO.allMatriculadosBySeccion(seccion, cicloAcademico);
//        MatriculaResumen matricularesumen = matriculaResumenDAO.findByCodigoCiclo(codigo, cicloAcademico);
//        Assert.isNotNull(matriculaSeccion, "El alumno no tuvo accion en esta sección");
        Assert.isFalse(matriculaSeccions.isEmpty(), "El código de sección no existe");
        return matriculaSeccions;
    }

    @Override
    public List<Seccion> allSeccionByNombre(String nombre, DataSessionPivot ds) {

        List<Seccion> seccions = seccionDAO.findByNombreCiclo(nombre, ds.getCicloAcademico());
        return seccions;
    }

}
