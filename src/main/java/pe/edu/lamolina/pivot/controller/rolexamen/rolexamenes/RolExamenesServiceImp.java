package pe.edu.lamolina.pivot.controller.rolexamen.rolexamenes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.dao.academico.CuotaGpoHorasDAO;

@Service
@Transactional(readOnly = true)
public class RolExamenesServiceImp implements RolExamenesService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CuotaGpoHorasDAO cuotaGpoHorasDAO;


//    @Override
//    public List<CuotasGrupoHoras> allRolExamenes(DynatableFilter filter, CicloAcademico cicloAcademico) {
//        return cuotaGpoHorasDAO.allByDynatable(filter, cicloAcademico);
//    }
}
