package pe.edu.lamolina.pivot.controller.academico.intercambio;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoIntercambio;
import pe.edu.lamolina.model.academico.BecaEstudio;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AlumnoIntercambioService {

    List<AlumnoIntercambio> allAlumnoBecado(DynatableFilter filter, CicloAcademico cicloAcademico);

    List<TipoDocIdentidad> allTiposDocIdentidad();

    List<CicloAcademico> allCicloAcademico();

    List<Alumno> allAlumnoByName(String nombre);

    void update(AlumnoIntercambio alumnoBecado);

    void delete(AlumnoIntercambio alumnoBecado);

    AlumnoIntercambio find(AlumnoIntercambio alumnoBecado);

    List<BecaEstudio> allBeca(String nombre);

    void save(AlumnoIntercambio alumnoBecado, Usuario user);

}
