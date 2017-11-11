package pe.edu.lamolina.pivot.controller.academico.horario.horario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;

@Service
@Transactional(readOnly = true)
public class HorarioServiceImp implements HorarioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

}
