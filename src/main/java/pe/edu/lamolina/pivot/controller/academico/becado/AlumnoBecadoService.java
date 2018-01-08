package pe.edu.lamolina.pivot.controller.academico.becado;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoBecado;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoBecadoService {

    List<AlumnoBecado> allAlumnoBecado(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<TipoDocIdentidad> allTiposDocIdentidad();

    List<CicloAcademico> allCicloAcademico();

    List<Alumno> allAlumnoByName(String nombre);

    void save(AlumnoBecado alumnoBecado, Usuario user);

    void update(AlumnoBecado alumnoBecado, Usuario user);

    void delete(AlumnoBecado alumnoBecado);

    AlumnoBecado find(AlumnoBecado alumnoBecado);

}
