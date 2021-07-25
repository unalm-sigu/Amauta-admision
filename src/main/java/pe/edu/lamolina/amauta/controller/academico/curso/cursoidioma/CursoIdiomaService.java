package pe.edu.lamolina.amauta.controller.academico.curso.cursoidioma;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.NombreCurso;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface CursoIdiomaService {

    public List<NombreCurso> allByDynatable(DynatableFilter filter);

    public void save(NombreCurso nombreCurso, Usuario usuario);

    public void delete(Long idNombreCurso);

    public void update(NombreCurso nombreCurso);
    
}
