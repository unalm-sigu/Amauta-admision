package pe.edu.lamolina.amauta.controller.escalafon.academico;

import java.util.List;
import pe.edu.lamolina.model.escalafon.AcademicoEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;

public interface AcademicoService {

    List<AcademicoEscalafon> allAcademicoByEscalafon(Escalafon escalafon);

    void save(AcademicoEscalafon academicoEscalafon);

    void eliminar(AcademicoEscalafon academicoEscalafon);

}
