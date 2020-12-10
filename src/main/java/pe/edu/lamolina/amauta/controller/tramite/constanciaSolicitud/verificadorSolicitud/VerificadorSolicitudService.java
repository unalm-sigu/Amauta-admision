package pe.edu.lamolina.amauta.controller.tramite.constanciaSolicitud.verificadorSolicitud;

import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;

public interface VerificadorSolicitudService {

    void verificarDocumentoAlumno(TramiteDocumentoAcademico documentoAcademico, Alumno alumno);

    void verificarDocumentoAlumno(PlantillaDocumentoAcademico plantillaDocumentoAcademico, TramiteDocumentoAcademico tramiteDocumentoAcademico, Alumno alumno);
}
