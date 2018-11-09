package pe.edu.lamolina.pivot.controller.rolexamen.rolexamenes;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;

@Service
@Transactional(readOnly = true)
public class RolExamenesServiceImp implements RolExamenesService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolExamenesDAO rolexamenesDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Override
    public List<RolExamenes> allRolExamenes(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return rolexamenesDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    public List<EventoCicloAcademico> allEventoCicloAcademicos(CicloAcademico cicloAcademico) {
        List<EventoCicloAcademico> eventoCicloAcademicos = eventoCicloAcademicoDAO.allEventoCicloAcademicos(cicloAcademico);
        return eventoCicloAcademicos;
    }

}
