package pe.edu.lamolina.pivot.controller.academico.profesor.informacionprofesor;

import java.util.List;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

public interface InformacionProfesorService {

    Docente findDocente(Docente docente);

    List<TipoDocIdentidad> allDocumentos();

    List<ModalidadEstudio> allModalidadEstudio(Compania compania);

}
