package pe.edu.lamolina.amauta.controller.consejeria.administracion;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeriaHistorialDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.ConsejeriaHistorial;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AdministracionConsejeriaServiceImp implements AdministracionConsejeriaService {

    private final ConsejeriaHistorialDAO consejeriaHistorialDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;

    @Override
    public List<ConsejeriaHistorial> allConsejeriaHistorialByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {

        return consejeriaHistorialDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    public List<CicloAcademico> allCiclo() {
        return cicloAcademicoDAO.allPregradoByRangeCode(201510, 220000);
    }

    @Override
    public void clonar(ClonarConsejerosDTO clonarDTO) {
       
    }

}
