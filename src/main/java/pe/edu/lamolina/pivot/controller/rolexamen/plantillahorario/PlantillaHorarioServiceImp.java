package pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;

@Service
@Transactional(readOnly = true)
public class PlantillaHorarioServiceImp implements PlantillaHorarioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Autowired
    SemanaExamenDAO semanaExamenDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Override
    public RolExamenes findRolExamenes(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        List<SemanaExamen> semanasExamen = semanaExamenDAO.allByRolExamenes(rolExamenes);
        rolExamenes.setSemanasExamen(semanasExamen);
        return rolExamenes;
    }

    @Override
    public void calcularPlantillaHorario(SemanaExamen semanaExamen) {
        List<GrupoHoras> gruposHoras = this.allGrupoHorasBySemanaExamen(semanaExamen);

    }

    @Override
    public List<GrupoHoras> allGrupoHorasBySemanaExamen(SemanaExamen semanaExamen) {
        semanaExamen = semanaExamenDAO.find(semanaExamen.getId());
        RolExamenes rolExamenes = rolExamenesDAO.find(semanaExamen.getRolExamenes().getId());

        Map<Long, Integer> gruposCount = grupoHorasDAO.allGruposCountBySemanaExamen(semanaExamen, rolExamenes.getEventoCicloAcademico().getCicloAcademico(), TipoGrupoHorasEnum.REGULAR);
        List<Long> gruposIdsFiltered = gruposCount.entrySet().stream().filter(x -> x.getValue() >= 2).map(x -> x.getKey()).collect(Collectors.toList());

        List<GrupoHoras> gruposHoras = grupoHorasDAO.allGrupoHoras(gruposIdsFiltered);
        for (GrupoHoras gruposHora : gruposHoras) {
            List<DiaHoraGrupo> diasHorasGrupo = diaHoraGrupoDAO.allByGrupoCiclo(gruposHora, rolExamenes.getEventoCicloAcademico().getCicloAcademico());
            gruposHora.setDiaHoraGrupo(diasHorasGrupo);
        }
        return gruposHoras;
    }

}
