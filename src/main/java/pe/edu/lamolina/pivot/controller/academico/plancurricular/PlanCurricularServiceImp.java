package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.academico.OrientacionCarrera;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Service
@Transactional(readOnly = true)
public class PlanCurricularServiceImp implements PlanCurricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    OrientacionCarreraDAO orientacionCarreraDAO;

    @Override
    public List<Carrera> allCarrerasByFilter(Facultad facultad, EstadoEnum estadoEnum) {
        return carreraDAO.allByFilter(facultad, estadoEnum);
    }

    @Override
    public List<OrientacionCarrera> allOrientacionCarreraByFilter(Carrera carrera, EstadoEnum estadoEnum) {
        return orientacionCarreraDAO.allByFilter(carrera, estadoEnum);
    }

}
