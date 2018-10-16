package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CuotaGpoHoras;
import pe.edu.lamolina.pivot.dao.academico.CuotaGpoHorasDAO;

@Service
@Transactional(readOnly = true)
public class CuotaGpoHorasServiceImp implements CuotaGpoHorasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CuotaGpoHorasDAO cuotaGpoHorasDAO;

    @Override
    public List<CuotaGpoHoras> allCuotasGpoHoras(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return cuotaGpoHorasDAO.allByDynatable(filter, cicloAcademico);
    }

}
