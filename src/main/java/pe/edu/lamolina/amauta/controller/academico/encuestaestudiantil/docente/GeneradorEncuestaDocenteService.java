package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.docente;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.ModalidadEstudio;

public interface GeneradorEncuestaDocenteService {

    void generarEncuesta(CicloAcademico cicloAcademico, ModalidadEstudio encuestaModalida, DataSessionPivot ds);

    void generarEncuestaDocente(DocenteSeccion docenteSeccion, CicloAcademico ciclo, ModalidadEstudio encuentarModalidad, DataSessionPivot ds);

    void desactivarEncuestaDocente(EncuestaDocente encuestaForm, CicloAcademico ciclo, DataSessionPivot ds);

    void activarEncuestaDocente(EncuestaDocente encuestaBD, CicloAcademico ciclo, DataSessionPivot ds);

}
