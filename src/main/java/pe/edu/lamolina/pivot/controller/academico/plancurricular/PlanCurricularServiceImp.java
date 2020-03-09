package pe.edu.lamolina.pivot.controller.academico.plancurricular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoAdicionalCurricula;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.CursoEquivalenteElectivo;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.academico.RequisitoCursoOpcional;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.CurriculaEstadoEnum;
import pe.edu.lamolina.model.posgrado.CursoHabilEscuela;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EstadoEnum.CRE;
import static pe.edu.lamolina.model.enums.EstadoEnum.INA;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoCreditoEnum;
import pe.edu.lamolina.model.enums.TipoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.CULT;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.DEP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.EAD;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ECC;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ECP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.EEP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELC;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELE;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.GEN;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.OBL;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.PROD;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.TECIND;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularAsincronoService;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoAvanceCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoAdicionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteElectivoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoOpcionalDAO;
import pe.edu.lamolina.pivot.dao.academico.ResumenPlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.posgrado.CursoHabilEscuelaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.CODIGO_CURSO_DEP;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PlanCurricularServiceImp implements PlanCurricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    OrientacionCarreraDAO orientacionCarreraDAO;

    @Autowired
    PlanCurricularDAO planCurricularDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    RequisitoCursoCurriculaDAO requisitoCursoCurriculaDAO;

    @Autowired
    CursoHabilEscuelaDAO cursoHabilEscuelaDAO;

    @Autowired
    CursoAdicionalCurriculaDAO cursoAdicionalCurriculaDAO;

    @Autowired
    CursoOpcionalCurriculaDAO cursoOpcionalCurriculaDAO;

    @Autowired
    ResumenPlanCurricularDAO resumenPlanCurricularDAO;

    @Autowired
    RequisitoCursoOpcionalDAO requisitoCursoOpcionalDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    CursoEquivalenteDAO cursoEquivalenteDAO;

    @Autowired
    AlumnoAvanceCurricularDAO alumnoAvanceCurricularDAO;

    @Autowired
    CursoEquivalenteElectivoDAO cursoEquivalenteElectivoDAO;

    @Autowired
    UsuarioRolDAO usuarioRolDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    AvanceCurricularService avanceCurricularService;

    @Autowired
    AvanceCurricularAsincronoService avanceCurricularAsincronoService;

    @Autowired
    VisorAsignaCurricula visorAsignaCurricula;

    @Autowired
    VerificadorService verificadorService;

    @Override
    public void caducar(Long idCursoCurricula, DataSessionPivot ds) {

        CursoCurricula cursoCurricula = cursoCurriculaDAO.find(idCursoCurricula);

        List<CursoCurricula> cursoCurriculasAllPlanes = cursoCurriculaDAO.allByCurso(cursoCurricula.getCurso());

        for (CursoCurricula cursoCurriculasPlan : cursoCurriculasAllPlanes) {

            cursoCurriculasPlan.setEstado(CurriculaEstadoEnum.CAD.name());
            cursoCurriculasPlan.setFechaCaduca(new Date());
            cursoCurriculasPlan.setUserCaduca(ds.getUsuario());
            cursoCurriculaDAO.updateColumns(cursoCurriculasPlan, "estado", "fechaCaduca", "userCaduca");
        }

    }

    private enum NivelEnum {
        OBLIGATORIO, OPCIONAL, ADICIONAL
    };

    @Override
    public Carrera findCarrera(Carrera carrera) {
        return carreraDAO.find(carrera.getId());
    }

    @Override
    public List<Carrera> allCarreras(List<Carrera> carreras) {
        return carreraDAO.allRegularesByCarreras(carreras);
    }

    @Override
    public List<Carrera> allCarreras(DataSessionPivot ds, HttpServletRequest request) {
        return verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.ESP, request, ds);
    }

    @Override
    public List<Curso> allCursoByNombre(Curso curso) {

        List<Curso> cursos = cursoDAO.allByNombreTipoCurricula(curso.getNombre(), Arrays.asList(TipoCurriculaEnum.REG.name()), 10);
//        List<CursoCurricula> cursoCurriculas = cursoCurriculaDAO.allByCurso(curso.getNombre());
//        for (CursoCurricula cursoCurricula : cursoCurriculas) {
//            Curso cur = cursoCurricula.getCurso();
//            cur.setPl
//        }
        return cursos;
    }

    @Override
    public List<OrientacionCarrera> allOrientacionByCarreraEstado(Carrera carrera, EstadoEnum estadoEnum) {
        return orientacionCarreraDAO.allByCarreraEstado(carrera, estadoEnum);
    }

    @Override
    public Long countAlumnosByPlanCurricularCicloAcademico(PlanCurricular planCurricular, CicloAcademico cicloAcademico) {
        return alumnoCicloDAO.countByCicloAcademicoPlanCurricular(cicloAcademico, planCurricular);
    }

    @Override
    public Long countAlumnosByPlanCurricular(PlanCurricular planCurricular) {

        return alumnoDAO.countByPlanCurricular(planCurricular);
    }

    @Override
    public void deleteCursoEquivalenteByGrupoCursoCurricula(Integer grupo, CursoCurricula curso) {
        cursoEquivalenteDAO.deleteByGrupoCursoCurricula(grupo, curso);
    }

    @Override
    @Transactional
    public void deleteCursoEquivalenteElectivoByGrupoCursoCurricula(Integer grupo, CursoOpcionalCurricula cursoOpcionalCurricula) {
        cursoEquivalenteElectivoDAO.deleteByGrupoCursoOpcionalCurricula(grupo, cursoOpcionalCurricula);
    }

    @Override
    public CursoOpcionalCurricula findCursoOpcionalCurricula(Long cursoOpcionalCurriculaId) {
        CursoOpcionalCurricula cursoOpcionalCurricula = cursoOpcionalCurriculaDAO.findById(cursoOpcionalCurriculaId);

        List<CursoEquivalenteElectivo> equivalenteElectivos = cursoEquivalenteElectivoDAO.allActivoByCursoOpcional(cursoOpcionalCurricula);
        cursoOpcionalCurricula.setCursoEquivalenteElectivo(equivalenteElectivos);
        return cursoOpcionalCurricula;
    }

    @Override
    @Transactional
    public void saveGrupoEquivalente(GrupoCursoEquivalente grupo, DataSessionPivot ds) {
        if (grupo.getCursoEquivalente() == null) {
            return;
        }

        CursoCurricula cursoCurricula = cursoCurriculaDAO.find(grupo.getCursoCurricula().getId());
        List<CursoCurricula> cursoCurriculas = cursoCurriculaDAO.allByPlanCurricularCAD(cursoCurricula.getPlanCurricular());
        Map<Long, CursoCurricula> map = TypesUtil.convertListToMap("curso.id", cursoCurriculas);

        Integer maxNumeroGrupo = cursoEquivalenteDAO.findMaxGrupoByCursoCurricula(grupo.getCursoCurricula()) + 1;
        for (CursoEquivalente curso : grupo.getCursoEquivalente()) {

            CursoCurricula curriculaCaduca = map.get(curso.getCursoEquivalente().getId());
            curso.setCursoEquivalente(cursoDAO.find(curso.getCursoEquivalente().getId()));
            curso.setCursoCurricula(grupo.getCursoCurricula());
            curso.setGrupo(maxNumeroGrupo);
            curso.setEstado(EstadoEnum.ACT.name());
            curso.setFechaRegistro(new Date());
            curso.setUserRegistro(ds.getUsuario());
            curso.setCursoCaduco(curriculaCaduca);
            cursoEquivalenteDAO.save(curso);

            if (curriculaCaduca != null) {

                List<RequisitoCursoCurricula> requisitoCursoCurriculas = requisitoCursoCurriculaDAO.allByCursoCurricula(curriculaCaduca);

                for (RequisitoCursoCurricula requisitoCursoCurricula : requisitoCursoCurriculas) {
                    requisitoCursoCurricula.setEstado(EstadoEnum.INA.name());
                    requisitoCursoCurricula.setFechaModificacion(new Date());
                    requisitoCursoCurricula.setUserModificacion(ds.getUsuario());
                    requisitoCursoCurriculaDAO.update(requisitoCursoCurricula);

                    RequisitoCursoCurricula requisitoCursoCurriculaNew = new RequisitoCursoCurricula();
                    requisitoCursoCurriculaNew.setCursoCurricula(grupo.getCursoCurricula());
                    requisitoCursoCurriculaNew.setCursoRequisito(requisitoCursoCurricula.getCursoRequisito());
                    requisitoCursoCurriculaNew.setEstado(EstadoEnum.ACT.name());
                    requisitoCursoCurriculaNew.setFechaRegistro(new Date());
                    requisitoCursoCurriculaNew.setUserRegistro(ds.getUsuario());
                    requisitoCursoCurriculaNew.setSimultaneo(requisitoCursoCurricula.getSimultaneo());
                    requisitoCursoCurriculaDAO.save(requisitoCursoCurriculaNew);
                }

                List<RequisitoCursoOpcional> requisitoCursoOpcionalsDe = requisitoCursoOpcionalDAO.allRequisitoOpcionalDe(curriculaCaduca);
                for (RequisitoCursoOpcional requisitoCursoOpcional : requisitoCursoOpcionalsDe) {
                    requisitoCursoOpcional.setEstado(EstadoEnum.INA.name());
                    requisitoCursoOpcional.setFechaModificacion(new Date());
                    requisitoCursoOpcional.setUserModificacion(ds.getUsuario());
                    requisitoCursoOpcionalDAO.update(requisitoCursoOpcional);

                    RequisitoCursoOpcional requisitoCursoOpcionalNew = new RequisitoCursoOpcional();
                    requisitoCursoOpcionalNew.setCursoOpcional(requisitoCursoOpcional.getCursoOpcional());
                    requisitoCursoOpcionalNew.setCursoRequisitoCurricula(grupo.getCursoCurricula());
                    requisitoCursoOpcionalNew.setCursoRequisitoOpcional(requisitoCursoOpcional.getCursoRequisitoOpcional());
                    requisitoCursoOpcionalNew.setEstado(EstadoEnum.ACT.name());
                    requisitoCursoOpcionalNew.setFechaRegistro(new Date());
                    requisitoCursoOpcionalNew.setSimultaneo(requisitoCursoOpcional.getSimultaneo());
                    requisitoCursoOpcionalNew.setUserRegistro(ds.getUsuario());
                    requisitoCursoOpcionalDAO.save(requisitoCursoOpcionalNew);
                }

                List<RequisitoCursoCurricula> requisitoCursoCurriculasDe = requisitoCursoCurriculaDAO.allByRequisitoCurriculaDe(curriculaCaduca);

                for (RequisitoCursoCurricula requisitoCursoCurricula : requisitoCursoCurriculasDe) {
                    requisitoCursoCurricula.setEstado(EstadoEnum.INA.name());
                    requisitoCursoCurricula.setFechaModificacion(new Date());
                    requisitoCursoCurricula.setUserModificacion(ds.getUsuario());
                    requisitoCursoCurriculaDAO.update(requisitoCursoCurricula);

                    RequisitoCursoCurricula requisitoCursoCurriculaDeNew = new RequisitoCursoCurricula();
                    requisitoCursoCurriculaDeNew.setCursoCurricula(requisitoCursoCurricula.getCursoCurricula());
                    requisitoCursoCurriculaDeNew.setCursoRequisito(grupo.getCursoCurricula());
                    requisitoCursoCurriculaDeNew.setEstado(EstadoEnum.ACT.name());
                    requisitoCursoCurriculaDeNew.setFechaRegistro(new Date());
                    requisitoCursoCurriculaDeNew.setUserRegistro(ds.getUsuario());
                    requisitoCursoCurriculaDeNew.setSimultaneo(requisitoCursoCurricula.getSimultaneo());
                    requisitoCursoCurriculaDAO.save(requisitoCursoCurriculaDeNew);
                }
            }

        }

    }

    @Override
    public void saveGrupoEquivalenteElectivo(GrupoCursoEquivalenteElectivo grupo, DataSessionPivot ds) {
        if (grupo.getCursoEquivalenteElectivo() == null) {

            return;
        }

        Integer maxNumeroGrupo = cursoEquivalenteElectivoDAO.findMaxGrupoByCursoOpcionalCurricula(grupo.getCursoOpcionalCurricula()) + 1;
        for (CursoEquivalenteElectivo curso : grupo.getCursoEquivalenteElectivo()) {
            curso.setCursoEquivalente(cursoDAO.find(curso.getCursoEquivalente().getId()));
            curso.setCursoOpcionalCurricula(grupo.getCursoOpcionalCurricula());
            curso.setGrupo(maxNumeroGrupo);
            curso.setEstado(EstadoEnum.ACT.name());
            curso.setFechaRegistro(new Date());
            curso.setUserRegistro(ds.getUsuario());
            cursoEquivalenteElectivoDAO.save(curso);
        }
    }

    @Override
    @Transactional
    public PlanCurricular savePlanCurricular(PlanCurricular planForm) {
        ObjectUtil.eliminarAttrSinId(planForm);

        Carrera carreraForm = planForm.getCarrera();
        Carrera carreraBD = carreraDAO.find(carreraForm.getId());
        CicloAcademico cicloInicioForm = planForm.getCicloInicioVigencia();
        CicloAcademico cicloInicioBD = cicloAcademicoDAO.find(cicloInicioForm.getId());
        ModalidadEstudio modalidadCarrera = carreraBD.getModalidadEstudio();
        ModalidadEstudio modalidadCiclo = cicloInicioBD.getModalidadEstudio();

        boolean esMismaModalidad = modalidadCarrera.getId() == modalidadCiclo.getId().longValue();
        Assert.isTrue(esMismaModalidad, "La modalidad de estudio de la especialidad debe ser la misma del ciclo de inicio de vigencia");

        planForm.setEstadoEnum(EstadoEnum.CRE);
        planCurricularDAO.save(planForm);

        if (modalidadCarrera.getCodigoEnum() == ModalidadEstudioEnum.PRE) {
            List<String> codigosTipoCurr = Arrays.asList(ELE.name(), EEP.name());
            List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.allByCodigos(codigosTipoCurr);

            for (TipoCursoCurricula tipoCursoCurricula : tipoCursoCurriculas) {
                ResumenPlanCurricular resumenPlan = new ResumenPlanCurricular();
                resumenPlan.setCreditos(0);
                resumenPlan.setCursos(0);
                resumenPlan.setPlanCurricular(planForm);
                resumenPlan.setTipoCursoCurricula(tipoCursoCurricula);
                resumenPlan.setMinimoCreditos(0);
                resumenPlanCurricularDAO.save(resumenPlan);
            }
        }

        return planForm;
    }

    @Override
    @Transactional
    public void updatePlanCurricular(PlanCurricular planForm) {
        ObjectUtil.eliminarAttrSinId(planForm);
        PlanCurricular planBD = planCurricularDAO.find(planForm.getId());
        planBD.setOrientacionCarrera(planForm.getOrientacionCarrera());
        planBD.setCicloInicioVigencia(planForm.getCicloInicioVigencia());
        planBD.setFechaAprobado(planForm.getFechaAprobado());
        planCurricularDAO.updatePlanCurricular(planBD);
    }

    @Override
    @Transactional
    public void saveCursoCurricula(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        Integer nroCiclo = cursoCurricula.getNumeroCiclo();
        Curso curso = cursoDAO.find(cursoCurricula.getCurso().getId());
        TipoCursoCurricula tipoCurricula = tipoCursoCurriculaDAO.find(cursoCurricula.getTipoCursoCurricula().getId());
        verificarExistenciaCurso(curso, cursoCurricula.getPlanCurricular(), tipoCurricula, cursoCurricula.getCreditos(), NivelEnum.OBLIGATORIO, nroCiclo);

        Integer nroCurso = 1;
        PlanCurricular plan = cursoCurricula.getPlanCurricular();
        List<CursoCurricula> cursosCurr = cursoCurriculaDAO.allByPlanCurricularNroCiclo(plan, nroCiclo);
        for (CursoCurricula cursoCurr : cursosCurr) {
            if (cursoCurr.getNumeroCurso() != null) {
                nroCurso = cursoCurr.getNumeroCurso() > nroCurso ? cursoCurr.getNumeroCurso() + 1 : nroCurso;
            }
        }

        List<RequisitoCursoCurricula> requisitos = cursoCurricula.getCursosCurricula();
        requisitos = (requisitos == null) ? new ArrayList() : requisitos;

        for (RequisitoCursoCurricula requisito : requisitos) {
            requisito.setSimultaneo(requisito.getSimultaneo() == null ? 0 : 1);
            requisito.setCursoCurricula(cursoCurricula);
            requisito.setUserRegistro(ds.getUsuario());
            requisito.setFechaRegistro(new Date());
        }

        cursoCurricula.setNumeroCurso(nroCurso);
        cursoCurricula.setUserRegistro(ds.getUsuario());
        cursoCurricula.setFechaRegistro(new Date());
        cursoCurricula.setRequisitosOr(cursoCurricula.getRequisitosOr() == null ? false : cursoCurricula.getRequisitosOr());
        cursoCurricula.setEstado(CurriculaEstadoEnum.ACT.name());
        cursoCurriculaDAO.save(cursoCurricula);
        for (RequisitoCursoCurricula requisito : requisitos) {
            requisitoCursoCurriculaDAO.save(requisito);
        }
        //Curso curso = cursoDAO.find(cursoCurricula.getCurso().getId());
        if (curso.getCodigo().equals(CODIGO_CURSO_DEP)) {
            TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(DEP);
            cursoCurricula.setTipoCursoCurricula(tipoCursoCurricula);
        }

        ResumenPlanCurricular resumen = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                cursoCurricula.getTipoCursoCurricula(),
                cursoCurricula.getPlanCurricular());

        if (resumen == null) {
            resumen = new ResumenPlanCurricular();
            resumen.setPlanCurricular(cursoCurricula.getPlanCurricular());
            resumen.setTipoCursoCurricula(cursoCurricula.getTipoCursoCurricula());
            resumen.setCreditos(cursoCurricula.getCreditos());
            resumen.setMinimoCreditos(cursoCurricula.getCreditos());
            resumen.setCursos(1);
            resumenPlanCurricularDAO.save(resumen);

        } else {
            resumen.setCreditos(resumen.getCreditos() + cursoCurricula.getCreditos());
            resumen.setMinimoCreditos(resumen.getMinimoCreditos() + cursoCurricula.getCreditos());
            resumen.setCursos(resumen.getCursos() + 1);
            resumenPlanCurricularDAO.update(resumen);
        }

    }

    @Override
    @Transactional
    public void updateCursoCurricula(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        CursoCurricula cursoCurriculaBD = cursoCurriculaDAO.find(cursoCurricula.getId());
        int diff = cursoCurricula.getCreditos() - cursoCurriculaBD.getCreditos();

        cursoCurriculaBD.setRequisitosOr(cursoCurricula.getRequisitosOr());
        cursoCurriculaBD.setCreditosRequisito(cursoCurricula.getCreditosRequisito());
        cursoCurriculaDAO.update(cursoCurriculaBD);

        List<RequisitoCursoCurricula> requisitosDB = requisitoCursoCurriculaDAO.allByCursoCurricula(cursoCurricula);
        List<RequisitoCursoCurricula> requisitosForm = cursoCurricula.getCursosCurricula();
        ListsInspector inspector = TypesUtil.analizeLists(requisitosDB, requisitosForm, "cursoRequisito.id");
        Map<Long, RequisitoCursoCurricula> mapRequisitos = TypesUtil.convertListToMap("cursoRequisito.id", inspector.getListForm());

        List<RequisitoCursoCurricula> nuevos = inspector.getNewList();
        List<RequisitoCursoCurricula> eliminables = inspector.getDeadList();
        List<RequisitoCursoCurricula> existentes = inspector.getOldListDB();

        for (RequisitoCursoCurricula nuevo : nuevos) {
            nuevo.setSimultaneo(nuevo.getSimultaneo() == null ? 0 : 1);
            nuevo.setCursoCurricula(cursoCurricula);
            nuevo.setFechaRegistro(new Date());
            nuevo.setUserRegistro(ds.getUsuario());
            requisitoCursoCurriculaDAO.save(nuevo);
        }

        for (RequisitoCursoCurricula eliminable : eliminables) {
            requisitoCursoCurriculaDAO.delete(eliminable);
        }

        for (RequisitoCursoCurricula existenteBD : existentes) {
            RequisitoCursoCurricula existenteForm = mapRequisitos.get(existenteBD.getCursoRequisito().getId());
            existenteBD.setSimultaneo(existenteForm.getSimultaneo() == null ? 0 : 1);
            requisitoCursoCurriculaDAO.update(existenteBD);
        }

        if (diff != 0) {
            Curso curso = cursoDAO.find(cursoCurricula.getCurso().getId());
            if (curso.getCodigo().equals(CODIGO_CURSO_DEP)) {
                TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(DEP);
                cursoCurricula.setTipoCursoCurricula(tipoCursoCurricula);
            }
            ResumenPlanCurricular resumen = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                    cursoCurricula.getTipoCursoCurricula(),
                    cursoCurricula.getPlanCurricular());

            resumen.setCreditos(resumen.getCreditos() + diff);
            resumenPlanCurricularDAO.update(resumen);
        }
    }

    @Override
    @Transactional
    public void deleteCursoCurricula(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        CursoCurricula cursoCurriculaBD = cursoCurriculaDAO.find(cursoCurricula.getId());
        if (cursoCurriculaBD.getCurso().getCodigo().equals(CODIGO_CURSO_DEP)) {
            TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(DEP);
            cursoCurriculaBD.setTipoCursoCurricula(tipoCursoCurricula);
        }
        ResumenPlanCurricular resumen = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                cursoCurriculaBD.getTipoCursoCurricula(),
                cursoCurriculaBD.getPlanCurricular());

        resumen.setCreditos(resumen.getCreditos() - cursoCurriculaBD.getCreditos());
        resumen.setMinimoCreditos(resumen.getCreditos() - cursoCurriculaBD.getCreditos());
        resumen.setCursos(resumen.getCursos() - 1);
        if (resumen.getCursos() == 0) {
            resumenPlanCurricularDAO.delete(resumen);
        } else {
            resumenPlanCurricularDAO.update(resumen);
        }

        cursoCurriculaDAO.delete(cursoCurriculaBD);

    }

    @Override
    @Transactional
    public void trasladarCiclo(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        CursoCurricula cursoCurriculaBD = cursoCurriculaDAO.find(cursoCurricula.getId());
        PlanCurricular plan = cursoCurriculaBD.getPlanCurricular();

        Assert.isTrue(plan.getId().longValue() == cursoCurricula.getPlanCurricular().getId(), "El Plan de Estudios no es el correcto");
        Assert.isFalse(cursoCurricula.getNumeroCiclo() < 1, "El numero de ciclo no es el correcto");
        Assert.isFalse(cursoCurricula.getNumeroCiclo() > plan.getCiclos(), "El numero de ciclo no corresponde al plan");

        Integer nroCurso = 1;
        Integer nroCiclo = cursoCurricula.getNumeroCiclo();
        List<CursoCurricula> cursosCurr = cursoCurriculaDAO.allByPlanCurricularNroCiclo(plan, nroCiclo);
        for (CursoCurricula cursoCurr : cursosCurr) {
            if (cursoCurr.getNumeroCurso() != null) {
                nroCurso = cursoCurr.getNumeroCurso() > nroCurso ? cursoCurr.getNumeroCurso() + 1 : nroCurso;
            }
        }

        cursoCurriculaBD.setNumeroCiclo(cursoCurricula.getNumeroCiclo());
        cursoCurriculaBD.setNumeroCurso(nroCurso);
        cursoCurriculaDAO.update(cursoCurriculaBD);
    }

    @Override
    @Transactional
    public void trasladarToElectivos(CursoCurricula cursoCurricula, DataSessionPivot ds) {
        CursoCurricula cursoCurriculaBD = cursoCurriculaDAO.find(cursoCurricula.getId());
        List<RequisitoCursoCurricula> preRequisitos = requisitoCursoCurriculaDAO.allByCursoCurricula(cursoCurricula);
        List<RequisitoCursoCurricula> postRequisitos = requisitoCursoCurriculaDAO.allByRequisitoCurriculaDe(cursoCurricula);

        Assert.isTrue(postRequisitos.isEmpty(), "Este curso es pre-requisito de otros cursos. No puede ser trasladado");

        PlanCurricular plan = cursoCurriculaBD.getPlanCurricular();

        CursoOpcionalCurricula cursoOpcional = new CursoOpcionalCurricula();
        cursoOpcional.setCreditos(cursoCurriculaBD.getCreditos());
        cursoOpcional.setCreditosRequisito(cursoCurriculaBD.getCreditosRequisito());
        cursoOpcional.setCurso(cursoCurriculaBD.getCurso());
        cursoOpcional.setPlanCurricular(cursoCurriculaBD.getPlanCurricular());
        cursoOpcional.setTipoCursoCurricula(cursoCurricula.getTipoCursoCurricula());
        cursoOpcional.setFechaRegistro(new Date());
        cursoOpcional.setUserRegistro(ds.getUsuario());

        List<RequisitoCursoOpcional> newRequisitos = new ArrayList();
        for (RequisitoCursoCurricula requisito : preRequisitos) {
            RequisitoCursoOpcional newRequisito = new RequisitoCursoOpcional();
            newRequisito.setCursoRequisitoCurricula(requisito.getCursoRequisito());
            newRequisito.setCursoOpcional(cursoOpcional);
            newRequisito.setSimultaneo(requisito.getSimultaneo());
            newRequisito.setFechaRegistro(new Date());
            newRequisito.setUserRegistro(ds.getUsuario());
            newRequisitos.add(newRequisito);
        }
        if (cursoCurriculaBD.getCurso().getCodigo().equals(CODIGO_CURSO_DEP)) {
            TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(DEP);
            cursoCurriculaBD.setTipoCursoCurricula(tipoCursoCurricula);
        }
        ResumenPlanCurricular resumenA = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                cursoCurriculaBD.getTipoCursoCurricula(),
                cursoCurriculaBD.getPlanCurricular());

        resumenA.setCreditos(resumenA.getCreditos() - cursoCurriculaBD.getCreditos());
        resumenA.setCursos(resumenA.getCursos() - 1);
        resumenPlanCurricularDAO.update(resumenA);

        for (RequisitoCursoCurricula requisito : preRequisitos) {
            requisitoCursoCurriculaDAO.delete(requisito);
        }
        cursoCurriculaDAO.delete(cursoCurriculaBD);

        cursoOpcionalCurriculaDAO.save(cursoOpcional);
        for (RequisitoCursoOpcional requisito : newRequisitos) {
            requisitoCursoOpcionalDAO.save(requisito);
        }
    }

    @Override
    @Transactional
    public void trasladarToObligatorios(CursoCurricula cursoCurriculaForm, DataSessionPivot ds) {
        CursoOpcionalCurricula cursoOpcionalBD = cursoOpcionalCurriculaDAO.find(cursoCurriculaForm.getId());
        List<RequisitoCursoOpcional> preRequisitos = requisitoCursoOpcionalDAO.allByCursoElectivo(cursoOpcionalBD);
        List<RequisitoCursoOpcional> postRequisitos = requisitoCursoOpcionalDAO.allPostRequisitosByCursoElectivo(cursoOpcionalBD);

        Assert.isTrue(postRequisitos.isEmpty(), "Este curso es pre-requisito de otros cursos. No puede ser trasladado");

        PlanCurricular plan = cursoOpcionalBD.getPlanCurricular();

        Integer nroCurso = 1;
        Integer nroCiclo = cursoCurriculaForm.getNumeroCiclo();
        List<CursoCurricula> cursosCurr = cursoCurriculaDAO.allByPlanCurricularNroCiclo(plan, nroCiclo);
        for (CursoCurricula cursoCurr : cursosCurr) {
            if (cursoCurr.getNumeroCurso() != null) {
                nroCurso = cursoCurr.getNumeroCurso() > nroCurso ? cursoCurr.getNumeroCurso() + 1 : nroCurso;
            }
        }

        CursoCurricula cursoCurricula = new CursoCurricula();
        cursoCurricula.setCreditos(cursoOpcionalBD.getCreditos());
        cursoCurricula.setCreditosRequisito(cursoOpcionalBD.getCreditosRequisito());
        cursoCurricula.setCurso(cursoOpcionalBD.getCurso());
        cursoCurricula.setPlanCurricular(cursoOpcionalBD.getPlanCurricular());
        cursoCurricula.setRequisitosOr(cursoOpcionalBD.getRequisitosOr());
        cursoCurricula.setTipoCursoCurricula(cursoCurriculaForm.getTipoCursoCurricula());
        cursoCurricula.setNumeroCiclo(cursoCurriculaForm.getNumeroCiclo());
        cursoCurricula.setNumeroCurso(nroCurso);
        cursoCurricula.setFechaRegistro(new Date());
        cursoCurricula.setUserRegistro(ds.getUsuario());

        List<RequisitoCursoCurricula> newRequisitos = new ArrayList();
        for (RequisitoCursoOpcional requisito : preRequisitos) {
            Assert.isNull(requisito.getCursoRequisitoOpcional(), "No puede trasladarse este curso porque su requisito es un curso electivo");
            Assert.isTrue(requisito.getCursoRequisitoCurricula().getNumeroCiclo() < cursoCurriculaForm.getNumeroCiclo(),
                    "No puede trasladarse este curso porque su requisito pertence a un ciclo mayor al que desea trasladar");

            RequisitoCursoCurricula newRequisito = new RequisitoCursoCurricula();
            newRequisito.setCursoRequisito(requisito.getCursoRequisitoCurricula());
            newRequisito.setCursoCurricula(cursoCurricula);
            newRequisito.setSimultaneo(requisito.getSimultaneo());
            newRequisito.setFechaRegistro(new Date());
            newRequisito.setUserRegistro(ds.getUsuario());
            newRequisitos.add(newRequisito);
        }

        for (RequisitoCursoOpcional requisito : preRequisitos) {
            requisitoCursoOpcionalDAO.delete(requisito);
        }
        cursoOpcionalCurriculaDAO.delete(cursoOpcionalBD);

        cursoCurriculaDAO.save(cursoCurricula);
        for (RequisitoCursoCurricula requisito : newRequisitos) {
            requisitoCursoCurriculaDAO.save(requisito);
        }
        if (cursoCurricula.getCurso().getCodigo().equals(CODIGO_CURSO_DEP)) {
            TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(DEP);
            cursoCurricula.setTipoCursoCurricula(tipoCursoCurricula);
        }
        ResumenPlanCurricular resumen = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(
                cursoCurricula.getTipoCursoCurricula(),
                cursoCurricula.getPlanCurricular());

        if (resumen == null) {
            resumen = new ResumenPlanCurricular();
            resumen.setPlanCurricular(plan);
            resumen.setTipoCursoCurricula(cursoCurriculaForm.getTipoCursoCurricula());
            resumen.setCreditos(cursoCurriculaForm.getCreditos());
            resumen.setMinimoCreditos(cursoCurriculaForm.getCreditos());
            resumen.setCursos(1);
            resumenPlanCurricularDAO.save(resumen);

        } else {
            resumen.setCreditos(resumen.getCreditos() + cursoCurricula.getCreditos());
            resumen.setMinimoCreditos(resumen.getMinimoCreditos() + cursoCurricula.getCreditos());
            resumen.setCursos(resumen.getCursos() + 1);
            resumenPlanCurricularDAO.update(resumen);
        }
    }

    @Override
    @Transactional
    public void saveCursoAdicional(CursoAdicionalCurricula cursoAdicional, DataSessionPivot ds) {
        Curso curso = cursoDAO.find(cursoAdicional.getCurso().getId());
        verificarExistenciaCurso(curso, cursoAdicional.getPlanCurricular(), null, 0, NivelEnum.ADICIONAL, 0);

        cursoAdicional.setUserRegistro(ds.getUsuario());
        cursoAdicional.setFechaRegistro(new Date());
        cursoAdicionalCurriculaDAO.save(cursoAdicional);
    }

    @Override
    @Transactional
    public void saveCursoOpcional(CursoOpcionalCurricula cursoOpcional, DataSessionPivot ds) {
        Curso curso = cursoDAO.find(cursoOpcional.getCurso().getId());
        TipoCursoCurricula tipoCurricula = tipoCursoCurriculaDAO.find(cursoOpcional.getTipoCursoCurricula().getId());
        verificarExistenciaCurso(curso, cursoOpcional.getPlanCurricular(), tipoCurricula, cursoOpcional.getCreditos(), NivelEnum.OPCIONAL, 0);

        List<RequisitoCursoOpcional> requisitos = cursoOpcional.getCursosOpcionales();
        requisitos = (requisitos == null) ? new ArrayList() : requisitos;

        for (RequisitoCursoOpcional requisito : requisitos) {
            ObjectUtil.eliminarAttrSinId(requisito, "cursoRequisitoCurricula");
            ObjectUtil.eliminarAttrSinId(requisito, "cursoRequisitoOpcional");

            requisito.setSimultaneo(requisito.getSimultaneo() == null ? 0 : 1);
            requisito.setCursoOpcional(cursoOpcional);
            requisito.setUserRegistro(ds.getUsuario());
            requisito.setFechaRegistro(new Date());
        }

        cursoOpcional.setUserRegistro(ds.getUsuario());
        cursoOpcional.setFechaRegistro(new Date());
        cursoOpcional.setRequisitosOr(cursoOpcional.getRequisitosOr() == null ? false : cursoOpcional.getRequisitosOr());
        cursoOpcionalCurriculaDAO.save(cursoOpcional);
        for (RequisitoCursoOpcional requisito : requisitos) {
            requisitoCursoOpcionalDAO.save(requisito);
        }

    }

    @Override
    @Transactional
    public void updateCursoOpcional(CursoOpcionalCurricula cursoOpcional, DataSessionPivot ds) {
        CursoOpcionalCurricula cursoOpcionalBD = cursoOpcionalCurriculaDAO.find(cursoOpcional.getId());
        cursoOpcionalBD.setCreditosRequisito(cursoOpcional.getCreditosRequisito());
        cursoOpcionalBD.setRequisitosOr(cursoOpcional.getRequisitosOr());
        cursoOpcionalCurriculaDAO.update(cursoOpcionalBD);

        List<RequisitoCursoOpcional> requisitosDB = requisitoCursoOpcionalDAO.allByCursoElectivo(cursoOpcional);
        List<RequisitoCursoOpcional> requisitosForm = cursoOpcional.getCursosOpcionales();
        ListsInspector inspector = TypesUtil.analizeLists(requisitosDB, requisitosForm, "cursoRequisito.id");
        Map<Long, RequisitoCursoOpcional> mapRequisitos = TypesUtil.convertListToMap("cursoRequisito.id", inspector.getListForm());

        List<RequisitoCursoOpcional> nuevos = inspector.getNewList();
        List<RequisitoCursoOpcional> eliminables = inspector.getDeadList();
        List<RequisitoCursoOpcional> existentes = inspector.getOldListDB();

        for (RequisitoCursoOpcional nuevo : nuevos) {
            ObjectUtil.eliminarAttrSinId(nuevo, "cursoRequisitoCurricula");
            ObjectUtil.eliminarAttrSinId(nuevo, "cursoRequisitoOpcional");

            nuevo.setSimultaneo(nuevo.getSimultaneo() == null ? 0 : 1);
            nuevo.setCursoOpcional(cursoOpcional);
            nuevo.setFechaRegistro(new Date());
            nuevo.setUserRegistro(ds.getUsuario());
            requisitoCursoOpcionalDAO.save(nuevo);
        }

        for (RequisitoCursoOpcional eliminable : eliminables) {
            requisitoCursoOpcionalDAO.delete(eliminable);
        }

        for (RequisitoCursoOpcional existenteBD : existentes) {
            ObjectUtil.eliminarAttrSinId(existenteBD, "cursoRequisitoCurricula");
            ObjectUtil.eliminarAttrSinId(existenteBD, "cursoRequisitoOpcional");

            RequisitoCursoOpcional existenteForm = mapRequisitos.get(existenteBD.getCursoRequisito().getId());
            existenteBD.setSimultaneo(existenteForm.getSimultaneo() == null ? 0 : 1);
            requisitoCursoOpcionalDAO.update(existenteBD);
        }
    }

    @Override
    public PlanCurricular findPlanCurricularById(PlanCurricular planCurricular) {
        PlanCurricular plan = planCurricularDAO.find(planCurricular.getId());
        List<CursoCurricula> cursosPlan = cursoCurriculaDAO.allByPlanCurricular(plan);
        Map<Long, CursoCurricula> mapCursosPlan = TypesUtil.convertListToMap("id", cursosPlan);
        plan.setCursoCurricula(cursosPlan);
        for (CursoCurricula cursoCurricula : cursosPlan) {
            cursoCurricula.setRequisitosCursoCurricula(new ArrayList());
        }

        List<RequisitoCursoCurricula> requisitos = requisitoCursoCurriculaDAO.allByCursosCurricula(cursosPlan);
        for (RequisitoCursoCurricula requisito : requisitos) {
            CursoCurricula cursoMain = mapCursosPlan.get(requisito.getCursoCurricula().getId());
            cursoMain.getRequisitosCursoCurricula().add(requisito);
        }

        return plan;
    }

    @Override
    public List<CicloAcademico> allUltimosCiclos(Integer cantidadCiclos) {
        return cicloAcademicoDAO.allUltimos(cantidadCiclos);
    }

    @Override
    public List<CicloAcademico> allUltimosCiclosByModalidad(ModalidadEstudio modalidad, Integer cantidadCiclos) {
        return cicloAcademicoDAO.allUltimosByModalidad(modalidad, cantidadCiclos);
    }

    @Override
    public List<PlanCurricular> allByDynatable(DynatableFilter filter, List<Carrera> carreras) {
        List<PlanCurricular> planesCurriculares = planCurricularDAO.allByDynatable(filter, carreras);
        Map<Long, Integer> cursosCurriculaCounts = cursoCurriculaDAO.countByPlanesCurricular(planesCurriculares);
        Map<Long, Integer> cursosAdiCurriculaCounts = cursoAdicionalCurriculaDAO.countByPlanesCurricular(planesCurriculares);
        Map<Long, Integer> cursosOpcCurriculaCounts = cursoOpcionalCurriculaDAO.countByPlanesCurricular(planesCurriculares);

        for (PlanCurricular planCurricular : planesCurriculares) {
            Integer curObl = cursosCurriculaCounts.get(planCurricular.getId());
            Integer curOpc = cursosOpcCurriculaCounts.get(planCurricular.getId());
            Integer curAdc = cursosAdiCurriculaCounts.get(planCurricular.getId());
            planCurricular.setCantidadCursosCurricula(curObl == null ? 0 : curObl);
            planCurricular.setCantidadCursosOpcionales(curOpc == null ? 0 : curOpc);
            planCurricular.setCantidadCursosAdicionales(curAdc == null ? 0 : curAdc);
        }
        return planesCurriculares;
    }

    @Override
    public List<CursoCurricula> allCursosOblByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList();
        }

        List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByDynatable(filter);
        List<RequisitoCursoCurricula> preRequisitos = requisitoCursoCurriculaDAO.allByCursosCurricula(cursosCurricula);
        List<RequisitoCursoCurricula> postRequisitos = requisitoCursoCurriculaDAO.allPostRequisitosByCursosCurricula(cursosCurricula);
        List<RequisitoCursoOpcional> postRequisitosOpc = requisitoCursoOpcionalDAO.allPostRequisitosByCursosCurricula(cursosCurricula);
        List<CursoEquivalente> equivalentesCurricula = cursoEquivalenteDAO.allActivoByCursosCurriculas(cursosCurricula);

        Map<Long, List<RequisitoCursoCurricula>> mapPreRequisitos = TypesUtil.convertListToMapList("cursoCurricula.id", preRequisitos);
        Map<Long, List<RequisitoCursoCurricula>> mapPostRequisitos = TypesUtil.convertListToMapList("cursoRequisito.id", postRequisitos);
        Map<Long, List<RequisitoCursoOpcional>> mapPostRequisitosOpc = TypesUtil.convertListToMapList("cursoRequisitoCurricula.id", postRequisitosOpc);
        Map<Long, List<CursoEquivalente>> mapEquivalentes = TypesUtil.convertListToMapList("cursoCurricula.id", equivalentesCurricula);

        for (CursoCurricula curso : cursosCurricula) {
            List<RequisitoCursoCurricula> preRequisitosCurso = fillList(mapPreRequisitos.get(curso.getId()));
            List<RequisitoCursoCurricula> postRequisitosCurso = fillList(mapPostRequisitos.get(curso.getId()));
            List<RequisitoCursoOpcional> postRequisitosCursoOpc = fillList(mapPostRequisitosOpc.get(curso.getId()));
            List<CursoEquivalente> equivalentesCurso = fillList(mapEquivalentes.get(curso.getId()));

            curso.setCursosCurricula(preRequisitosCurso);
            curso.setRequisitosCursoCurricula(postRequisitosCurso);
            curso.setRequisitosCursoOpcional(postRequisitosCursoOpc);
            curso.setCursosEquivalentes(equivalentesCurso);
        }

        return cursosCurricula;
    }

    @Override
    public List<ResumenPlanCurricular> allResPlanCurByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList();
        }
        return resumenPlanCurricularDAO.allByDynatable(filter);
    }

    @Override
    public List<CursoAdicionalCurricula> allCursosAdcByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList();
        }
        return cursoAdicionalCurriculaDAO.allByDynatable(filter);
    }

    @Override
    public List<CursoOpcionalCurricula> allCursosElecByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            return new ArrayList();
        }

        List<CursoOpcionalCurricula> cursosElectivos = cursoOpcionalCurriculaDAO.allByDynatable(filter);
        List<RequisitoCursoOpcional> preRequisitos = requisitoCursoOpcionalDAO.allRequisitosByCursosElectivos(cursosElectivos);
        List<RequisitoCursoOpcional> postRequisitos = requisitoCursoOpcionalDAO.allPostRequisitosByCursosElectivo(cursosElectivos);
        List<CursoEquivalenteElectivo> equivalentesCurricula = cursoEquivalenteElectivoDAO.allActivoByCursosOpcionales(cursosElectivos);

        Map<Long, List<RequisitoCursoOpcional>> mapRequisitos = TypesUtil.convertListToMapList("cursoOpcional.id", preRequisitos);
        Map<Long, List<RequisitoCursoOpcional>> mapPostRequisitos = TypesUtil.convertListToMapList("cursoRequisitoOpcional.id", postRequisitos);
        Map<Long, List<CursoEquivalenteElectivo>> mapEquivalesCurricula = TypesUtil.convertListToMapList("cursoOpcionalCurricula.id", equivalentesCurricula);

        for (CursoOpcionalCurricula cursoElectivo : cursosElectivos) {
            List<RequisitoCursoOpcional> preRequisitosElec = fillList(mapRequisitos.get(cursoElectivo.getId()));
            List<RequisitoCursoOpcional> postRequisitosElec = fillList(mapPostRequisitos.get(cursoElectivo.getId()));
            List<CursoEquivalenteElectivo> equivalentesCurso = fillList(mapEquivalesCurricula.get(cursoElectivo.getId()));

            cursoElectivo.setCursosOpcionales(preRequisitosElec);
            cursoElectivo.setRequisitosCursoOpcionales(postRequisitosElec);
            cursoElectivo.setCursoEquivalenteElectivo(equivalentesCurso);
        }

        return cursosElectivos;
    }

    @Override
    public List<TipoCursoCurricula> allTiposCursoCurriculaByPlan(PlanCurricular plan) {
        Carrera carrera = plan.getCarrera();
        ModalidadEstudio modalidad = carrera.getModalidadEstudio();
        List<TipoCursoCurricula> tipos = tipoCursoCurriculaDAO.all();
        List<TipoCursoCurricula> tiposEnvio = new ArrayList();

        for (TipoCursoCurricula tipo : tipos) {
            if (modalidad.getCodigoEnum() == ModalidadEstudioEnum.EPG) {
                if (Arrays.asList(OBL, ELC, ELE, ECP, ECC, EAD).contains(tipo.getCodigoEnum())) {
                    tiposEnvio.add(tipo);
                }
            } else if (tipo.getCodigoEnum() == CULT) {
                if (carrera.getCodigo().equals("010")) { // Solo agronomia
                    tiposEnvio.add(tipo);
                }
            } else if (tipo.getCodigoEnum() == PROD) {
                if (carrera.getCodigo().equals("060")) { // Solo zootecnia
                    tiposEnvio.add(tipo);
                }
            } else if (tipo.getCodigoEnum() == TECIND) {
                if (carrera.getCodigo().equals("060")) { // Solo zootecnia
                    tiposEnvio.add(tipo);
                }
            } else if (!Arrays.asList(EEP, ELE, ECP, ECC, EAD).contains(tipo.getCodigoEnum())) {
                tiposEnvio.add(tipo);
            }
        }
        return tiposEnvio;
    }

    @Override
    public List<TipoCursoCurricula> allTiposCursoCurriculasElectivosByPlan(PlanCurricular plan) {
        Carrera carrera = plan.getCarrera();
        ModalidadEstudio modalidad = carrera.getModalidadEstudio();
        List<TipoCursoCurricula> tiposEnvio = new ArrayList();
        List<TipoCursoCurricula> tiposTodos = tipoCursoCurriculaDAO.all();

        for (TipoCursoCurricula tipo : tiposTodos) {
            if (modalidad.getCodigoEnum() == ModalidadEstudioEnum.EPG) {
                if (Arrays.asList(ECP, ELC, ECC, EAD).contains(tipo.getCodigoEnum())) {
                    tiposEnvio.add(tipo);
                }
            } else if (tipo.getCodigoEnum() == ELC) {
                tiposEnvio.add(tipo);
            } else if (tipo.getCodigoEnum() == CULT) {
                if (carrera.getCodigo().equals("010")) { // Solo agronomia
                    tiposEnvio.add(tipo);
                }
            } else if (tipo.getCodigoEnum() == PROD) {
                if (carrera.getCodigo().equals("060")) { // Solo zootecnia
                    tiposEnvio.add(tipo);
                }
            } else if (tipo.getCodigoEnum() == TECIND) {
                if (carrera.getCodigo().equals("060")) { // Solo zootecnia
                    tiposEnvio.add(tipo);
                }
            }
        }
        return tiposEnvio;
    }

    @Override
    public List<TipoCursoCurricula> allTiposCursoCurriculasObligatorios() {
        List<TipoCursoCurricula> total = tipoCursoCurriculaDAO.all();
        List<TipoCursoCurricula> lista = new ArrayList();
        for (TipoCursoCurricula tipo : total) {
            if (Arrays.asList(OBL, GEN).contains(tipo.getCodigoEnum())) {
                lista.add(tipo);
            }
        }
        return lista;
    }

    @Override
    public TipoCursoCurricula findTipoCurricula(Long tipoCursoCurricula) {
        return tipoCursoCurriculaDAO.find(tipoCursoCurricula);
    }

    @Override
    public List<CursoCurricula> allCursosCurriculaByFilter(TipoCursoCurricula tipoCursoCurricula) {
        return cursoCurriculaDAO.allByFilter(tipoCursoCurricula);
    }

    @Override
    public List<Curso> allCursoByNombreTipoCurricula(String nombre, List<TipoCurriculaEnum> tiposCurriculaEnum) {
        List<String> tiposCurricula = null;
        if (tiposCurriculaEnum != null && !tiposCurriculaEnum.isEmpty()) {
            tiposCurricula = new ArrayList();
            for (TipoCurriculaEnum tipoCurriculaEnum : tiposCurriculaEnum) {
                tiposCurricula.add(tipoCurriculaEnum.name());
            }
        }
        return cursoDAO.allByNombreTipoCurricula(nombre, tiposCurricula, 10);
    }

    @Override
    public List<CursoCurricula> allCursoCurriculaByNombre(CursoCurricula cursoCurricula) {
        return cursoCurriculaDAO.allByNombrePlanNroCiclo(cursoCurricula, 10);
    }

    @Override
    public List<RequisitoCursoOpcional> allCursosObligatoriosAndElectivosByNombre(CursoCurricula cursoCurriculaForm) {
        List<RequisitoCursoOpcional> cursos = new ArrayList();
        List<CursoCurricula> cursosObligatorios = cursoCurriculaDAO.allByNombrePlanNroCiclo(cursoCurriculaForm, 10);
        for (CursoCurricula obligatorio : cursosObligatorios) {
            RequisitoCursoOpcional curso = new RequisitoCursoOpcional();
            curso.setCursoRequisitoCurricula(obligatorio);
            cursos.add(curso);
        }
        List<CursoOpcionalCurricula> cursosElectivos = cursoOpcionalCurriculaDAO.allByNombrePlan(cursoCurriculaForm, 10);
        for (CursoOpcionalCurricula electivo : cursosElectivos) {
            RequisitoCursoOpcional curso = new RequisitoCursoOpcional();
            curso.setCursoRequisitoOpcional(electivo);
            cursos.add(curso);
        }

        Collections.sort(cursos, new RequisitoCursoOpcional.CompareNombre());
        return cursos;
    }

    @Override
    public CursoCurricula findCursoCurricula(Long cursoCurriculaId) {
        CursoCurricula cursoCurricula = cursoCurriculaDAO.find(cursoCurriculaId);
        List<RequisitoCursoCurricula> requisitos = requisitoCursoCurriculaDAO.allByCursoCurricula(cursoCurricula);
        List<CursoEquivalente> cursoEquivalentes = cursoEquivalenteDAO.allActivoByCursoCurricula(cursoCurricula);
        cursoCurricula.setCursosEquivalentes(cursoEquivalentes);
        cursoCurricula.setCursosCurricula(requisitos);
        return cursoCurricula;
    }

    @Override
    public CursoOpcionalCurricula findCursoElectivo(Long cursoElectivoId) {
        CursoOpcionalCurricula cursoElectivo = cursoOpcionalCurriculaDAO.find(cursoElectivoId);
        List<RequisitoCursoOpcional> requisitos = requisitoCursoOpcionalDAO.allByCursoElectivo(cursoElectivo);
        cursoElectivo.setCursosOpcionales(requisitos);
        return cursoElectivo;
    }

    @Override
    @Transactional
    public void deleteCursoAdicional(Long cursoAdicionalId) {
        cursoAdicionalCurriculaDAO.delete(new CursoAdicionalCurricula(cursoAdicionalId));
    }

    @Override
    @Transactional
    public void deleteCursoOpcional(CursoOpcionalCurricula cursoElectivo) {
        CursoOpcionalCurricula electivoBD = cursoOpcionalCurriculaDAO.find(cursoElectivo.getId());
        List<RequisitoCursoOpcional> requisitos = requisitoCursoOpcionalDAO.allByCursoElectivo(electivoBD);
        for (RequisitoCursoOpcional requisito : requisitos) {
            requisitoCursoOpcionalDAO.delete(requisito);
        }
        cursoOpcionalCurriculaDAO.delete(electivoBD);
    }

    @Override
    public List<Curso> allCursosByCodigo(String codigo) {
        return cursoDAO.allByCodigo(codigo);
    }

    private CursoCurricula findCursoCurriculaByCursoPlan(Curso curso, PlanCurricular planCurricular) {
        List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByPlanCurricular(planCurricular);
        Map<Long, CursoCurricula> mapCursosCurricula = TypesUtil.convertListToMap("curso.id", cursosCurricula);
        return mapCursosCurricula.get(curso.getId());
    }

    private CursoOpcionalCurricula findCursoOpcionalByCursoPlan(Curso curso, PlanCurricular planCurricular) {
        List<CursoOpcionalCurricula> cursosOpcionales = cursoOpcionalCurriculaDAO.allByPlanCurricular(planCurricular);
        Map<Long, CursoOpcionalCurricula> mapCursosOpcionales = TypesUtil.convertListToMap("curso.id", cursosOpcionales);
        return mapCursosOpcionales.get(curso.getId());
    }

    private void verificarExistenciaCurso(
            Curso curso,
            PlanCurricular planCurricular,
            TipoCursoCurricula tipoCurricula,
            int creditosNuevos,
            NivelEnum nivel,
            int nroCiclo) {

        if (nivel == NivelEnum.ADICIONAL) {
            List<CursoAdicionalCurricula> cursosAdicionales = cursoAdicionalCurriculaDAO.allByPlanCurricular(planCurricular);
            Map<Long, CursoAdicionalCurricula> mapCursosAdicionales = TypesUtil.convertListToMap("curso.id", cursosAdicionales);
            CursoAdicionalCurricula cursoAdicional = mapCursosAdicionales.get(curso.getId());
            Assert.isNull(cursoAdicional, "Este curso ya existe en el grupo de adicionales");
            return;
        }

        if (nivel == NivelEnum.OBLIGATORIO) {
            if (TipoCurriculaEnum.COMD != curso.getTipoCurriculaEnum()) {
                Assert.isNotNull(curso.getTipoCredito(), "Este curso no tiene definido el tipo de crédito");
            }

            List<CursoCurricula> cursosCurricula = cursoCurriculaDAO.allByCursoPlan(curso, planCurricular);
            if (Arrays.asList(OBL, GEN).contains(tipoCurricula.getCodigoEnum())) {
                if (!cursosCurricula.isEmpty()) {
                    Assert.isTrue(curso.getTipoCreditoEnum() == TipoCreditoEnum.VAR, "Solo cursos con créditos variable pueden ser ingresados en diferentes ciclos");
                    for (CursoCurricula cursoCurr : cursosCurricula) {
                        boolean esMismoTipoCurricula = tipoCurricula.getId() == cursoCurr.getTipoCursoCurricula().getId().longValue();
                        Assert.isTrue(esMismoTipoCurricula, "Este curso ya existe como " + cursoCurr.getTipoCursoCurricula().getNombre());
                    }
                }

                if (curso.getTipoCreditoEnum() == TipoCreditoEnum.VAR) {
                    int creditosPrevios = 0;
                    for (CursoCurricula cursoCurricula : cursosCurricula) {
                        creditosPrevios += cursoCurricula.getCreditos();
                    }
                    boolean dentroRangoCreditos = creditosPrevios + creditosNuevos <= curso.getCreditosVariables();
                    Assert.isTrue(dentroRangoCreditos, "No puede exceder un total de " + curso.getCreditosVariables() + " créditos");
                }

            }
            for (CursoCurricula cursoCurr : cursosCurricula) {
                Assert.isFalse(cursoCurr.getNumeroCiclo() == nroCiclo, "No se puede agregar el mismo curso en mismo ciclo");
            }
            return;
        }

        if (nivel == NivelEnum.OPCIONAL) {
            CursoOpcionalCurricula cursoOpcional = findCursoOpcionalByCursoPlan(curso, planCurricular);
            Assert.isNull(cursoOpcional, "Este curso ya existe en el grupo de electivos");
        }

    }

    @Override
    @Transactional
    public void deletePlanCurricular(PlanCurricular plan) {
        PlanCurricular planBD = planCurricularDAO.find(plan.getId());
        Assert.isTrue(planBD.getEstadoEnum() == EstadoEnum.CRE, "Solo puede eliminarse un plan con estado Creado");

        List<CursoCurricula> cursos = cursoCurriculaDAO.allByPlanCurricular(planBD);
        List<CursoAdicionalCurricula> adicionales = cursoAdicionalCurriculaDAO.allByPlanCurricular(planBD);
        List<CursoOpcionalCurricula> opcionales = cursoOpcionalCurriculaDAO.allByPlanCurricular(planBD);
        List<ResumenPlanCurricular> resumenes = resumenPlanCurricularDAO.allByPlan(planBD);

        List<RequisitoCursoCurricula> requisitos = requisitoCursoCurriculaDAO.allByCursosCurricula(cursos);
        List<RequisitoCursoOpcional> requisitosOpc = requisitoCursoOpcionalDAO.allRequisitosByCursosElectivos(opcionales);

        for (RequisitoCursoOpcional req : requisitosOpc) {
            requisitoCursoOpcionalDAO.delete(req);
        }
        for (RequisitoCursoCurricula req : requisitos) {
            requisitoCursoCurriculaDAO.delete(req);
        }
        for (CursoAdicionalCurricula adi : adicionales) {
            cursoAdicionalCurriculaDAO.delete(adi);
        }
        for (CursoOpcionalCurricula opcional : opcionales) {
            cursoOpcionalCurriculaDAO.delete(opcional);
        }
        for (CursoCurricula curso : cursos) {
            cursoCurriculaDAO.delete(curso);
        }
        for (ResumenPlanCurricular resumen : resumenes) {
            resumenPlanCurricularDAO.delete(resumen);
        }
        planCurricularDAO.delete(planBD);
    }

    @Override
    @Transactional
    public void desactivarPlanCurricular(PlanCurricular plan) {
        PlanCurricular planBD = planCurricularDAO.find(plan.getId());
        Assert.isTrue(planBD.getEstadoEnum() == EstadoEnum.ACT, "Solo puede desactivarse un plan con estado Activo");
        planBD.setEstadoEnum(EstadoEnum.INA);
        planCurricularDAO.update(planBD);
    }

    @Override
    @Transactional
    public void activarPlanCurricular(PlanCurricular plan) {
        PlanCurricular planBD = planCurricularDAO.find(plan.getId());
        Assert.isTrue(Arrays.asList(INA, CRE).contains(planBD.getEstadoEnum()), "Solo puede activarse un plan con estado Inactivo");
        planBD.setEstadoEnum(EstadoEnum.ACT);
        planCurricularDAO.update(planBD);
    }

    @Override
    @Transactional
    public PlanCurricular clonarPlanCurricular(PlanCurricular planForm, CicloAcademico ciclo, DataSessionPivot ds) {
        PlanCurricular planBD = planCurricularDAO.find(planForm.getId());
        Assert.isTrue(Arrays.asList(ACT, INA).contains(planBD.getEstadoEnum()), "No está permitido clonar planes curriculares a partir de este");

        PlanCurricular planNew = new PlanCurricular();
        planNew.setCarrera(planBD.getCarrera());
        planNew.setCiclos(planBD.getCiclos());
        planNew.setOrientacionCarrera(planBD.getOrientacionCarrera());
        planNew.setEstadoEnum(CRE);
        planNew.setCiclos(planBD.getCiclos());
        planNew.setCicloInicioVigencia(ciclo);
        planCurricularDAO.save(planNew);

        List<CursoCurricula> cursosPlan = cursoCurriculaDAO.allByPlanCurricularACT(planBD);
        List<CursoAdicionalCurricula> adicionales = cursoAdicionalCurriculaDAO.allByPlanCurricular(planBD);
        List<CursoOpcionalCurricula> opcionales = cursoOpcionalCurriculaDAO.allByPlanCurricular(planBD);
        List<ResumenPlanCurricular> resumenes = resumenPlanCurricularDAO.allByPlan(planBD);

        List<RequisitoCursoCurricula> requisitos = requisitoCursoCurriculaDAO.allByCursosCurricula(cursosPlan);
        List<RequisitoCursoOpcional> requisitosOpc = requisitoCursoOpcionalDAO.allRequisitosByCursosElectivos(opcionales);

        Map<Long, CursoCurricula> mapCursoCurricula = new LinkedHashMap();
        for (CursoCurricula curso : cursosPlan) {
            CursoCurricula cc = new CursoCurricula();
            cc.setCreditos(curso.getCreditos());
            cc.setCreditosCurriculaRequisito(curso.getCreditosCurriculaRequisito());
            cc.setCreditosRequisito(curso.getCreditosRequisito());
            cc.setFechaRegistro(new Date());
            cc.setNumeroCiclo(curso.getNumeroCiclo());
            cc.setNumeroCurso(curso.getNumeroCurso());
            cc.setCurso(curso.getCurso());
            cc.setPlanCurricular(planNew);
            cc.setTipoCursoCurricula(curso.getTipoCursoCurricula());
            cc.setUserRegistro(ds.getUsuario());
            cc.setCursosCurricula(new ArrayList());
            cc.setRequisitosOr(curso.getRequisitosOr());

            mapCursoCurricula.put(cc.getCurso().getId(), cc);
            cursoCurriculaDAO.save(cc);
        }

        for (CursoAdicionalCurricula adi : adicionales) {
            CursoAdicionalCurricula ca = new CursoAdicionalCurricula();
            ca.setCurso(adi.getCurso());
            ca.setFechaRegistro(new Date());
            ca.setUserRegistro(ds.getUsuario());
            ca.setPlanCurricular(planNew);

            cursoAdicionalCurriculaDAO.save(ca);
        }

        Map<Long, CursoOpcionalCurricula> mapCursoOpcional = new LinkedHashMap();
        for (CursoOpcionalCurricula opc : opcionales) {
            CursoOpcionalCurricula coc = new CursoOpcionalCurricula();
            coc.setCreditos(opc.getCreditos());
            coc.setCreditosCurriculaRequisito(opc.getCreditosCurriculaRequisito());
            coc.setCreditosRequisito(opc.getCreditosRequisito());
            coc.setCurso(opc.getCurso());
            coc.setFechaRegistro(new Date());
            coc.setPlanCurricular(planNew);
            coc.setTipoCursoCurricula(opc.getTipoCursoCurricula());
            coc.setUserRegistro(ds.getUsuario());
            coc.setRequisitosCursoOpcionales(new ArrayList());
            coc.setRequisitosOr(opc.getRequisitosOr());

            mapCursoOpcional.put(coc.getCurso().getId(), coc);
            cursoOpcionalCurriculaDAO.save(coc);
        }

        for (RequisitoCursoCurricula req : requisitos) {
            RequisitoCursoCurricula rcc = new RequisitoCursoCurricula();
            CursoCurricula cc = mapCursoCurricula.get(req.getCursoCurricula().getCurso().getId());
            rcc.setCursoCurricula(cc);
            rcc.setCursoRequisito(mapCursoCurricula.get(req.getCursoRequisito().getCurso().getId()));
            rcc.setSimultaneo(req.getSimultaneo());
            rcc.setFechaRegistro(new Date());
            rcc.setUserRegistro(ds.getUsuario());

            cc.getCursosCurricula().add(rcc);
            requisitoCursoCurriculaDAO.save(rcc);
        }

        for (RequisitoCursoOpcional reqOpc : requisitosOpc) {
            RequisitoCursoOpcional rco = new RequisitoCursoOpcional();
            CursoOpcionalCurricula co = mapCursoOpcional.get(reqOpc.getCursoOpcional().getCurso().getId());
            rco.setCursoOpcional(co);
            rco.setSimultaneo(reqOpc.getSimultaneo());
            rco.setFechaRegistro(new Date());
            rco.setUserRegistro(ds.getUsuario());

            if (reqOpc.getCursoRequisitoOpcional() != null) {
                CursoOpcionalCurricula coc = mapCursoOpcional.get(reqOpc.getCursoRequisitoOpcional().getCurso().getId());
                rco.setCursoRequisitoOpcional(coc);
            }
            if (reqOpc.getCursoRequisitoCurricula() != null) {
                CursoCurricula rcc = mapCursoCurricula.get(reqOpc.getCursoRequisitoCurricula().getCurso().getId());
                rco.setCursoRequisitoCurricula(rcc);
            }
            requisitoCursoOpcionalDAO.save(rco);
        }

        for (ResumenPlanCurricular resumen : resumenes) {
            ResumenPlanCurricular rpc = new ResumenPlanCurricular();
            rpc.setCreditos(resumen.getCreditos());
            rpc.setMinimoCreditos(resumen.getCreditos());
            rpc.setCursos(resumen.getCursos());
            rpc.setTipoCursoCurricula(resumen.getTipoCursoCurricula());
            rpc.setPlanCurricular(planNew);
            resumenPlanCurricularDAO.save(rpc);
        }

        return planNew;
    }

    @Override
    @Transactional
    public void moveCurso(CursoCurricula cursoCurrForm, String direccion, DataSessionPivot ds) {
        CursoCurricula cursoCurrBD = cursoCurriculaDAO.find(cursoCurrForm.getId());

        PlanCurricular planBD = cursoCurrBD.getPlanCurricular();
        PlanCurricular planForm = cursoCurrForm.getPlanCurricular();
        Assert.isTrue(planBD.getId() == planForm.getId().longValue(), "El curso no pertenece al plan que está modificando");
        Assert.isTrue(cursoCurrBD.getNumeroCiclo() > 0, "Solo pueden moverse cursos ubicados en ciclos correctos");
        Assert.isFalse(cursoCurrBD.getNumeroCurso() == 1 && direccion.equals("DOWN"), "No es correcto mover este curso");

        List<CursoCurricula> cursosCurr = cursoCurriculaDAO.allByPlanCurricular(planBD);
        Map<Integer, List<CursoCurricula>> mapCursosCicloCurr = TypesUtil.convertListToMapList("numeroCiclo", cursosCurr);
        List<CursoCurricula> cursosCicloCurr = fillList(mapCursosCicloCurr.get(cursoCurrBD.getNumeroCiclo()));
        Map<Integer, CursoCurricula> mapCursosPosicion = TypesUtil.convertListToMap("numeroCurso", cursosCicloCurr);
        Integer next = cursoCurrBD.getNumeroCurso() + (direccion.equals("DOWN") ? -1 : (direccion.equals("UP") ? 1 : 0));
        CursoCurricula cursoNext = mapCursosPosicion.get(next);

        if (cursoNext == null) {
            cursoCurrBD.setNumeroCurso(next);
            cursoCurriculaDAO.update(cursoCurrBD);

        } else {
            cursoNext.setNumeroCurso(cursoCurrBD.getNumeroCurso());
            cursoCurriculaDAO.update(cursoNext);
            cursoCurrBD.setNumeroCurso(next);
            cursoCurriculaDAO.update(cursoCurrBD);
        }

    }

    @Override
    @Transactional
    public void generarAvanceCurricular(PlanCurricular plan, DataSessionPivot ds) {
        List<Alumno> alumnos = alumnoDAO.allByPlanCurricular(plan);
        avanceCurricularService.generarAvanceCurricularByAlumnosPregrados(alumnos, ds, null);
    }

    @Override
    @Transactional
    public void desvincularCursoCurricula(PlanCurricular plan, DataSessionPivot ds) {
        avanceCurricularService.desvincularCursoCurricula(plan, ds);
    }

    @Override
    public CursoAdicionalCurricula findCursoAdicional(Long cursoAdicionalId) {
        return cursoAdicionalCurriculaDAO.find(cursoAdicionalId);
    }

    @Override
    @Transactional
    public void updateCursoAdicional(CursoAdicionalCurricula cursoAdicionalCurricula, DataSessionPivot ds) {
        CursoAdicionalCurricula cacBD = cursoAdicionalCurriculaDAO.find(cursoAdicionalCurricula.getId());

        cacBD.setCicloInicio(cursoAdicionalCurricula.getCicloInicio());

        if (cursoAdicionalCurricula.getCicloFin().getId() != null) {
            cacBD.setCicloFin(cursoAdicionalCurricula.getCicloFin());
        } else {
            cacBD.setCicloFin(null);
        }

        cursoAdicionalCurriculaDAO.update(cacBD);
    }

    @Override
    public List<Carrera> allCarrerasByuser(Usuario usuario, Persona persona) {

        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(new Oficina(OficinaEnum.OERA.getId()), persona);

        if (colaborador != null) {
            return carreraDAO.all();
        }

        List<UsuarioRol> usu = usuarioRolDAO.findByUsuario(usuario);

        List<Long> idFac = new ArrayList();
        List<Long> idEsp = new ArrayList();

        for (UsuarioRol usuarioRol : usu) {
            Oficina ofi = usuarioRol.getOficina();
            TipoOficina tipoOfi = ofi.getTipoOficina();
            if (tipoOfi.getCodigo().equals(TipoOficinaEnum.FAC.name())) {
                idFac.add(ofi.getInstanciaOficina());
            } else if (tipoOfi.getCodigo().equals(TipoOficinaEnum.ESP.name())) {
                idEsp.add(ofi.getInstanciaOficina());
            }
        }
        List<Carrera> all = new ArrayList();
        List<Carrera> carrera1 = carreraDAO.all(idEsp);
        List<Carrera> carrera2 = carreraDAO.allOficinaAndIds(idFac);

        all.addAll(carrera1);
        all.addAll(carrera2);
        return all;

    }

    @Async
    @Override
    public void asignacionMasivaCursoCurricula(Carrera carrera, DataSessionPivot ds) {
        carrera = carreraDAO.find(carrera.getId());

        List<PlanCurricular> planesCurricular = planCurricularDAO.allActivosByCarrera(carrera);
        List<PlanCurricular> planesCurriculars = planCurricularDAO.all();

        List<CursoCurricula> cursoCurriculasAll = cursoCurriculaDAO.allByPlanes(planesCurriculars);
        Map<Long, List<CursoCurricula>> mapCursoCurriculaAllPlanes = TypesUtil.convertListToMapList("planCurricular.id", cursoCurriculasAll);

        CicloAcademico cicloInicia = null;
        cicloInicia = planesCurricular.stream().map(x -> x.getCicloInicioVigencia()).min(Comparator.comparing(CicloAcademico::getCodigo)).get();
        Map<String, List<PlanCurricular>> mapPlanesByCiclo = TypesUtil.convertListToMapList("cicloInicioVigencia.codigo", planesCurricular);
        Map<String, CicloAcademico> mapCiclosPlanes = TypesUtil.convertListToMap("cicloInicioVigencia.codigo", "cicloInicioVigencia", planesCurricular);

        List<Alumno> alumnos = alumnoDAO.allByCarreraCicloMayores(carrera, cicloInicia.getCodigo());
        List<CursoHabilEscuela> cursosHabilEscuela = new ArrayList();
        if (carrera.getModalidadEstudio().isPostgrado()) {
            cursosHabilEscuela = cursoHabilEscuelaDAO.allAlumnos(alumnos);
        }
        Map<Long, List<CursoHabilEscuela>> mapCursoHabilEscuela = TypesUtil.convertListToMapList("alumno.id", cursosHabilEscuela);
        List<String> codigosCiclosPlanes = new ArrayList<String>(mapCiclosPlanes.keySet());

        Collections.sort(codigosCiclosPlanes);
        Collections.reverse(codigosCiclosPlanes);

        Map<Long, List<CursoCurricula>> mapCursoCurriculaAll = new LinkedHashMap();
        Map<Long, CursoCurricula> mapCursoCurriculaByCurso = new HashMap<>();
        Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurriculaAll = new LinkedHashMap();
        Map<Long, List<CursoEquivalente>> mapCursosEquivalentesAll = new LinkedHashMap();

        this.obtenerDataVarios(planesCurricular, mapCursoCurriculaAll, mapRequisitoCursoCurriculaAll, mapCursosEquivalentesAll, mapCursoCurriculaByCurso);

        List<CursoEquivalenteElectivo> cursoEquivalenteElectivos = cursoEquivalenteElectivoDAO.allCursoPlanCurricula(planesCurricular);
        Map<Long, List<CursoEquivalenteElectivo>> mapEquivalenteElectivo = TypesUtil.convertListToMapList("cursoOpcionalCurricula.planCurricular.id", cursoEquivalenteElectivos);

        List<CursoOpcionalCurricula> cursoOpcionalCurriculas = cursoOpcionalCurriculaDAO.allByPlanCurricular(planesCurricular);
        Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcional = TypesUtil.convertListToMapList("planCurricular.id", cursoOpcionalCurriculas);

        List<CursoOpcionalCurricula> cursoOpcionalAllPlanes = cursoOpcionalCurriculaDAO.allNotPlanCurricularAndCurso(planesCurricular);
        Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll = TypesUtil.convertListToMapList("planCurricular.id", cursoOpcionalAllPlanes);

        List<RequisitoCursoOpcional> requisitoCursoOpcionals = requisitoCursoOpcionalDAO.allRequisitosByCursosElectivos(cursoOpcionalAllPlanes);
        Map<Long, List<RequisitoCursoOpcional>> mapRequisitoCursoOpcionals = TypesUtil.convertListToMapList("cursoOpcional.id", requisitoCursoOpcionals);

        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allActivoByAlumnosCicloActivo(alumnos);
        Map<Long, List<MatriculaCurso>> mapCursosMatriculados = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", cursosMatriculados);

        List<AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumnos(alumnos);
        Map<String, AlumnoCicloCurso> mapCursosAprobadosKey = TypesUtil.convertListToMap("alumnoCursoKey", cursosAprobados);

        List<AlumnoCicloCurso> cursosDesapr = alumnoCicloCursoDAO.allDesaproActivoByAlumnos(alumnos);
        for (AlumnoCicloCurso alumnoCicloCurso : cursosDesapr) {
            if (mapCursosAprobadosKey.get(alumnoCicloCurso.getAlumnoCursoKey()) == null) {
                cursosAprobados.add(alumnoCicloCurso);
                mapCursosAprobadosKey.put(alumnoCicloCurso.getAlumnoCursoKey(), alumnoCicloCurso);
            }
        }
        Map<Long, List<AlumnoCicloCurso>> mapCursosAprobados = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", cursosAprobados);

        List<AlumnoCicloCurso> cursosVecesLlevado = alumnoCicloCursoDAO.allVecesLlevadoByAlumnos(alumnos);
        Map<String, AlumnoCicloCurso> mapTodosCursosVecesLlevado = TypesUtil.convertListToMap("alumnoCursoKey", cursosVecesLlevado);
//        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCursosVecesLlevado = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", cursosVecesLlevado);

        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnos(alumnos);
        Map<Long, List<AlumnoCursoCurricula>> mapAlumnoCursoCurricula = TypesUtil.convertListToMapList("alumno.id", alumnoCursoCurriculas);

        int count = 0;
        for (AlumnoCicloCurso cursoAprobado : cursosAprobados) {
            cursoAprobado.setVecesCursadoTransient(0);
            AlumnoCicloCurso cursoVeces = mapTodosCursosVecesLlevado.get(cursoAprobado.getAlumnoCursoKey());
            if (cursoVeces == null) {
                continue;
            }
            cursoAprobado.setVecesCursadoTransient(cursoVeces.getVecesCursado());
        }
        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();

        List<AlumnoAvanceCurricular> alumnosAvanceCurriculars = alumnoAvanceCurricularDAO.allByAlumnos(alumnos);

        List<ResumenPlanCurricular> alumnosResumenPlanCurriculars = resumenPlanCurricularDAO.all();
        Map<Long, List<ResumenPlanCurricular>> mapResumenPlanCurriculaAll = TypesUtil.convertListToMapList("planCurricular.id", alumnosResumenPlanCurriculars);

        visorAsignaCurricula.putTope(carrera, alumnos.size() * 2);
        for (Alumno alumno : alumnos) {

            List<CursoHabilEscuela> habilEscuelas = mapCursoHabilEscuela.get(alumno.getId());
            OrientacionCarrera orientacionCarrera = alumno.getOrientacionCarrera();

            List<AlumnoAvanceCurricular> avanceCurriculars = alumnosAvanceCurriculars.stream().filter(x -> Objects.equals(x.getAlumno().getId(), alumno.getId())).collect(Collectors.toList());

            String codigoCicloAlumno = (String) ObjectUtil.getParentTree(alumno, "cicloIngreso.codigo");

            count++;
            String codigoCicloPlan = this.getIndiceCicloAcademico(codigoCicloAlumno, codigosCiclosPlanes);
            List<PlanCurricular> planesBD = mapPlanesByCiclo.get(codigoCicloPlan);
            PlanCurricular planCurricularBD = null;
            if (orientacionCarrera != null) {
                planCurricularBD = planesBD.stream().filter(x -> Objects.equals(x.getOrientacionCarrera().getId(), orientacionCarrera.getId())).findAny().orElse(null);
            } else {
                planCurricularBD = planesBD.get(0);
            }

            PlanCurricular planBD = planCurricularBD;

            List<ResumenPlanCurricular> resumenPlanCurriculars = mapResumenPlanCurriculaAll.get(planBD.getId());
            Map<TipoCursoCurriculaEnum, ResumenPlanCurricular> mapResumenPlanCurricular = TypesUtil.convertListToMap("tipoCursoCurricula.codigoEnum", resumenPlanCurriculars);

            List<MatriculaCurso> cursosMatriculadosAlumno = fillList(mapCursosMatriculados.get(alumno.getId()));
            List<AlumnoCicloCurso> cursosAprobadosAlumno = fillList(mapCursosAprobados.get(alumno.getId()));
            List<CursoCurricula> cursosCurriculaPLan = fillList(mapCursoCurriculaAll.get(planBD.getId()));
            Map<Long, CursoCurricula> mapCursoCurriculaPlan = TypesUtil.convertListToMap("id", cursosCurriculaPLan);
            List<AlumnoCursoCurricula> alumnoCursoCurriculaOld = mapAlumnoCursoCurricula.get(alumno.getId());
            List<CursoOpcionalCurricula> opcionalCurriculas = mapCursoOpcional.get(planBD.getId());
            List<CursoEquivalenteElectivo> equivalenteElectivos = mapEquivalenteElectivo.get(planBD.getId());

            visorAsignaCurricula.incrementar(carrera);
            logger.debug("ALUMNO -------------------------------> {}", alumno.getCodigo());
            avanceCurricularAsincronoService.crearAvanceCurricular(
                    alumno,
                    planBD,
                    mapCursoCurriculaPlan,
                    mapRequisitoCursoCurriculaAll,
                    mapCursosEquivalentesAll,
                    mapTodosCursosVecesLlevado,
                    cursosMatriculadosAlumno,
                    cursosAprobadosAlumno,
                    alumnoCursoCurriculaOld,
                    opcionalCurriculas,
                    tipoCursoCurriculas,
                    mapResumenPlanCurricular,
                    avanceCurriculars,
                    equivalenteElectivos,
                    mapCursoOpcionalAll,
                    planesCurriculars,
                    mapCursoCurriculaAllPlanes,
                    habilEscuelas,
                    mapRequisitoCursoOpcionals,
                    ds);
        }

    }

    private String getIndiceCicloAcademico(String codigoCicloAlumno, List<String> codigosCiclosPlanes) {
        for (String codigoCicloPlan : codigosCiclosPlanes) {
            if (codigoCicloAlumno.compareTo(codigoCicloPlan) >= 0) {
                return codigoCicloPlan;
            }
        }
        return null;
    }

    @Override
    public void desvincularMasivaCursoCurricula(Carrera carrera, DataSessionPivot ds) {

        List<PlanCurricular> planesCurricular = planCurricularDAO.allActivosByCarrera(carrera);

        Assert.isFalse(planesCurricular.isEmpty(), "La especialización no cuenta con planes curriculares activos.");
        CicloAcademico cicloInicia = null;
        cicloInicia = planesCurricular.stream().map(x -> x.getCicloInicioVigencia()).min(Comparator.comparing(CicloAcademico::getCodigo)).get();

        List<Alumno> alumnos = alumnoDAO.allByCarreraCicloMayores(carrera, cicloInicia.getCodigo());
        visorAsignaCurricula.putTope(carrera, alumnos.size() * 2);

        for (Alumno alumno : alumnos) {

            avanceCurricularAsincronoService.limpiarAlumno(alumno);
        }

    }

    private void obtenerDataVarios(
            List<PlanCurricular> planes,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula,
            Map<Long, List<CursoEquivalente>> mapCursosEquivalentes,
            Map<Long, CursoCurricula> mapCursoCurriculaByCurso) {

        List<RequisitoCursoCurricula> requisitoCursoCurriculas = requisitoCursoCurriculaDAO.allByPlanes(planes);
        Map<Long, List<RequisitoCursoCurricula>> mapRequisitoTemp = TypesUtil.convertListToMapList("cursoCurricula.id", requisitoCursoCurriculas);

        List<CursoEquivalente> cursoEquivalentes = cursoEquivalenteDAO.allActivoByPlanes(planes);
        Map<Long, List<CursoEquivalente>> mapEquivalentes = TypesUtil.convertListToMapList("cursoCurricula.id", cursoEquivalentes);

        List<CursoCurricula> cursosCurri = cursoCurriculaDAO.allByPlanes(planes);
        for (CursoCurricula cursoCurr : cursosCurri) {

            PlanCurricular plan = cursoCurr.getPlanCurricular();

            List<CursoCurricula> cursosCurriculaPlan = mapCursoCurriculaAll.get(plan.getId());
            if (cursosCurriculaPlan == null) {
                cursosCurriculaPlan = new ArrayList();
                mapCursoCurriculaAll.put(plan.getId(), cursosCurriculaPlan);
            }

            cursosCurriculaPlan.add(cursoCurr);

            List<RequisitoCursoCurricula> requisitos = fillList(mapRequisitoTemp.get(cursoCurr.getId()));
            cursoCurr.setRequisitosCursoCurricula(requisitos);
            mapRequisitoCursoCurricula.put(cursoCurr.getId(), requisitos);

            List<CursoEquivalente> equivalencias = fillList(mapEquivalentes.get(cursoCurr.getId()));
            mapCursosEquivalentes.put(cursoCurr.getId(), equivalencias);
        }

    }

    private List fillList(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
    }

    @Override
    public List<Carrera> filtrarByPlanes(List<Carrera> carrerasTodas) {
        List<Carrera> carreras = new ArrayList();
        List<PlanCurricular> planes = planCurricularDAO.all();
        Map<Long, List<PlanCurricular>> mapPlanes = TypesUtil.convertListToMapList("carrera.id", planes);
        for (Carrera carrera : carrerasTodas) {
            if (carrera.getModalidadEstudio().getCodigoEnum() == PRE) {

                List<PlanCurricular> planesCarr = mapPlanes.get(carrera.getId());
                if (planesCarr != null) {
                    carreras.add(carrera);
                }
            }
        }
        return carreras;
    }

    @Override
    public void verificarAsignacion(Carrera carrera) {
        Carrera carr = carreraDAO.find(carrera.getId());
        if (!visorAsignaCurricula.addCarrera(carr, VisorAsignaCurricula.AccionEnum.DESVINCULA)) {
            throw new PhobosException("Ya existe un proceso de asignación masiva de planes para esta carrera");
        }
    }

    @Override
    public Carrera getCarreraActiva() {
        return visorAsignaCurricula.getCarreraActiva();
    }

    @Override
    @Transactional
    public void updateResumen(Integer minCreditos, Integer totalCreditos, ResumenPlanCurricular resumenForm) {
        resumenForm = resumenPlanCurricularDAO.find(resumenForm.getId());
        if (totalCreditos != null) {
            resumenForm.setCreditos(totalCreditos);
        } else if (minCreditos != null) {
            resumenForm.setMinimoCreditos(minCreditos);
        }

        if (resumenForm.getTipoCursoCurricula().getCodigoEnum() == ELC) {
            int restoCreditosELC = resumenForm.getCreditos() - resumenForm.getMinimoCreditos();
            TipoCursoCurricula tipoCurriculaELE = tipoCursoCurriculaDAO.findByCodigo(ELE);
            TipoCursoCurricula tipoCurriculaCULT = tipoCursoCurriculaDAO.findByCodigo(CULT);
            TipoCursoCurricula tipoCurriculaPROD = tipoCursoCurriculaDAO.findByCodigo(PROD);
            TipoCursoCurricula tipoCurriculaTEC = tipoCursoCurriculaDAO.findByCodigo(TECIND);

            ResumenPlanCurricular resumenELE = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(tipoCurriculaELE, resumenForm.getPlanCurricular());
            ResumenPlanCurricular resumenCULT = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(tipoCurriculaCULT, resumenForm.getPlanCurricular());
            ResumenPlanCurricular resumenPROD = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(tipoCurriculaPROD, resumenForm.getPlanCurricular());
            ResumenPlanCurricular resumenTEC = resumenPlanCurricularDAO.findByTipoCursoCurrPlan(tipoCurriculaTEC, resumenForm.getPlanCurricular());

            if (resumenCULT != null) {
                Assert.isTrue(resumenCULT.getCreditos() <= restoCreditosELC, "Los créditos para cursos de cultivos debe ser menor o igual a " + restoCreditosELC);
                restoCreditosELC = restoCreditosELC - resumenCULT.getMinimoCreditos();
            }
            if (resumenPROD != null) {
                Assert.isTrue(resumenPROD.getCreditos() <= restoCreditosELC, "Los créditos para cursos de producción debe ser menor o igual a " + restoCreditosELC);
                restoCreditosELC = restoCreditosELC - resumenPROD.getMinimoCreditos();
            }
            if (resumenTEC != null) {
                Assert.isTrue(resumenTEC.getCreditos() <= restoCreditosELC, "Los créditos para cursos de tecnología/industrialización debe ser menor o igual a " + restoCreditosELC);
                restoCreditosELC = restoCreditosELC - resumenTEC.getMinimoCreditos();
            }

            resumenELE.setMinimoCreditos(0);
            resumenELE.setCreditos(restoCreditosELC);
            resumenPlanCurricularDAO.update(resumenELE);
        }

        resumenPlanCurricularDAO.update(resumenForm);
    }

    @Override
    @Transactional
    public void allUpdateResumen() {
        List<PlanCurricular> planCurriculars = planCurricularDAO.allActivo();
        List<ResumenPlanCurricular> resumenPlanCurriculars = resumenPlanCurricularDAO.allByPlanes(planCurriculars);
        for (PlanCurricular planCurricular : planCurriculars) {
            ResumenPlanCurricular rpcs = resumenPlanCurriculars.stream()
                    .filter(x -> Objects.equals(x.getPlanCurricular().getId(), planCurricular.getId()) && x.getTipoCursoCurricula().getCodigoEnum() == DEP).findAny().orElse(null);
            List<CursoCurricula> cursoCurriculas = cursoCurriculaDAO.allByPlanCurricular(planCurricular);
            cursoCurriculas = cursoCurriculas.stream().filter(x -> x.getCurso().getCodigo().equals(CODIGO_CURSO_DEP)).collect(Collectors.toList());
            int count = cursoCurriculas.stream().mapToInt(x -> x.getCreditos()).sum();
            if (!cursoCurriculas.isEmpty() && rpcs == null) {
                TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(DEP);
                rpcs = new ResumenPlanCurricular();
                rpcs.setCreditos(count);
                rpcs.setCursos(cursoCurriculas.size());
                rpcs.setMinimoCreditos(count);
                rpcs.setPlanCurricular(planCurricular);
                rpcs.setTipoCursoCurricula(tipoCursoCurricula);
                resumenPlanCurricularDAO.save(rpcs);
            }
            if (!cursoCurriculas.isEmpty() && rpcs != null) {
                rpcs.setCreditos(count);
                rpcs.setMinimoCreditos(cursoCurriculas.size());
                rpcs.setCursos(cursoCurriculas.size());
                resumenPlanCurricularDAO.update(rpcs);
            }
        }
    }

    @Override
    @Transactional
    public void allUpdateResumenPost() {
        List<PlanCurricular> planCurriculars = planCurricularDAO.allActivo();
//        planCurriculars = planCurriculars.stream().filter(x -> x.getCarrera().getModalidadEstudio().isPostgrado()).collect(Collectors.toList());
        List<ResumenPlanCurricular> resumenPlanCurriculars = resumenPlanCurricularDAO.allByPlanes(planCurriculars);
        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();
        for (PlanCurricular planCurricular : planCurriculars) {
            List<ResumenPlanCurricular> resumen = resumenPlanCurriculars.stream().filter(x -> Objects.equals(x.getPlanCurricular().getId(), planCurricular.getId())).collect(Collectors.toList());
            List<CursoCurricula> cursoCurriculas = cursoCurriculaDAO.allByPlanCurricularACT(planCurricular);
            Map<Long, ResumenPlanCurricular> mapResumen = TypesUtil.convertListToMap("tipoCursoCurricula.id", resumen);
            Map<TipoCursoCurriculaEnum, Integer> mapTipoCurso = cursoCurriculas.stream().collect(Collectors.groupingBy(CursoCurricula::getTipoCursoCurriculaEnum, Collectors.summingInt(x -> x.getCreditos())));
            Map<Long, List<CursoCurricula>> mapTipoCursoCoun = TypesUtil.convertListToMapList("tipoCursoCurricula.id", cursoCurriculas);
            for (TipoCursoCurricula tcc : tipoCursoCurriculas) {
                ResumenPlanCurricular curricular = mapResumen.get(tcc.getId());
                if (mapTipoCurso.get(tcc.getCodigoEnum()) == null) {
                    continue;
                }
                int count = mapTipoCurso.get(tcc.getCodigoEnum());
                List<CursoCurricula> countCur = mapTipoCursoCoun.get(tcc.getId());
                if (!cursoCurriculas.isEmpty() && curricular == null) {
                    curricular = new ResumenPlanCurricular();
                    curricular.setCreditos(count);
                    curricular.setCursos(countCur.size());
                    curricular.setMinimoCreditos(count);
                    curricular.setPlanCurricular(planCurricular);
                    curricular.setTipoCursoCurricula(tcc);
                    resumenPlanCurricularDAO.save(curricular);
                }
                if (!cursoCurriculas.isEmpty() && curricular != null) {
                    curricular.setCreditos(count);
                    curricular.setMinimoCreditos(count);
                    curricular.setCursos(countCur.size());
                    resumenPlanCurricularDAO.update(curricular);
                }
            }

        }
    }
}
