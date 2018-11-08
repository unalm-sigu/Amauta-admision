package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

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
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.rolexamen.SeccionCursoRegular;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionCursoMasivoDAO;

@Service
@Transactional(readOnly = true)
public class GrupoRegularServiceImp implements GrupoRegularService {

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Transactional(readOnly = false)
    public void calcularExamenesGrupoRegular(GrupoRegularExamen grupoRegularExamen, CicloAcademico cicloAcademico) {
        List<CursoMasivoExamen> allCursoMasivoExamenByGrupoRegular = cursoMasivoExamenDAO.allActiveByRolExamen(grupoRegularExamen.getRolExamen());
        List<SeccionCursoMasivo> allSeccionesCursoMasivosActives = seccionCursoMasivoDAO.allActiveByCursosMasivos(allCursoMasivoExamenByGrupoRegular);

        List<Seccion> secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.REGULAR); //grupo horas regulares
        Map<Long, List<Seccion>> grupoHorasMap = TypesUtil.convertListToMapList("grupoHoras.id", secciones);
        
        SeccionCursoRegular seccionCursoRegular=new SeccionCursoRegular();
        
        throw new PhobosException("no pasaras papu");
    }

}
