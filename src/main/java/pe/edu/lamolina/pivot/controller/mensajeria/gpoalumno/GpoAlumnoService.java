package pe.edu.lamolina.pivot.controller.mensajeria.gpoalumno;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface GpoAlumnoService {

    public List<GrupoAlumno> allByDynatble(DynatableFilter filter);

    public void save(GrupoAlumno gpoAlumno, CicloAcademico cicloAcademico, Usuario usuario);

    public void update(GrupoAlumno gpoAlumno, CicloAcademico cicloAcademico, Usuario usuario);

    public void eliminar(GrupoAlumno gpoAlumno);

}
