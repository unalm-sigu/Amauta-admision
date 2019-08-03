package pe.edu.lamolina.pivot.controller.oficinas.matricula.omisoeleccion;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoOmisoEleccion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface OmisoEleccionService {

    public List<Alumno> allDeudaAlumno(DynatableFilter filter);

    public void saveOmision(AlumnoOmisoEleccion omisoEleccion, DataSessionPivot ds);

    public void anularOmision(List<AlumnoOmisoEleccion> omisoEleccion, DataSessionPivot ds);

    public List<String> cargarDeudas(MultipartFile file, String codigoCiclo, DataSessionPivot ds);

    public List<CicloAcademico> allCicloAcademico(CicloAcademico cicloAcademico);

    public List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds);

}
