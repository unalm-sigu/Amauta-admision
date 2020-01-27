package pe.edu.lamolina.pivot.dao.academico;

import java.math.BigDecimal;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;

public interface CursoCicloAcademicoDAO extends EasyDAO<CursoCicloAcademico> {

    List<CursoCicloAcademico> allByCiclo(CicloAcademico ciclo);

    void updatePrecioByTpc(CicloAcademico cicloAcademico, String tpc, BigDecimal precio);

    void deleteAllByCiclo(CicloAcademico ciclo);

    List<CursoCicloAcademico> allByCiclo(CicloAcademico cicloDestino, CicloAcademicoEstadoEnum... estadoEnum);

    List<CursoCicloAcademico> allByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    List<CursoCicloAcademico> countGpoSeccByCursosCiclo(List<Curso> cursos, CicloAcademico ciclo);

    List<CursoCicloAcademico> allByLista(List<CursoCicloAcademico> cursosCiclos);

    List<CursoCicloAcademico> allByCursosCiclo(List<Curso> cursos, CicloAcademico ciclo);

    CursoCicloAcademico findByCursoCiclo(Curso curso, CicloAcademico ciclo);

    void updateColumns(CursoCicloAcademico cursoCicloAcademico, String... columns);

    List<CursoCicloAcademico> allByCicloAndNombre(CicloAcademico cicloAcademico, String nombre);

    int updateList(List<CursoCicloAcademico> cursosCiclos, String... columnas);

    int saveList(List<CursoCicloAcademico> cursosCiclo);

}
