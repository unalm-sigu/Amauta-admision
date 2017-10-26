package pe.edu.lamolina.pivot.controller.academico.docente;

import java.util.List;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.model.general.TipoDocIdentidad;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface DocenteService {

    List<Docente> allByDynatable(DynatableFilter filter);

    Docente find(Docente docente);

    List<TipoDocIdentidad> allDocumentos();

    void save(Docente docente, DataSessionPivot ds);

    String validarEmailByDocente(String email, Docente docente);

    String validarEmailEmpresaByDocente(String email, Docente docente);

    void estado(Docente docente);

    Persona findPersona(Persona personaTmp);

    Docente findDocenteByPersona(Persona persona);

    List<ModalidadEstudio> allModalidadEstudio(Compania compania);

}
