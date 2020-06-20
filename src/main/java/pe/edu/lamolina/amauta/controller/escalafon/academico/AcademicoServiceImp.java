package pe.edu.lamolina.amauta.controller.escalafon.academico;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.escalafon.AcademicoEscalafonDAO;
import pe.edu.lamolina.model.escalafon.AcademicoEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;

@Service
@Transactional
public class AcademicoServiceImp implements AcademicoService {

    @Autowired
    AcademicoEscalafonDAO academicoEscalafonDAO;

    @Override
    public List<AcademicoEscalafon> allAcademicoByEscalafon(Escalafon escalafon) {
        return academicoEscalafonDAO.allByEscalafon(escalafon);
    }

    @Override
    public void save(AcademicoEscalafon academicoEscalafonForm) {
        if (academicoEscalafonForm.getId() != null) {
            AcademicoEscalafon academicoEscalafonBD = academicoEscalafonDAO.find(academicoEscalafonForm.getId());
            academicoEscalafonBD.setConfirmado(academicoEscalafonForm.getConfirmado());
            academicoEscalafonBD.setFechaFin(academicoEscalafonForm.getFechaFin());
            academicoEscalafonBD.setFechaInicio(academicoEscalafonForm.getFechaInicio());
            academicoEscalafonBD.setGrado(academicoEscalafonForm.getGrado());
            academicoEscalafonBD.setNotaConfirmacion(academicoEscalafonForm.getNotaConfirmacion());
            academicoEscalafonBD.setPais(academicoEscalafonForm.getPais());
            if (academicoEscalafonForm.getUniversidad() != null) {
                academicoEscalafonBD.setInstitucion(null);
                academicoEscalafonBD.setUniversidad(academicoEscalafonForm.getUniversidad());
            } else {
                academicoEscalafonBD.setUniversidad(null);
                academicoEscalafonBD.setInstitucion(academicoEscalafonForm.getInstitucion());
            }
            academicoEscalafonDAO.update(academicoEscalafonBD);
        } else {
            academicoEscalafonDAO.save(academicoEscalafonForm);
        }
    }

    @Override
    public void eliminar(AcademicoEscalafon academicoEscalafon) {
        academicoEscalafonDAO.delete(academicoEscalafon);
    }

}
