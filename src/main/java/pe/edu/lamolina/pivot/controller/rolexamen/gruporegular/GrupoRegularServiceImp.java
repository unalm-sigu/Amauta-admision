package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;

@Service
@Transactional(readOnly = true)
public class GrupoRegularServiceImp implements GrupoRegularService {

    @Autowired
    RolExamenesDAO rolExamenesDAO;
    /*
    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;
     */
    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Transactional(readOnly = false)
    public void calcularExamenesGrupoRegular(GrupoRegularExamen grupoRegularExamen, CicloAcademico cicloAcademico) {
        //    List<CursoMasivoExamen> allCursoMasivoExamenByGrupoRegular = cursoMasivoExamenDAO.allActiveByRolExamen(grupoRegularExamen.getRolExamen());
        //  List<SeccionCursoMasivo> allSeccionesCursoMasivosActives = seccionCursoMasivoDAO.allActiveByCursosMasivos(allCursoMasivoExamenByGrupoRegular);

        List<Seccion> secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.REGULAR); //grupo horas regulares
        Map<String, List<Seccion>> grupoHorasMap = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);

        Map matriculadosByGrupo = grupoHorasDAO.countAlumnosGroupByGrupoHoras(new ArrayList(grupoHorasMap.keySet()), cicloAcademico);

        //  SeccionCursoRegular seccionCursoRegular = new SeccionCursoRegular();
        List<GrupoHoras> gruposHoras = grupoHorasDAO.allByTipoGrupoHoraAndCiclo(TipoGrupoHorasEnum.REGULAR, cicloAcademico);

        throw new PhobosException("no pasaras papu");
    }

}
