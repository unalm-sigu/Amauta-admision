package pe.edu.lamolina.amauta.controller.mensajeria.tipomsgintranet;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.TipoMensajeIntranet;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface TipoMsgIntranetService {

    List<TipoMensajeIntranet> allByDynatble(DynatableFilter filter);

    public void save(TipoMensajeIntranet tipoMsg, CicloAcademico cicloAcademico, Usuario usuario);

    public void update(TipoMensajeIntranet tipoMsg, CicloAcademico cicloAcademico, Usuario usuario);

    public void eliminar(TipoMensajeIntranet tipoMsg);

}
