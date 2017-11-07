package pe.edu.lamolina.pivot.controller.academico.matriculable;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

@Service
@Transactional(readOnly = true)
public class MatriculableServiceImp implements MatriculableService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;

    @Override
    public List<Alumno> allAlumnosByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros) {
        return alumnoDAO.allByCicloRolDynatable(filter, cicloAcademico, codigo, filtros);
    }

    @Override
    public MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico) {
        return alumnoDAO.findResumenByCiclo(cicloAcademico);
    }

}
