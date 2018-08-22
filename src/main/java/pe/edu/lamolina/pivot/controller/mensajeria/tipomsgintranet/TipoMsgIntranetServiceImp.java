package pe.edu.lamolina.pivot.controller.mensajeria.tipomsgintranet;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.TipoMensajeIntranet;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.mensajeria.TipoMensajeIntranetDAO;

@Service
@Transactional(readOnly = true)
public class TipoMsgIntranetServiceImp implements TipoMsgIntranetService {

    @Autowired
    TipoMensajeIntranetDAO tipoMensajeIntranetDAO;

    @Override
    public List<TipoMensajeIntranet> allByDynatble(DynatableFilter filter) {
        return tipoMensajeIntranetDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void save(TipoMensajeIntranet tipoMsg, CicloAcademico cicloAcademico, Usuario usuario) {
        tipoMensajeIntranetDAO.save(tipoMsg);
    }

    @Override
    @Transactional
    public void update(TipoMensajeIntranet tipoMsg, CicloAcademico cicloAcademico, Usuario usuario) {
        ObjectUtil.printAttr(tipoMsg);
        tipoMensajeIntranetDAO.update(tipoMsg);
    }

    @Override
    @Transactional
    public void eliminar(TipoMensajeIntranet tipoMsg) {
        tipoMensajeIntranetDAO.delete(tipoMsg);
    }

}
