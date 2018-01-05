package pe.edu.lamolina.pivot.controller.comun;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import pe.edu.lamolina.pivot.dao.general.UbicacionDAO;
import pe.edu.lamolina.pivot.dao.general.UniversidadDAO;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.PlanCalificacion;
import pe.edu.lamolina.pivot.model.general.Pais;
import pe.edu.lamolina.pivot.model.general.Ubicacion;
import pe.edu.lamolina.pivot.model.general.Universidad;

@Service
@Transactional(readOnly = true)
public class BuscarServiceImp implements BuscarService {

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    UbicacionDAO ubicacionDAO;

    @Autowired
    PlanCalificacionDAO planCalificacionDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    PaisDAO paisDAO;

    @Autowired
    UniversidadDAO universidadDAO;

    @Override
    public List<Curso> allCursosSCA(String nombre, Long idDepartamentoAca, Long planCalificacionId, Long idCiclo) {
        PlanCalificacion planCalificacion = planCalificacionDAO.find(planCalificacionId);
        return cursoDAO.allForSistemaCalificacion(nombre, idDepartamentoAca, planCalificacion, idCiclo);
    }

    @Override
    public List<Ubicacion> allDistritosByName(String nombre) {
        return ubicacionDAO.allDistritos(this.forLike(nombre));
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    public List<DepartamentoAcademico> allDepartamentosByName(String nombre) {
        return departamentoAcademicoDAO.allDepartamentos(this.forLike(nombre));
    }

    @Override
    public List<Docente> allCoordinadoresByIdDptoName(Long idDpto, String nombre) {
        return docenteDAO.allCoordinadoresByIdDptoName(idDpto, this.forLike(nombre));
    }

    @Override
    public List<Pais> allPaisesByName(String nombre) {
        return paisDAO.allPaisesByName(this.forLike(nombre));
    }

    @Override
    public List<Universidad> allUniversidadByName(String nombre) {
        return universidadDAO.allUniversidadByName(this.forLike(nombre));
    }

}
