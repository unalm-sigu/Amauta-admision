package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

public interface DocenteSeccionDAO extends Crud<DocenteSeccion> {

    List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico ciclo);

    List<DocenteSeccion> allByDocente(Docente docente, CicloAcademico ciclo);

    List<DocenteSeccion> allResponsablesByGpoSecciones(List<GrupoSeccion> gruposSeccion, CicloAcademico ciclo);

    List<DocenteSeccion> allBySeccion(Seccion seccion);

    List<DocenteSeccion> allPersonasActivasBySeccion(Seccion seccionResponsable);

    List<DocenteSeccion> allPersonasActivasBySecciones(List<Seccion> secciones);

    List<DocenteSeccion> allByGrupoSeccion(GrupoSeccion grupoSeccion);

    DocenteSeccion findByFilter(Docente docente, Seccion seccion);

    DocenteSeccion findByDocenteSeccion(Docente profe, Seccion seccion);

    List<DocenteSeccion> allByCiclo(CicloAcademico ciclo);

    List<DocenteSeccion> allByFilter(Docente docente, Seccion seccion);

    List<DocenteSeccion> allPendientePlan(CicloAcademico ciclo);

    List<DocenteSeccion> allActivosBySecciones(List<Seccion> secciones);

    void deleteDocenteSeccionBySeccion(Seccion seccion);

}
