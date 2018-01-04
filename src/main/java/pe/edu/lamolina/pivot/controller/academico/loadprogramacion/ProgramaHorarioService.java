package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import org.springframework.web.multipart.MultipartFile;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ProgramaHorarioService {

    void loadArchivosHorario(MultipartFile[] files, CicloAcademico ciclo, DataSessionPivot ds);

}
