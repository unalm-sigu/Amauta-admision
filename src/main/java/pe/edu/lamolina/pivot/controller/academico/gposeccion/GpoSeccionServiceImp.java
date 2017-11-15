package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;

@Service
@Transactional(readOnly = true)
public class GpoSeccionServiceImp implements GpoSeccionService {

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Override
    public List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return grupoSeccionDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    public GpoSeccionResumen resumen() {
        return grupoSeccionDAO.resumen();
    }

}
