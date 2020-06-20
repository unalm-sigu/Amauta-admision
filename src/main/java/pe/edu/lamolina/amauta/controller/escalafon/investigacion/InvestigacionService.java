package pe.edu.lamolina.amauta.controller.escalafon.investigacion;

import java.util.List;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.InvestigacionEscalafon;

public interface InvestigacionService {

    List<InvestigacionEscalafon> allInvestigacionEscalafonByEscalafon(Escalafon escalafon);

    void save(InvestigacionEscalafon investigacionEscalafon);

    void eliminar(InvestigacionEscalafon investigacionEscalafon);

}
