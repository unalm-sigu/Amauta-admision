package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;

public interface DocenteSeccionDAO extends EasyDAO<DocenteSeccion> {

    List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico ciclo);

    List<DocenteSeccion> allByDocente(Docente docente, CicloAcademico ciclo);

    List<DocenteSeccion> allResponsablesByGpoSecciones(List<GrupoSeccion> gruposSeccion, CicloAcademico ciclo);

    List<DocenteSeccion> allBySeccion(Seccion seccion);

    List<DocenteSeccion> allActivosBySeccion(Seccion seccion);

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

    List<DocenteSeccion> allBySecciones(List<Seccion> secciones);

    List<DocenteSeccion> allPrincipalesBySecciones(List<Seccion> secciones);

    void updatePrincipal(DocenteSeccion docenteSeccion);

    void updateDocente(DocenteSeccion docenteSeccion);

    void updatePorcentajeAvance(DocenteSeccion docenteSeccion);

    void updateFechaInicio(DocenteSeccion docenteSeccion);

    void updateFechaFin(DocenteSeccion docenteSeccion);

}
