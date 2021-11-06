package pe.edu.lamolina.amauta.controller.configuracion.cursodirigidofacultad;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.tramite.CursoDirigidoFacultad;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.tramite.CursoDirigidoFacultadDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class CursoDirigidoFacultadServiceImp implements CursoDirigidoFacultadService {

    private final CursoDAO cursoDAO;
    private final CursoDirigidoFacultadDAO cursoDirigidoFacultadDAO;

    @Override
    public List<CursoDirigidoFacultad> allByDynatable(DynatableFilter filter) {
        return cursoDirigidoFacultadDAO.allByDynatable(filter);
    }

    @Override
    public List<Curso> allCursoLikeParamByFacultad(String parametro, Facultad facultad) {
        List<CursoDirigidoFacultad> cursoDirigidosFAC = cursoDirigidoFacultadDAO.allByFacultad(facultad);
        List<Curso> cursos = cursoDirigidosFAC.stream().map(CursoDirigidoFacultad::getCurso).collect(Collectors.toList());

        parametro = "%" + parametro.replaceAll(" ", "%") + "%";
        return cursoDAO.searchLikeNombreNotIn(parametro, cursos);
    }

    @Override
    @Transactional
    public void save(CursoDirigidoFacultad cursoDirigidoFacultad, DataSessionPivot ds) {
        cursoDirigidoFacultad.setEstado(EstadoEnum.ACT.name());
        cursoDirigidoFacultad.setFechaRegistro(new Date());
        cursoDirigidoFacultad.setUserRegistro(ds.getUsuario());
        cursoDirigidoFacultadDAO.save(cursoDirigidoFacultad);
    }

    @Override
    @Transactional
    public void eliminar(CursoDirigidoFacultad cursoDirigidoFacultad) {
        cursoDirigidoFacultadDAO.delete(cursoDirigidoFacultad);
    }

}
