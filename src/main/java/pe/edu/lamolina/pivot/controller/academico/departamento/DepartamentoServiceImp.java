package pe.edu.lamolina.pivot.controller.academico.departamento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.zelper.enums.DepartamentoAcademicoEstadoEnum;

@Service
@Transactional(readOnly = true)
public class DepartamentoServiceImp implements DepartamentoService {

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<DepartamentoAcademico> allDepartamentoAcademico(DynatableFilter filter) {
        return departamentoAcademicoDAO.allDynatable(filter);
    }

    @Override
    public DepartamentoAcademico findDepartamentoAcademico(Long idDepartamentoAcademico) {
        return departamentoAcademicoDAO.findDepartamentoAcademico(idDepartamentoAcademico);
    }

    @Override
    public void save(DepartamentoAcademico departamentoAcademico) {
        departamentoAcademico.setEstado(DepartamentoAcademicoEstadoEnum.CRE.name());
        departamentoAcademicoDAO.save(departamentoAcademico);
    }

    @Override
    @Transactional
    public void update(DepartamentoAcademico departamentoAcademico) {
        DepartamentoAcademico departamentoAcademicoDb = departamentoAcademicoDAO.findDepartamentoAcademico(departamentoAcademico.getId());
        departamentoAcademicoDb.setCodigo(departamentoAcademico.getCodigo());
        departamentoAcademicoDb.setNombre(departamentoAcademico.getNombre());
        departamentoAcademicoDb.setNombreLargo(departamentoAcademico.getNombreLargo());
        departamentoAcademicoDb.setFacultad(departamentoAcademico.getFacultad());
        departamentoAcademicoDAO.update(departamentoAcademicoDb);
    }

    @Override
    @Transactional
    public void delete(DepartamentoAcademico departamentoAcademico) {
        departamentoAcademicoDAO.delete(departamentoAcademico);
    }

    @Override
    @Transactional
    public void estado(DepartamentoAcademico departamentoAcademico) {
        DepartamentoAcademico departamentoAcademicoBD = departamentoAcademicoDAO.findDepartamentoAcademico(departamentoAcademico.getId());
        if (DepartamentoAcademicoEstadoEnum.CRE.name().equalsIgnoreCase(departamentoAcademicoBD.getEstado())) {
            departamentoAcademicoBD.setEstado(DepartamentoAcademicoEstadoEnum.ACT.name());
        } else if (DepartamentoAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(departamentoAcademicoBD.getEstado())) {
            departamentoAcademicoBD.setEstado(DepartamentoAcademicoEstadoEnum.INA.name());
            departamentoAcademicoBD.setMotivoDesactivacion(departamentoAcademico.getMotivoDesactivacion());
            departamentoAcademicoBD.setFechaDesactivacion(new Date());
        } else {
            departamentoAcademicoBD.setEstado(DepartamentoAcademicoEstadoEnum.ACT.name());
        }
        departamentoAcademicoDAO.update(departamentoAcademicoBD);
    }

    @Override
    public List<DepartamentoCursoDocente> allDepartamentoCursoDocente(List<DepartamentoAcademico> departamentos) {
        if (departamentos == null || departamentos.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> departamentosList = departamentos
                .stream()
                .map(depa -> depa.getId())
                .collect(Collectors.toList());
        logger.debug("{}", departamentosList);
        return departamentoAcademicoDAO.allDepartamentoCursoDocente(departamentosList);
    }

}
