package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacionreplica.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.lamolina.model.academico.Curso;

@Getter
@Setter
@NoArgsConstructor
public class CursoReplicaDTO {

    private Curso curso;
    private List<Curso> cursosRegulares;

}
