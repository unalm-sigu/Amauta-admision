package pe.edu.lamolina.amauta.controller.academico.curso.cursoidioma;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static javax.management.Query.attr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreCursoDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.NombreCurso;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
@Transactional(readOnly = true)
public class CursoIdiomaServiceImp implements CursoIdiomaService {

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    NombreCursoDAO nombreCursoDAO;

    @Override
    public List<NombreCurso> allByDynatable(DynatableFilter filter) {
       
        List<Curso> cursos = cursoDAO.allByDynatableNombreCurso(filter);
        
        List<NombreCurso> nombreCursos = new ArrayList();
        
        if (cursos.isEmpty()) {
            return nombreCursos;
        }

        List<NombreCurso> nombreCursosDB = nombreCursoDAO.allByCursosIdioma(cursos, new Idioma(3));

        Map<Long, NombreCurso> nombreCursosXcurso = TypesUtil.convertListToMap("curso.id", nombreCursosDB);

        for (Curso curso : cursos) {

            NombreCurso nombreCurso = nombreCursosXcurso.get(curso.getId());

            if (nombreCurso == null) {
                
                nombreCurso = new NombreCurso();
                nombreCurso.setIdioma(new Idioma(3));
                nombreCurso.setCurso(curso);
            }

            nombreCursos.add(nombreCurso);
        }
        
        return nombreCursos;
    }

    @Override
    @Transactional
    public void save(NombreCurso nombreCurso, Usuario usuario) {
        nombreCurso.setUserRegistro(usuario);
        nombreCurso.setFechaRegistro(new Date());
        nombreCursoDAO.save(nombreCurso);
    }

    @Override
    @Transactional
    public void delete(NombreCurso nombreCurso) {
        nombreCursoDAO.delete(nombreCurso.getId());
    }

    @Override
    @Transactional
    public void update(NombreCurso nombreCurso) {
        nombreCursoDAO.updateColumns(nombreCurso, "nombre");
    }

}
