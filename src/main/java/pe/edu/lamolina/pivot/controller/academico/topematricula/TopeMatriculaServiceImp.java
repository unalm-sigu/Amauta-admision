package pe.edu.lamolina.pivot.controller.academico.topematricula;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.TopeMatricula;
import pe.edu.lamolina.pivot.dao.academico.TopeMatriculaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TopeMatriculaServiceImp implements TopeMatriculaService {

    @Autowired
    TopeMatriculaDAO topeMatriculaDAO;

    @Override
    public List<TopeMatricula> allTopeMatricula(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return topeMatriculaDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    @Transactional
    public void save(List<TopeMatricula> topesMatricula, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        for (TopeMatricula tope : topesMatricula) {
            tope.setCicloAcademico(cicloAcademico);
            if (tope.getId() == null) {
                topeMatriculaDAO.save(tope);
            } else if (tope.getCreditos() != null) {
                topeMatriculaDAO.update(tope);
            } else {
                topeMatriculaDAO.delete(tope);
            }

        }

    }
}
