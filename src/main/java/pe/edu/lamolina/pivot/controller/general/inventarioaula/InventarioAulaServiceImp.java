package pe.edu.lamolina.pivot.controller.general.inventarioaula;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.pivot.dao.almacen.InventarioDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;

@Service
@Transactional(readOnly = true)
public class InventarioAulaServiceImp implements InventarioAulaService {

    @Autowired
    InventarioDAO inventarioDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Override
    public Aula findAula(Long idaula) {
        return aulaDAO.find(idaula);
    }

}
