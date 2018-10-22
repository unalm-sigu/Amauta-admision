package pe.edu.lamolina.pivot.controller.academico.topematricula;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.TopeMatricula;
import pe.edu.lamolina.pivot.dao.academico.TopeMatriculaDAO;

@Service
@Transactional(readOnly = true)
public class TopeMatriculaServiceImp implements TopeMatriculaService {

    @Autowired
    TopeMatriculaDAO topeMatriculaDAO;

    @Override
    public List<TopeMatricula> allTopeMatricula(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return topeMatriculaDAO.allByDynatable(filter, cicloAcademico);
    }
}
