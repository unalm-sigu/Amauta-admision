package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.cancelarseccion;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;

@Service
@Transactional(readOnly = false)
public class CancelarSeccionServiceImp implements CancelarSeccionService {

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Override
    public List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion) {
        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccion);
        matriculasSeccion.removeIf(x -> !x.isEstadoMAT() && !x.isEstadoPMAT() && !x.isEstadoRCA());
        return matriculasSeccion;
    }

}
