package pe.edu.lamolina.amauta.controller.escalafon.distincion;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.escalafon.DistincionEscalfonDAO;
import pe.edu.lamolina.model.escalafon.DistincionEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;

@Service
@Transactional
public class DistincionServiceImp implements DistincionService {

    @Autowired
    DistincionEscalfonDAO distincionEscalafonDAO;

    @Override
    public List<DistincionEscalafon> allDistincionByEscalafon(Escalafon escalafon) {
        return distincionEscalafonDAO.allByEscalafon(escalafon);
    }

    @Override
    public void save(DistincionEscalafon distincionEscalafonForm) {
        if (distincionEscalafonForm.getId() != null) {
            DistincionEscalafon distincionEscalafonBD = distincionEscalafonDAO.find(distincionEscalafonForm.getId());
            distincionEscalafonBD.setConfirmado(distincionEscalafonForm.getConfirmado());
            distincionEscalafonBD.setDescripcion(distincionEscalafonForm.getDescripcion());
            distincionEscalafonBD.setFechaPremio(distincionEscalafonForm.getFechaPremio());
            distincionEscalafonBD.setNotaConfirmacion(distincionEscalafonForm.getNotaConfirmacion());
            distincionEscalafonBD.setPais(distincionEscalafonForm.getPais());
            distincionEscalafonBD.setTitulo(distincionEscalafonForm.getTitulo());
            distincionEscalafonDAO.update(distincionEscalafonBD);
        } else {
            distincionEscalafonDAO.save(distincionEscalafonForm);
        }
    }

    @Override
    public void eliminar(DistincionEscalafon distincionEscalafon) {
        distincionEscalafonDAO.delete(distincionEscalafon);
    }

}
