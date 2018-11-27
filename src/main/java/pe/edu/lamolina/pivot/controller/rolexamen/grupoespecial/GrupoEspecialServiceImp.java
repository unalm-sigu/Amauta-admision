package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;

@Service
@Transactional(readOnly = true)
public class GrupoEspecialServiceImp implements GrupoEspecialService {

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    AlumnoGrupoEspecialDAO alumnoGrupoEspecialDAO;

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Override
    public List<SeccionGrupoEspecial> allSeccionesGrupoEspecialByRolExamenes(DynatableFilter filter, RolExamenes rolExamenes) {
        List<SeccionGrupoEspecial> seccionesGrupoEspecial = seccionGrupoEspecialDAO.allByDynatableAndRolExamenes(filter, rolExamenes);
        Map<Long, Integer> mapAlumnosBySeccion = alumnoGrupoEspecialDAO.countBySeccionesGrupoEspecial(seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum.ACT);
        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspecial) {
            seccionGrupoEspecial.setAlumnosEspecialesActivosCount(mapAlumnosBySeccion.get(seccionGrupoEspecial.getId()) == null ? 0 : mapAlumnosBySeccion.get(seccionGrupoEspecial.getId()));
        }
        return seccionesGrupoEspecial;
    }

}
