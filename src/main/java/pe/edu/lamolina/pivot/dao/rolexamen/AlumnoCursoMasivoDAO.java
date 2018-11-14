package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;

public interface AlumnoCursoMasivoDAO extends EasyDAO<SeccionGrupoRegular> {

    List<SeccionGrupoRegular> allByLetraGrupoRegularAndEstados(
            LetraGrupoRegular letrasGruposRegular, List<SeccionRolExamenEstadoEnum> estados);

    void updateEstado(SeccionGrupoRegular seccionGrupoRegularUpd);

    public List<AlumnoCursoMasivo> allByLetraGrupoRegularAndEstados(CursoMasivoExamen cursoMasivoExamen, List<AlumnoRolExamenEstadoEnum> asList);

}
