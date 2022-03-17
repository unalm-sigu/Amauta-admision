package pe.edu.lamolina.amauta.controller.matricula.nivelacion;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface MatriculableNivelacionService {

    public void ClonarNivelacionDTO(DataSessionPivot ds, ClonarNivelacionDTO clonarNivelacionDTO);

    public void generarPrioridad(CicloAcademico cicloDestino);

}
