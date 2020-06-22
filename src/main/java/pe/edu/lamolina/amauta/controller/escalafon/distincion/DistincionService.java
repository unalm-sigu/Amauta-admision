package pe.edu.lamolina.amauta.controller.escalafon.distincion;

import java.util.List;
import pe.edu.lamolina.model.escalafon.DistincionEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;

public interface DistincionService {

    List<DistincionEscalafon> allDistincionByEscalafon(Escalafon Escalafon);

    void save(DistincionEscalafon distincionEscalafon);

    void eliminar(DistincionEscalafon distincionEscalafon);

}
