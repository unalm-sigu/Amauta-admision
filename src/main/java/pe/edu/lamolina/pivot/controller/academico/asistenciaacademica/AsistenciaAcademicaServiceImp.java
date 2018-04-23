package pe.edu.lamolina.pivot.controller.academico.asistenciaacademica;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;

@Service
@Transactional(readOnly = true)
public class AsistenciaAcademicaServiceImp implements AsistenciaAcademicaService {

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Override
    public List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion) {
        return matriculaSeccionDAO.allBySeccion(seccion);
    }

}
