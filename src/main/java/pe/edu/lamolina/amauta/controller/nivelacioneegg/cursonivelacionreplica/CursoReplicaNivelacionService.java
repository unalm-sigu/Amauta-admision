package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacionreplica;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacionreplica.dto.CursoReplicaDTO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Curso;

public interface CursoReplicaNivelacionService {

    List<Curso> allByDynatable(DynatableFilter filter);

    List<Curso> allCursos(String nombre);

    int saveRelacionRegular(CursoReplicaDTO cursoReplicaDTO, DataSessionPivot ds);

}
