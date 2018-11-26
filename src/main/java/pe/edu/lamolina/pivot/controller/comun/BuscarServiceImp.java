package pe.edu.lamolina.pivot.controller.comun;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
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
    SeccionDAO seccionDAO;

    @Autowired
    EmpresaDAO empresaDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    FacultadDAO facultadDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

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

    @Override
    public List<SituacionAcademica> allSituaciones() {
        return situacionAcademicaDAO.all();
    }

    @Override
    public List<ModalidadEstudio> allModalidadEstudios() {
        return modalidadEstudioDAO.allRegularesActivas();
    }

    @Override
    public List<Facultad> allFacultades() {
        return facultadDAO.allActivos();
    }

    @Override
    public List<Carrera> allCarrerasByName(String nombre) {
        nombre = forLike(nombre);
        return carreraDAO.allByNombre(nombre);
    }

    @Override
    public List<GrupoSeccion> allGrupoSeccionesByCiclo(CicloAcademico ciclo, String codigo, Long curso) {
        return grupoSeccionDAO.allByCicloCurso(ciclo, this.forLike(codigo), curso);
    }

    @Override
    public List<Curso> allCurso(String codigo, CicloAcademico ciclo) {
        codigo = forLike(codigo);
        return cursoDAO.allActiveByCodigo(codigo, ciclo);
    }

    @Override
    public List<Seccion> allSeccionByCodigo(String codigo, CicloAcademico ciclo) {
        codigo = forLike(codigo);
        return seccionDAO.allByCodigo(codigo);
    }

    @Override
    public List<CicloAcademico> allCicloByDescripcion(String nombre) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return cicloAcademicoDAO.allByModalidadEstudioName(modalidad, nombre);
    }

    @Override
    public List<PlanCalificacion> allPlanCalificacionByDescripcion(String nombre) {
        return planCalificacionDAO.allByNombre(nombre);
    }

    @Override
    public List<Curso> allCursoByModalidadEstudioNombre(String nombre, ModalidadEstudioEnum moda) {
        return cursoDAO.allByModalidadEstudioNombre(moda, nombre);
    }

    @Override
    public List<CicloAcademico> allCicloByDescripcionDescendent(String nombre) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return cicloAcademicoDAO.allCicloByNameDescendent(nombre, modalidad);
    }

    @Override
    public List<Universidad> allUniversidadByNamePais(String nombre, Long pais) {
        return universidadDAO.allUniversidadByNamePais(this.forLike(nombre), new Pais(pais));
    }

}
