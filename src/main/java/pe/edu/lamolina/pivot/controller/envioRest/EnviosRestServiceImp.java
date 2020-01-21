package pe.edu.lamolina.pivot.controller.envioRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo.ResponseRestService;

@Service
@Transactional(readOnly = true)
public class EnviosRestServiceImp implements EnviosRestService {

    @Autowired
    ResponseRestService responseRestService;

}
