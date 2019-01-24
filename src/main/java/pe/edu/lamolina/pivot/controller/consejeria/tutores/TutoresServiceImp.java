package pe.edu.lamolina.pivot.controller.consejeria.tutores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.dao.consejeria.CoordinadorConsejeriaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;

@Service
@Transactional(readOnly = true)
public class TutoresServiceImp implements TutoresService {

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    CoordinadorConsejeriaDAO coordinadorConsejeriaDAO;

//    @Override
//    @Transactional
//         public Persona findPersonaById(long l) {
//
//        return personaDAO.find(l);
//    }
}
