package pe.edu.lamolina.pivot.controller.rolexamen.plantillahorario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PlantillaHorarioService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    RolExamenes findRolExamenes(RolExamenes rolExamenes);

    void calcularPlantillaHorario(RolExamenes rolExamenes);

    List<GrupoHoras> allGrupoHorasBySemanaExamen(SemanaExamen semanaExamen);

    List<GrupoHorasExamen> allGrupoHorasExamenByRolExamen(RolExamenes rolExamenes, DynatableFilter filter);

    GrupoHorasExamen findGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen);

    void agregarFechoHoraGrupoExamen(FechaHoraGrupoExamen fechaHoraGrupoExamen);

    List<Dia> allDias();

    List<FechaHoraGrupoExamen> allFechaHoraGrupoExamenBySemanaExamen(SemanaExamen semanaExamen);

    List<FechaHoraGrupoExamen> allFechaHoraGrupoExamenByRolExamen(RolExamenes rolExamenes);

    List<Hora> allHoras();

    void deleteFechaHoraGrupoExamen(FechaHoraGrupoExamen fechaHoraGrupoExamen);

    void deletePlantillaHorario(RolExamenes rolExamenes);

    void confirmarPlantillaHorario(RolExamenes rolExamenes, DataSessionPivot ds);

}
