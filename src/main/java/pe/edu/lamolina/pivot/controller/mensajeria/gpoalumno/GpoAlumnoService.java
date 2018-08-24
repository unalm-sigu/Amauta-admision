package pe.edu.lamolina.pivot.controller.mensajeria.gpoalumno;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DetalleGrupoAlumno;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface GpoAlumnoService {

    List<GrupoAlumno> allByDynatble(DynatableFilter filter);

    void save(GrupoAlumno gpoAlumno, CicloAcademico cicloAcademico, Usuario usuario);

    void update(GrupoAlumno gpoAlumno, CicloAcademico cicloAcademico, Usuario usuario);

    void eliminar(GrupoAlumno gpoAlumno);

    List<DetalleGrupoAlumno> allDetallesByDynatbleGrupoAlumno(DynatableFilter filter, GrupoAlumno grupo);

    GrupoAlumno findGrupoById(Long id);

    void saveDetalleGrupo(DetalleGrupoAlumno detalleGrupo);

    void eliminarDetalle(DetalleGrupoAlumno detalleGrupo);

}
