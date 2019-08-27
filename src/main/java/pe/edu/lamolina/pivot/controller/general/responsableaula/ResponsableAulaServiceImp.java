package pe.edu.lamolina.pivot.controller.general.responsableaula;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.ResponsableAulaDAO;

@Service
@Transactional(readOnly = false)
public class ResponsableAulaServiceImp implements ResponsableAulaService {

    @Autowired
    ResponsableAulaDAO responsableAulaDAO;

    @Autowired
    PersonaDAO personaDAO;

    @Override
    public List<Persona> allResponsablesByRaptor(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<Persona> personasResponsables = personaDAO.allResponsableAulas(filter, EstadoEnum.ACT);

        return personasResponsables;
    }

}
