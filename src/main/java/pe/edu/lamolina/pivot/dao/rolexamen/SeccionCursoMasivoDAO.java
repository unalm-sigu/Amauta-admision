package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;

public interface SeccionCursoMasivoDAO extends EasyDAO<SeccionCursoMasivo> {

//    List<SeccionGrupoRegular> allByLetraGrupoRegularAndEstados(
//            LetraGrupoRegular letrasGruposRegular, List<SeccionRolExamenEstadoEnum> estados);
//
//    void updateEstado(SeccionGrupoRegular seccionGrupoRegularUpd);

    List<SeccionCursoMasivo> allByCursoMasivoExamenAndEstados(
            CursoMasivoExamen cursoMasivoExamen, List<SeccionRolExamenEstadoEnum> estados);

}
