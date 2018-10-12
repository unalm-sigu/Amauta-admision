package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface LoadProgramacionService {

    Map<String, String> loadArchivosHorario(MultipartFile[] files);

    void inicioProcesarArchivos(Map<String, String> rutasFiles, CicloAcademico ciclo, DataSessionPivot ds);

}
