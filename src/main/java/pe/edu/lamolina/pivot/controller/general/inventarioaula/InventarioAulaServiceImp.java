package pe.edu.lamolina.pivot.controller.general.inventarioaula;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.almacen.Inventario;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.seguridad.Usuario;
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

    @Override
    public List<Inventario> allByDynatable(DynatableFilter filter, Aula aula) {
        return inventarioDAO.allByDynatable(filter, aula);
    }

    @Override
    public void update(Inventario inventario) {
        inventarioDAO.update(inventario);
    }

    @Override
    public void save(Inventario inventario, Usuario user) {
        inventarioDAO.save(inventario);
    }

    @Override
    public void delete(Inventario inventario) {
        inventarioDAO.delete(inventario);
    }

    @Override
    public Inventario find(Inventario inventario) {
        return inventarioDAO.find(inventario.getId());
    }

}
