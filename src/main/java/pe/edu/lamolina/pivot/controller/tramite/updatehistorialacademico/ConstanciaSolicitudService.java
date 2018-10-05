package pe.edu.lamolina.pivot.controller.tramite.updatehistorialacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ConstanciaSolicitudService {

    void updateHistorialAcademico(Alumno alumnoForm, DataSessionPivot ds);

    List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno);

    void updateTramiteDocumentoAcademico(TramiteDocumentoAcademico solicitudConstanciaForm, DataSessionPivot ds);

    List<MatriculaResumen> allMatriculaResumenByAlumno(Alumno alumno);

    List<Idioma> allIdiomas();

    Alumno findAlumno(Alumno alumnoSesssion);

    List<Alumno> allAlumnoByPersona(Persona persona);

    Persona findPersona(Persona persona);

    void fillTipoDocumentoAcademico(ArrayNode arrayTipoDocumentoAcademico);

    TramiteDocumentoAcademico findTramite(TramiteDocumentoAcademico tramiteDocumentoAcademicoForm);

    ContenidoCarta findContenidoBoletaByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum);

    PrecioDocumento findPrecioDocumentoByTipoIdioma(TipoDocumentoAcademico tipoDocumento, Idioma idioma);

    List<Alumno> allAlumnoByName(String nombre);

    List<Colaborador> allColaboradorByName(String nombre);

    void updateFotoTemporal(Persona imagenForm);

    public void save(TramiteDocumentoAcademico documentoAcademico, DataSessionPivot ds);

    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter);

    public List<PrecioDocumento> allPrecioDocumento();

    public List<TipoDocumentoAcademico> allTipoDocumentoAcademico();

}
