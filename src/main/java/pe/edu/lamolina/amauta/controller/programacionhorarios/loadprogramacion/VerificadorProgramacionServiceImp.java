package pe.edu.lamolina.amauta.controller.programacionhorarios.loadprogramacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;

@Service
@Transactional(readOnly = true)
public class VerificadorProgramacionServiceImp implements VerificadorProgramacioService {

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MatriculaSeccion findMatriculaSeccion(long id) {
        return matriculaSeccionDAO.find(id);
    }

}
