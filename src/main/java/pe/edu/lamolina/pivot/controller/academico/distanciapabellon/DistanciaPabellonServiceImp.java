package pe.edu.lamolina.pivot.controller.academico.distanciapabellon;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;

@Service
@Transactional(readOnly = false)
public class DistanciaPabellonServiceImp implements DistanciaPabellonService {

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Override
    public List<DepartamentoAcademico> allDepartamentos() {
        return departamentoAcademicoDAO.allActivos();
    }

    @Override
    public List<Aula> allModulos() {
        return aulaDAO.allPabellonesByOficina(OficinaEnum.OERA);
    }

}
