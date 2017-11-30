package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

public interface GpoSeccionService {

    List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    GpoSeccionResumen resumen();

    List<AnexoBoletin> allAnexosSuperiores();

    List<Curso> allCursosForProgramacion(String nomString);

    List<AnexoBoletin> allAnexoBoletionHijos();

    AnexoBoletin findAnexoBoletin(Long idAnexoBoletin);

    Curso findCurso(Long id);

    GrupoSeccion saveGpoSeccionHeader(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico);

    GrupoSeccion findGpoSeccion(Long id);

    List<Seccion> allSeccionesByGrupo(GrupoSeccion grupoSeccion);

    void addSeccion(GrupoSeccion grupoSeccion);

    void addDocenteSeccion(Seccion seccion);

    void deleteSeccion(Seccion seccion);

    List<DocenteSeccion> allDocentesSeccionBySeccion(Seccion seccion);

}
