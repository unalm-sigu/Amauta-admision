package pe.edu.lamolina.amauta.controller.programacionhorarios.resumen;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;

import java.util.List;

public interface ResumenProgramacionService {
    DepartamentoAcademico findDepartamento(Long idDepartamentoAcad);
    AnexoBoletin findAnexoBoletin(Long idAnexoBoletin);
    public List<DepartamentoAcademico> allDepartamentosAcademicos();
    List<DepartamentoCursosProgramadosDTO> countGroupsByFilter(List<Long> anexos, CicloAcademico cicloAcademico, AnexoBoletin departamentoAcademico);
    List<AnexoBoletin> allActiveAnexos(DynatableFilter filter, CicloAcademico cicloAcademico);
    List<DepartamentoCursosProgramadosDTO> obtenerEstadisticas();
    List<GrupoSeccion> allGrupoSeccionByFilterDyna(CicloAcademico cicloAcademico, AnexoBoletin anexoBoletin, DynatableFilter dynatableFilter);

}
