package pe.edu.lamolina.amauta.controller.escalafon.produccion;

import java.util.List;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ProduccionEscalafon;

public interface ProduccionService {

    List<ProduccionEscalafon> allProduccionEscalafonByEscalafon(Escalafon escalafon);

    void save(ProduccionEscalafon produccionEscalafon);

    void eliminar(ProduccionEscalafon produccionEscalafon);

}
