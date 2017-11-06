package pe.edu.lamolina.pivot.controller.academico.alumno;

import java.util.List;
import pe.edu.lamolina.pivot.model.academico.Alumno;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface AlumnoFisicoService {

    List<TipoDocIdentidad> allDocumentos();

    List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos);

    void saveAlumno(Alumno alumno, Usuario usuario);

}
