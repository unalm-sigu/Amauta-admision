package pe.edu.lamolina.pivot.controller.comun;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.dao.general.EmpresaDAO;
import pe.edu.lamolina.pivot.dao.general.PaisDAO;
import pe.edu.lamolina.pivot.dao.general.UbicacionDAO;
import pe.edu.lamolina.pivot.dao.general.UniversidadDAO;

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

    @Autowired
    EmpresaDAO empresaDAO;

    @Override
    public List<Curso> allCursosSCA(String nombre, PlanCalificacion plan, CicloAcademico ciclo) {
        PlanCalificacion planCalificacion = planCalificacionDAO.find(plan.getId());
        return cursoDAO.allForSistemaCalificacion(nombre, planCalificacion.getDepartamentoAcademico(), planCalificacion, ciclo);
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

    @Override
    public List<Empresa> allEmpresaByName(Pais pais, String nombre) {
        return empresaDAO.allEmpresaByName(pais, this.forLike(nombre));
    }

}
