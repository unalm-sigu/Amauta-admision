package pe.edu.lamolina.amauta.controller.academico.renuncia;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface AlumnoRenunciaService {

    public List<Postulante> allAlumnosbyDynatable(DynatableFilter filter);

    public void apply(Postulante postulante, DataSessionPivot ds);

}
