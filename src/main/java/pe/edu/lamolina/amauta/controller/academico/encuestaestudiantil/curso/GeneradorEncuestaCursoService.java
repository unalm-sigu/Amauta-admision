package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.curso;

import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.ModalidadEstudio;

public interface GeneradorEncuestaCursoService {

    void generarEncuesta(ModalidadEstudio encuentarModalidad, CicloAcademico cicloAcademico, DataSessionPivot ds);

}
