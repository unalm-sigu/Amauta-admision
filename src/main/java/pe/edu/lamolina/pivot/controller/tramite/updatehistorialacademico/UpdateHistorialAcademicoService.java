package pe.edu.lamolina.pivot.controller.tramite.updatehistorialacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface UpdateHistorialAcademicoService {

    Alumno allInfo(Alumno alumno);

    void updateHistorialAcademico(Alumno alumnoForm, DataSessionPivot ds);

    List<CicloAcademico> allCicloAcademico();

    ObjectNode toJson(Object object);

    List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno);

    List<Curso> allCursoByName(String nombre);

    List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter);

    void updateTramiteDocumentoAcademico(TramiteDocumentoAcademico solicitudConstanciaForm, DataSessionPivot ds);

    TramiteDocumentoAcademico findTramiteDocumentoAcademico(TramiteDocumentoAcademico solicitudConstanciaForm);

    void delete(TramiteDocumentoAcademico solicitudConstancia);

    List<MatriculaResumen> allMatriculaResumenByAlumno(Alumno alumno);

    List<Idioma> allIdiomas();

    List<TipoDocumentoAcademico> allTipoDocumentoAcademico();

    Alumno findAlumno(Alumno alumnoSesssion);

    List<Alumno> allAlumnoByPersona(Persona persona);

    Persona findPersona(Persona persona);

    void fillTipoDocumentoAcademico(ArrayNode arrayTipoDocumentoAcademico);

    void saveTramiteDocumentoAcademico(TramiteDocumentoAcademico tramiteDocumentoAcademico, DataSessionPivot ds);

    void cancelar(TramiteDocumentoAcademico solicitudConstancia);

    List<PrecioDocumento> allPrecioDocumento();

    String getCostoDocumento(TramiteDocumentoAcademico tramiteDoc, Map<Long, List<PrecioDocumento>> preciosMap);

    TramiteDocumentoAcademico findTramite(TramiteDocumentoAcademico tramiteDocumentoAcademicoForm);

    ContenidoCarta findContenidoBoletaByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum);

    PrecioDocumento findPrecioDocumentoByTipoIdioma(TipoDocumentoAcademico tipoDocumento, Idioma idioma);

    List<Alumno> allAlumnoByName(String nombre);

}
