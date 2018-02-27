package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;

public interface CursoEquivalenteDAO extends EasyDAO<CursoEquivalente> {

    Integer findMaxGrupoByCursoCurricula(CursoCurricula curso);
    
    void deleteByGrupoCursoCurricula(Integer grupo, CursoCurricula curso);
    
}

