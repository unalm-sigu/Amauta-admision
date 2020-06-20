package pe.edu.lamolina.amauta.controller.escalafon;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.escalafon.AreaInvestigacion;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.EscalafonConfirmBean;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface EscalafonService {

    List<Escalafon> allDynatable(DynatableFilter filter);

    Escalafon save(Escalafon escalafon, Usuario usuario);

    void eliminar(Escalafon escalafon);

    Escalafon loadEscalafon(Long idEscalafon);

    List<Idioma> allIdioma();

    List<AreaInvestigacion> allAreaInvestigacion();

    Escalafon updateGeneral(Escalafon escalafon, Usuario usuario);

    Escalafon findEscalafon(Escalafon escalafon);

    void confirmarEscalafon(EscalafonConfirmBean escalafonConfirmBean);

}
