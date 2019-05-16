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
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class CursosExcluidosServiceImp implements CursosExcluidosService {

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    CursoExcluidoDAO cursoExcluidoDAO;

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Autowired
    SemanaExamenDAO semanaExamenDAO;

    @Override
    public List<CursoExcluido> allCursosExcluidosByRolExamenes(RolExamenes rolExamenes) {
        return cursoExcluidoDAO.allByRolExamenes(rolExamenes);
    }

    @Override
    public List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico) {
        return cursoMasivoExamenDAO.allRolExamenesByCicloActivo(cicloAcademico);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public void anularExclusion(CursoExcluido cursoExcluido, DataSessionPivot ds) {
        CursoExcluido cursoExcluidoUpd = new CursoExcluido(cursoExcluido.getId());
        cursoExcluidoUpd.setEstadoEnum(EstadoEnum.ANU);
        cursoExcluidoDAO.updateAnulacion(cursoExcluidoUpd);
    }

    @Override
    public RolExamenes findRolExamenes(long rolExamenId) {
        RolExamenes rolExamenes = rolExamenesDAO.find(rolExamenId);
        List<SemanaExamen> semanaExamens = semanaExamenDAO.allByRolExamenes(rolExamenes);
        rolExamenes.setSemanasExamen(semanaExamens);
        return rolExamenes;
    }

}
