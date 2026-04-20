package pe.edu.lamolina.amauta.dao.bienestar;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.amauta.controller.programacionhorarios.tramiteaula.ReservaAulaBean;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.tramite.ReservaAula;

public interface ReservaAulaDAO extends EasyDAO<ReservaAula> {

    List<ReservaAula> allDynatableFilter(DynatableFilter filter);

    ReservaAula find(ReservaAula reservaAula);

    List<ReservaAulaBean> allReservaTipoAmbiente();

    List<ReservaAula> allByDocente(DynatableFilter filter, Docente docente);

}
