package pe.edu.lamolina.pivot.controller.academico.profesor.informacionprofesor;

import java.util.List;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.rrhh.CategoriaDocente;
import pe.edu.lamolina.model.rrhh.DedicacionDocente;
import pe.edu.lamolina.model.rrhh.SituacionDocente;

public interface InformacionProfesorService {

    Docente findDocente(Docente docente);

    List<SituacionDocente> allSituaciones();

    List<CategoriaDocente> allCategorias();

    List<DedicacionDocente> allDedicaciones();

    List<TipoDocIdentidad> allDocumentos();

    List<ModalidadEstudio> allModalidadEstudio(Compania compania);

    String validarEmailByPersona(String email, Persona persona);

    String validarEmailEmpresaByPersona(String email, Persona persona);

    List<Hora> allHoras();

}
