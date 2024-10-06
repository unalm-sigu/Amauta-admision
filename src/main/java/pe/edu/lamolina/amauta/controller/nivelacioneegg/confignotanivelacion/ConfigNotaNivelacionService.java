package pe.edu.lamolina.amauta.controller.nivelacioneegg.confignotanivelacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.ModalidadTemaCiclo;

public interface ConfigNotaNivelacionService {

    void revisarNotasExamen(CicloAcademico ciclo, DataSessionPivot ds);

    void revisarDatos(CicloAcademico ciclo, DataSessionPivot ds);

    List<ModalidadTemaCiclo> allConfiguracionsByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    void saveConfig(ModalidadTemaCiclo configuracion, DataSessionPivot ds);

    int activarTodos(CicloAcademico ciclo, DataSessionPivot ds);

    void activar(ModalidadTemaCiclo config, DataSessionPivot ds);

    void desactivar(ModalidadTemaCiclo config, DataSessionPivot ds);

}
