package pe.edu.lamolina.amauta.controller.escalafon.Idioma;

import java.util.List;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.IdiomaEscalafon;

public interface IdiomaEscalafonService {

    List<IdiomaEscalafon> allIdiomaEscalafonByEscalafon(Escalafon Escalafon);

    void save(IdiomaEscalafon idiomaEscalafon);

    void eliminar(IdiomaEscalafon idiomaEscalafon);

}
