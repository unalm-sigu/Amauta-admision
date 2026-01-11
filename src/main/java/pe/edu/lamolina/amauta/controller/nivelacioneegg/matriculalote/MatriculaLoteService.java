package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculalote;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface MatriculaLoteService {

    int procesarMatriculaLote(CicloAcademico ciclo, DataSessionPivot ds);

}
