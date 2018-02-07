package pe.edu.lamolina.pivot.controller.academico.profesor;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ProfesorService {

    List<Docente> allByDynatable(DynatableFilter filter);

    Docente find(Docente docente);

    List<TipoDocIdentidad> allDocumentos();

    void save(Docente docente, DataSessionPivot ds);

    String validarEmailByDocente(String email, Docente docente);

    String validarEmailEmpresaByDocente(String email, Docente docente);

    void estado(Docente docente);

    Persona findPersonaByDocIdentidad(Persona personaTmp);

    Docente findDocenteByPersona(Persona persona);

    List<ModalidadEstudio> allModalidadEstudio(Compania compania);

    Persona findPersona(Persona persona);

    void update(Docente docente, DataSessionPivot ds);

}
