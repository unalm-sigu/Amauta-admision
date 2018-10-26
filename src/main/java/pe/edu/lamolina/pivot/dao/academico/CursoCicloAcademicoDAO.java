package pe.edu.lamolina.pivot.dao.academico;

import java.math.BigDecimal;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;

public interface CursoCicloAcademicoDAO extends EasyDAO<CursoCicloAcademico> {

    List<CursoCicloAcademico> allByCiclo(CicloAcademico cicloDestino);

    void updatePrecioByTpc(CicloAcademico cicloAcademico, String tpc, BigDecimal precio);

    void deleteAllByCiclo(CicloAcademico ciclo);

    List<CursoCicloAcademico> allByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    List<CursoCicloAcademico> countGpoSeccByCursosCiclo(List<Curso> cursos, CicloAcademico ciclo);

    List<CursoCicloAcademico> allByLista(List<CursoCicloAcademico> cursosCiclos);

}
