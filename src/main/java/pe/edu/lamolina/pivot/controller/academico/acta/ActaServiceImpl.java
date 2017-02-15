package pe.edu.lamolina.pivot.controller.academico.acta;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;

@Service
@Transactional(readOnly = true)
public class ActaServiceImpl implements ActaService {

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Override
    public List<DepartamentoAcademico> allActiveDepartamentosAcademicos(DynatableFilter filter) {
        return departamentoAcademicoDAO.allActiveByDyna(filter);
    }

    @Override
    public DepartamentoAcademico findDepartamento(Long idDepartamentoAcad) {
        return departamentoAcademicoDAO.find(idDepartamentoAcad);
    }

    @Override
    public List<GrupoSeccion> allGrupoSeccionByFilter(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico) {
        return grupoSeccionDAO.allByFilter(null, cicloAcademico, departamentoAcademico);
    }

}
