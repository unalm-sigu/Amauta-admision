package pe.edu.lamolina.amauta.controller.programacionhorarios.reporte;

import java.util.List;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;

public interface ReporteProgramacionService {

    List<MatriculaPreBean> allMatriculaPregrado(CicloAcademico cicloAcademico, String facultad);

    public List<Carrera> allCarrera();

    public List<Facultad> allFacultadesPre(DataSessionPivot ds);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    public List<Carrera> searchAllCarrera(String nombre);
    
}
