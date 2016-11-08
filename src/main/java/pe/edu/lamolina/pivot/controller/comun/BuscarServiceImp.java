package pe.edu.lamolina.pivot.controller.comun;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;

@Service
@Transactional(readOnly = true)
public class BuscarServiceImp implements BuscarService {

    @Autowired
    CursoDAO cursoDAO;

    @Override
    public List<Curso> allCursosAutocomplete(String nombre) {
        return cursoDAO.allAutocomplete(nombre);
    }

}
