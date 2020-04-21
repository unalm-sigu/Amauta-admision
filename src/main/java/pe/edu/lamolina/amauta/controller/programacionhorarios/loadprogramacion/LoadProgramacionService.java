package pe.edu.lamolina.amauta.controller.programacionhorarios.loadprogramacion;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface LoadProgramacionService {

    Map<String, String> loadArchivosHorario(MultipartFile[] files);

    void inicioProcesarArchivos(Map<String, String> rutasFiles, CicloAcademico ciclo, DataSessionPivot ds);

    void inicioProcesarArchivoAlumno(Map<String, String> rutas, CicloAcademico cicloAcademico, DataSessionPivot ds);

    Map<String, String> loadArchivosAlumnos(MultipartFile[] file);

}
