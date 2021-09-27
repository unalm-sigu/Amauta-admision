package pe.edu.lamolina.amauta.controller.subvenciones.informe;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bienestar.InformeSubvencionado;
import pe.edu.lamolina.model.general.Persona;

public interface InformeSubvencionService {

    List<InformeSubvencionado> allInformesByDynatble(Persona supervisor, CicloAcademico ciclo, DynatableFilter filter);

    void aprobarInforme(InformeSubvencionado informe, Persona supervisor, DataSessionPivot ds);

    void observarInforme(InformeSubvencionado informe, Persona persona, DataSessionPivot ds);

}
