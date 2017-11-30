package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.cursocarrera;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;

@Service
@Transactional(readOnly = true)
public class HorarioCursoCarreraServiceImp implements HorarioCursoCarreraService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    CursoCachimbosDAO cursoCachimbosDAO;
    
    @Override
    public List<CursoCachimbos> allCursoCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return cursoCachimbosDAO.allCursoCachimbos(filter, cicloAcademico);
    }
    
    @Override
    @Transactional
    public void addCurso(CursoCachimbos cursoCachimbos) {
        CursoCachimbos cursoCachimbosDb = cursoCachimbosDAO.findByCursoCiclo(cursoCachimbos);
        if (cursoCachimbosDb == null) {
            cursoCachimbosDAO.save(cursoCachimbos);
        }
    }
    
    @Override
    @Transactional
    public void delete(CursoCachimbos cursoCachimbos) {
        cursoCachimbosDAO.delete(cursoCachimbos);
    }
    
}
