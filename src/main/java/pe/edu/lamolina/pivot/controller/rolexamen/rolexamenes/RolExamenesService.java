package pe.edu.lamolina.pivot.controller.rolexamen.rolexamenes;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface RolExamenesService {

    RolExamenes findRolExamenes(long rolExamenId);

    List<EventoCicloAcademico> allEventoCicloAcademicos(CicloAcademico cicloAcademico);

    List<RolExamenes> allRolExamenes(DynatableFilter filter, CicloAcademico cicloAcademico);

    void save(RolExamenes rolExamenes, DataSessionPivot ds);

    void update(RolExamenes rolExamenes, DataSessionPivot ds);

    List<Hora> allHoras();

    List<SemanaExamen> allSemanaExamenByEventoCiclo(EventoCicloAcademico eventoCicloAcademico);

    void publicarRolExamen(RolExamenes rolExamenes, DataSessionPivot ds);

    void eliminarConfiguracion(RolExamenes rolExamenes, DataSessionPivot ds);

    void fijarHorarioAula(RolExamenes rolExamenes, DataSessionPivot ds);
    
    void cerrar(RolExamenes rolExamenes, DataSessionPivot ds);
    
    void modificar(RolExamenes rolExamenes, DataSessionPivot ds);

}
