package pe.edu.lamolina.amauta.controller.escalafon.experienciaAsesor;

import java.util.List;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ExperienciaAsesor;

public interface ExperienciaAsesorService {

    List<ExperienciaAsesor> allExperienciaAsesorByEscalafon(Escalafon escalafon);

    void save(ExperienciaAsesor experienciaAsesor);

    void eliminar(ExperienciaAsesor experienciaAsesor);

}
