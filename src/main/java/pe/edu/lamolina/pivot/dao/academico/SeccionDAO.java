package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

public interface SeccionDAO extends Crud<Seccion> {

    List<Seccion> allByCargaAcademica(DynatableFilter filter, Docente docente);

    Seccion find(Long idSeccion);

    List<Seccion> allByFilter(Long idGrupo);

    Seccion findByCodeCiclo(String codigo, CicloAcademico ciclo);

    List<Seccion> allByCiclo(CicloAcademico ciclo);

    List<Seccion> allByGposSeccion(List<GrupoSeccion> gruposSeccion);

}
