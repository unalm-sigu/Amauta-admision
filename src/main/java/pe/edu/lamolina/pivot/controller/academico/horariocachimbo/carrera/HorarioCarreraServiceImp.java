package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.carrera;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;

@Service
@Transactional(readOnly = true)
public class HorarioCarreraServiceImp implements HorarioCarreraService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarreraCachimbosDAO carreraCachimbosDAO;

    @Override
    public List<CarreraCachimbos> allCarreraCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return carreraCachimbosDAO.allCarreraCachimbos(filter, cicloAcademico);
    }

}
