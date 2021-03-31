package pe.edu.lamolina.amauta.controller.academico.profesor.informacionprofesor;

import java.util.List;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.EmpresaEtiquetada;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCuentaBancaria;
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

    List<EmpresaEtiquetada> allBancos();

    List<PersonaCuentaBancaria> allCtasBancarias(Persona persona);

    Persona findPersona(Persona persona);

    void saveCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds);

    void deleteCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds);

    void activarCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds);

    void updateDocentePersona(Persona persona, Long idDocente, DataSessionPivot ds);

}
