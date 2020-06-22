package pe.edu.lamolina.amauta.controller.escalafon.investigacion;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.escalafon.InvestigacionEscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.InvestigacionEscalafon;

@Service
@Transactional
public class InvestigacionServiceImp implements InvestigacionService {

    @Autowired
    InvestigacionEscalafonDAO investigacionEscalafonDAO;

    @Override
    public List<InvestigacionEscalafon> allInvestigacionEscalafonByEscalafon(Escalafon escalafon) {
        return investigacionEscalafonDAO.allByEscalafon(escalafon);
    }

    @Override
    public void save(InvestigacionEscalafon investigacionEscalafonForm) {
        if (investigacionEscalafonForm.getId() != null) {
            InvestigacionEscalafon investigacionEscalafonBD = investigacionEscalafonDAO.find(investigacionEscalafonForm.getId());
            investigacionEscalafonBD.setArea(investigacionEscalafonForm.getArea());
            investigacionEscalafonBD.setConfirmado(investigacionEscalafonForm.getConfirmado());
            investigacionEscalafonBD.setFechaFin(investigacionEscalafonForm.getFechaFin());
            investigacionEscalafonBD.setFechaInicio(investigacionEscalafonForm.getFechaInicio());
            investigacionEscalafonBD.setInvestigadores(investigacionEscalafonForm.getInvestigadores());
            investigacionEscalafonBD.setNotaConfirmacion(investigacionEscalafonForm.getNotaConfirmacion());
            investigacionEscalafonBD.setTitulo(investigacionEscalafonForm.getTitulo());
            investigacionEscalafonBD.setUrlRepositorio(investigacionEscalafonForm.getUrlRepositorio());
            investigacionEscalafonDAO.update(investigacionEscalafonBD);
        } else {
            investigacionEscalafonDAO.save(investigacionEscalafonForm);
        }

    }

    @Override
    public void eliminar(InvestigacionEscalafon investigacionEscalafon) {
        investigacionEscalafonDAO.delete(investigacionEscalafon);
    }

}
