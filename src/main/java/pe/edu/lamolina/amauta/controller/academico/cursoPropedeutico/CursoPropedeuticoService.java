package pe.edu.lamolina.amauta.controller.academico.cursoPropedeutico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AlumnoCursoPropedeutico;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface CursoPropedeuticoService {

    public List<MatriculaResumen> findMatriculaResumen(String nombre, CicloAcademico cicloAcademico);

    public List<Seccion> findSeccion(String nombre, CicloAcademico cicloAcademico);

    public void save(AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, CicloAcademico cicloAcademico, Usuario usuario);

    public void update(AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, CicloAcademico cicloAcademico, Usuario usuario);

    public List<AlumnoCursoPropedeutico> list(DynatableFilter filter, CicloAcademico cicloAcademico);

    public void eliminarDeudaAlumnoCursoPropedeutico(Long idAlumnoCursoPropedeutico, CicloAcademico cicloAcademico, Usuario usuario);

}
