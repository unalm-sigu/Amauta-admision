package pe.edu.lamolina.pivot.controller.academico.encuesta.encuestar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EncuestaServiceImp implements EncuestaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

}
