package pe.edu.lamolina.pivot.controller.curso;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;

@Service
@Transactional
public class CursoServiceImp implements CursoService {

    @Autowired
    CursoDAO cursoDAO;

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter, List<DepartamentoAcademico> departamentos) {
        return cursoDAO.allByDynatable(filter, departamentos);
    }

    @Override
    @Transactional
    public void save(Curso curso, Usuario usuario) {
        curso.setEstado(EstadoEnum.ACT.name());
        cursoDAO.save(curso);
    }

    @Override
    public Curso find(Long id) {
        return cursoDAO.find(id);
    }

    @Override
    @Transactional
    public void cambiarEstadoCurso(Curso curso) {
        Curso cursoBD = cursoDAO.find(curso.getId());
        if (cursoBD.getEstado().equals(EstadoEnum.ACT.name())) {
            cursoBD.setEstado(EstadoEnum.INA.name());
            cursoBD.setFechaAnulacion(new Date());
            cursoBD.setMotivoAnulacion(curso.getMotivoAnulacion());
        } else {
            cursoBD.setEstado(EstadoEnum.ACT.name());
        }
        cursoDAO.update(cursoBD);
    }

}
