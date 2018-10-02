package pe.edu.lamolina.pivot.controller.posgrado.cuotasalumno;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;

@Service
@Transactional(readOnly = true)
public class CuotasAlumnoServiceImp implements CuotasAlumnoService {

    @Autowired
    AlumnoDAO alumnoDAO;

    @Override
    public List<Alumno> allAlumnosPosgrado(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<String> modalidadesEstudios = new ArrayList<>();
        modalidadesEstudios.add(ModalidadEstudioEnum.EPG.name());
        modalidadesEstudios.add(ModalidadEstudioEnum.ESP.name());
        return alumnoDAO.allByModalidadesDynatable(filter, cicloAcademico, modalidadesEstudios);
    }

}
