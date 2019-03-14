package pe.edu.lamolina.pivot.controller.rolexamen.cursosexcluidos;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class CursosExcluidosServiceImp implements CursosExcluidosService {

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    CursoExcluidoDAO cursoExcluidoDAO;

    @Override
    public List<CursoExcluido> allCursosExcluidosByRolExamenes(RolExamenes rolExamenes) {
        return cursoExcluidoDAO.allByRolExamenes(rolExamenes);
    }

    @Override
    public List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico) {
        return cursoMasivoExamenDAO.allRolExamenesByCicloActivo(cicloAcademico);
    }

    @Override
    public void excluirCurso(CursoExcluido cursoExcluido, DataSessionPivot ds) {
        CursoExcluido cursoExcluidoFound = cursoExcluidoDAO.findActiveByCursoAndRolExamenes(cursoExcluido.getCurso(), cursoExcluido.getRolExamenes());
        if (cursoExcluidoFound != null) {
            throw new PhobosException("El curso ya estaba excluido, verifique.");
        }

        cursoExcluido.setFechaRegistro(ds.getFechaAccionAudit());
        cursoExcluido.setUserRegistro(ds.getUsuario());
        cursoExcluido.setEstadoEnum(EstadoEnum.ACT);
        cursoExcluidoDAO.save(cursoExcluido);
    }

}
