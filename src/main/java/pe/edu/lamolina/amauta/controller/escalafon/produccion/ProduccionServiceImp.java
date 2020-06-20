package pe.edu.lamolina.amauta.controller.escalafon.produccion;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.escalafon.ProduccionEscalafonDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.ProduccionEscalafon;
import pe.edu.lamolina.model.escalafon.enums.TipoProduccionEscaEnum;

@Service
@Transactional
public class ProduccionServiceImp implements ProduccionService {

    @Autowired
    ProduccionEscalafonDAO produccionEscalafonDAO;

    @Override
    public List<ProduccionEscalafon> allProduccionEscalafonByEscalafon(Escalafon escalafon) {
        return produccionEscalafonDAO.allByEscalafon(escalafon);
    }

    @Override
    public void save(ProduccionEscalafon produccionEscalafonForm) {
        if (produccionEscalafonForm.getId() != null) {
            ProduccionEscalafon produccionEscalafonBD = produccionEscalafonDAO.find(produccionEscalafonForm.getId());
            if (produccionEscalafonForm.getTipoProduccionEscaEnum().equals(TipoProduccionEscaEnum.CIENTIFICA)) {
                produccionEscalafonBD.setSubTipo(produccionEscalafonForm.getSubTipo());
            }
            if (produccionEscalafonForm.getTipoProduccionEscaEnum().equals(TipoProduccionEscaEnum.OTROS)) {
                produccionEscalafonBD.setSubTipo(null);
            }
            produccionEscalafonBD.setAnioProduccion(produccionEscalafonForm.getAnioProduccion());
            produccionEscalafonBD.setAutores(produccionEscalafonForm.getAutores());
            produccionEscalafonBD.setTipo(produccionEscalafonForm.getTipo());
            produccionEscalafonBD.setTitulo(produccionEscalafonForm.getTitulo());
            produccionEscalafonBD.setTituloFuente(produccionEscalafonForm.getTituloFuente());
            produccionEscalafonBD.setUrlRepositorio(produccionEscalafonForm.getUrlRepositorio());
            produccionEscalafonDAO.update(produccionEscalafonBD);
        } else {
            produccionEscalafonDAO.save(produccionEscalafonForm);
        }
    }

    @Override
    public void eliminar(ProduccionEscalafon produccionEscalafon) {
        produccionEscalafonDAO.delete(produccionEscalafon);
    }

}
