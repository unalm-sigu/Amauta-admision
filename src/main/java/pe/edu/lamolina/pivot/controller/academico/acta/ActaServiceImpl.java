package pe.edu.lamolina.pivot.controller.academico.acta;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

@Service
@Transactional(readOnly = true)
public class ActaServiceImpl implements ActaService {

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

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

    @Override
    public List<GrupoSeccion> allGrupoSeccionByFilterDyna(CicloAcademico cicloAcademico, DepartamentoAcademico departamentoAcademico, DynatableFilter dynatableFilter) {
        return grupoSeccionDAO.allByFilter(cicloAcademico, departamentoAcademico, dynatableFilter);
    }

    @Override
    public DocenteSeccion findDocenteSeccionByFilter(Docente docente, Seccion seccion) {
        return docenteSeccionDAO.findByFilter(docente, seccion);
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionByGrupo(GrupoSeccion grupoSeccion) {
        return docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);
    }

    public void reabrirGrupo(GrupoSeccion grupoSeccion) {
        grupoSeccion = grupoSeccionDAO.find(grupoSeccion.getId());

    }

}
