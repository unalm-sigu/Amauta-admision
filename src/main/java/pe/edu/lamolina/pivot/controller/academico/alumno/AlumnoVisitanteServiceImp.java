package pe.edu.lamolina.pivot.controller.academico.alumno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;

@Service
@Transactional(readOnly = true)
public class AlumnoVisitanteServiceImp implements AlumnoVisitanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

}
