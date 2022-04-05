package pe.edu.lamolina.amauta.controller.academico.renuncia;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.inscripcion.PostulanteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
public class AlumnoRenunciaServiceImp implements AlumnoRenunciaService {

    private final PostulanteDAO postulanteDAO;

    @Override
    public List<Postulante> allAlumnosbyDynatable(DynatableFilter filter) {
        return postulanteDAO.allByDynatableRenuncia(filter);
    }


    @Override
    public void apply(Postulante postulanteForm, DataSessionPivot ds) {
        
    }

}
