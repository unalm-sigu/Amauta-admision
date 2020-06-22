package pe.edu.lamolina.amauta.controller.escalafon.Idioma;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.escalafon.IdiomaEscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.IdiomaEscalafon;

@Service
@Transactional
public class IdiomaEscalafonServiceImp implements IdiomaEscalafonService {

    @Autowired
    IdiomaEscalafonDAO idiomaEscalafonDAO;

    @Override
    public List<IdiomaEscalafon> allIdiomaEscalafonByEscalafon(Escalafon Escalafon) {
        return idiomaEscalafonDAO.allByEscalafon(Escalafon);
    }

    @Override
    public void save(IdiomaEscalafon idiomaEscalafonForm) {
        if (idiomaEscalafonForm.getLenguaMaterna()) {
            this.updateLenguaMaterna(idiomaEscalafonForm.getEscalafon());
        }
        if (idiomaEscalafonForm.getId() != null) {
            IdiomaEscalafon idiomaEscalafonBD = idiomaEscalafonDAO.find(idiomaEscalafonForm.getId());
            idiomaEscalafonBD.setLenguaMaterna(idiomaEscalafonForm.getLenguaMaterna());
            idiomaEscalafonBD.setConversacion(idiomaEscalafonForm.getConversacion());
            idiomaEscalafonBD.setLectura(idiomaEscalafonForm.getLectura());
            idiomaEscalafonBD.setEscritura(idiomaEscalafonForm.getEscritura());
            idiomaEscalafonBD.setIdioma(idiomaEscalafonForm.getIdioma());
            if (idiomaEscalafonForm.getIdioma().getNombre().equals("Otros")) {
                idiomaEscalafonBD.setIdiomaOtro(idiomaEscalafonForm.getIdiomaOtro());
            } else {
                idiomaEscalafonBD.setIdiomaOtro(null);
            }
            idiomaEscalafonDAO.update(idiomaEscalafonBD);
        } else {
            idiomaEscalafonDAO.save(idiomaEscalafonForm);
        }
    }

    @Override
    public void eliminar(IdiomaEscalafon idiomaEscalafon) {
        idiomaEscalafonDAO.delete(idiomaEscalafon);
    }

    private void updateLenguaMaterna(Escalafon escalafon) {
        List<IdiomaEscalafon> listIdiomaEscalafon = idiomaEscalafonDAO.allByEscalafon(escalafon);
        for (IdiomaEscalafon item : listIdiomaEscalafon) {
            item.setLenguaMaterna(Boolean.FALSE);
            idiomaEscalafonDAO.update(item);
        }
    }
}
