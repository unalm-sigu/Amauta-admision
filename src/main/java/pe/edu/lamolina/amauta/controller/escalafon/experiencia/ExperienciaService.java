package pe.edu.lamolina.amauta.controller.escalafon.experiencia;

import java.util.List;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ExperienciaEscalafon;

public interface ExperienciaService {

    void eliminar(ExperienciaEscalafon experienciaEscalafon);

    void save(ExperienciaEscalafon experienciaEscalafon);

    List<ExperienciaEscalafon> allExperienciaByEscalafon(Escalafon escalafon);

}
