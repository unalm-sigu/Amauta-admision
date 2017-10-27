package pe.edu.lamolina.pivot.controller.general.oficina;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.model.general.Colaborador;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Oficina;

@Service
@Transactional(readOnly = true)
public class OficinaServiceImp implements OficinaService {

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Oficina> allByDynatable(DynatableFilter filter, Compania compania) {
        return oficinaDAO.allByFilter(filter, compania);
    }

    @Override
    public Oficina find(Oficina persona) {
        return oficinaDAO.find(persona.getId());
    }

    @Override
    public void update(Oficina oficina) {
        oficinaDAO.update(oficina);
    }

    @Override
    public void save(Oficina oficina) {
        oficinaDAO.save(oficina);
    }

    @Override
    public void delete(Oficina oficina) {
        oficinaDAO.delete(oficina);
    }

    @Override
    public List<Colaborador> allColaborador(List<Oficina> oficinas) {
        return colaboradorDAO.allColaborador(oficinas);
    }
    
}
