package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;

public interface AlumnoCursoMasivoDAO extends EasyDAO<AlumnoCursoMasivo> {

    List<AlumnoCursoMasivo> allAlumnoByCursoMasivo(CursoMasivoExamen cursoMasivo);

    List<AlumnoCursoMasivo> allByCursoMasivo(CursoMasivoExamen cursoMasivo, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoCursoMasivo> allAlumnoByRolExamenes(RolExamenes rolExamenes, AlumnoRolExamenEstadoEnum... estados);

    List<AlumnoCursoMasivo> allByCursosMasivos(List<CursoMasivoExamen> cursosMasivoExamenes, AlumnoRolExamenEstadoEnum... estados);

}
