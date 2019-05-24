package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;

public interface DocenteSeccionDAO extends EasyDAO<DocenteSeccion> {

    List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico ciclo);

    List<DocenteSeccion> allByDocente(Docente docente, CicloAcademico ciclo);

    List<DocenteSeccion> allByModalidadEstudioCicloAcademico(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico);

    List<DocenteSeccion> allResponsablesByGpoSecciones(List<GrupoSeccion> gruposSeccion, CicloAcademico ciclo);

    List<DocenteSeccion> allDocentesPrincipalesByGpoSecciones(List<GrupoSeccion> gruposSeccion);

    List<DocenteSeccion> allPersonasActivasBySecciones(Seccion seccionResponsable);

    List<DocenteSeccion> allPersonasActivasBySecciones(List<Seccion> secciones);

    List<DocenteSeccion> allActivosBySecciones(List<Seccion> secciones);

    List<DocenteSeccion> allActivosBySeccion(Seccion seccion);

    List<DocenteSeccion> allActivosByCiclo(CicloAcademico cicloAcademico);

    List<DocenteSeccion> allByGrupoSeccion(GrupoSeccion grupoSeccion);

    DocenteSeccion findByFilter(Docente docente, Seccion seccion);

    DocenteSeccion findWithPersonaByDocenteSeccion(Docente profe, Seccion seccion);

    List<DocenteSeccion> allByCiclo(CicloAcademico ciclo);

    List<DocenteSeccion> allByFilter(Docente docente, Seccion seccion);

    List<DocenteSeccion> allPendientePlan(CicloAcademico ciclo);

    List<DocenteSeccion> allBySeccion(Seccion seccion);

    void deleteDocenteSeccionBySeccion(Seccion seccion);

    List<DocenteSeccion> allBySecciones(List<Seccion> secciones);

    List<DocenteSeccion> allPrincipalesBySecciones(List<Seccion> secciones);

    void updatePrincipal(DocenteSeccion docenteSeccion);

    void updateDocente(DocenteSeccion docenteSeccion);

    void updatePorcentajeAvance(DocenteSeccion docenteSeccion);

    void updateFechaInicio(DocenteSeccion docenteSeccion);

    void updateFechaFin(DocenteSeccion docenteSeccion);

    List<DocenteSeccion> allPrincipalesBySeccion(List<Seccion> secciones);

    List<DocenteSeccion> allSinNNByCicloModalidad(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio);

    List<DocenteSeccion> allActivosByDocenteCiclo(Docente docente, CicloAcademico ciclo);

    List<DocenteSeccion> allSeccionByClone(List<Seccion> secciones);

    void deleteAllByCiclo(CicloAcademico ciclo);

    List<DocenteSeccion> allSinNNByCicloModalidadReporte(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio);

    List<DocenteSeccion> allActivosBySeccionesOrderPrincipalLimit(List<Seccion> secciones);

    List<DocenteSeccion> allByGrupoSeccionForUpdateFecha(GrupoSeccion grupoSeccion);

    public List<DocenteSeccion> allResponsableBySeccionCiclo(List<Seccion> secciones, CicloAcademico ciclo);

    DocenteSeccion findPrincipalBySeccion(Seccion seccion);

    public List<DocenteSeccion> allByCiclo(CicloAcademico ciclo, List<EstadoEnum> asList, List<SeccionEstadoEnum> asList0);

}
