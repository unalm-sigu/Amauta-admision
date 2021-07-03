package pe.edu.lamolina.amauta.controller.academico.ciclo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.NumeroCicloAcademicoEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTrasladoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

@Service
@Transactional(readOnly = true)
public class CicloAcademicoServiceImp implements CicloAcademicoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    TramiteTrasladoDAO tramiteTrasladoDAO;

    @Autowired
    PlanCurricularDAO planCurricularDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AvanceCurricularService avanceCurricularService;

    @Override
    public List<CicloAcademico> allCicloAcademico(Integer maxResultado) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return cicloAcademicoDAO.allForChanges(maxResultado, modalidad);
    }

    @Override
    public CicloAcademico getCicloAcademico(Long cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }

    @Override
    @Transactional
    public void delete(CicloAcademico cicloAcademico) {
        cicloAcademicoDAO.delete(cicloAcademico);
    }

    @Override
    @Transactional
    public void save(CicloAcademico cicloAcademico, Usuario usuario) {

        ObjectUtil.eliminarAttrSinId(cicloAcademico, "modalidadEstudio");
        if (cicloAcademico.getModalidadEstudio() == null) {
            throw new PhobosException("Tiene que especificar la modalidad de estudio.");
        }
        for (NumeroCicloAcademicoEnum numeroCicloAcademicoEnum : NumeroCicloAcademicoEnum.values()) {
            CicloAcademico cicloAcademicoNew = new CicloAcademico();
            cicloAcademicoNew.setEstadoEnum(CicloAcademicoEstadoEnum.CRE);
            cicloAcademicoNew.setNumeroCiclo(numeroCicloAcademicoEnum.getValue());
            cicloAcademicoNew.setDescripcion(numeroCicloAcademicoEnum.getDescripcion().replace("XXXX", cicloAcademico.getYear().toString()));
            cicloAcademicoNew.setDescripcion2(numeroCicloAcademicoEnum.getDescripcion2().replace("XXXX", cicloAcademico.getYear().toString()));
            cicloAcademicoNew.setDescripcion3(numeroCicloAcademicoEnum.getDescripcion3().replace("XXXX", cicloAcademico.getYear().toString()));
            cicloAcademicoNew.setCodigo(numeroCicloAcademicoEnum.getCodigo().replace("XXXX", cicloAcademico.getYear().toString()));
            cicloAcademicoNew.setFechaRegistro(new Date());
            cicloAcademicoNew.setUserRegistro(usuario);
            cicloAcademicoNew.setYear(cicloAcademico.getYear());
            cicloAcademicoNew.setModalidadEstudio(cicloAcademico.getModalidadEstudio());
            cicloAcademicoNew.setFechaEntregaActas(cicloAcademico.getFechaEntregaActas());
            cicloAcademicoDAO.save(cicloAcademicoNew);
        }
    }

    @Override
    @Transactional
    public void update(CicloAcademico cicloAcademico, Usuario usuario) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);
        ObjectUtil.eliminarAttrSinId(cicloAcademico, "modalidadEstudio");
        if (cicloAcademico.getModalidadEstudio() == null) {
            throw new PhobosException("Tiene que especificar la modalidad de estudio.");
        }
        NumeroCicloAcademicoEnum numeroCicloAcademicoEnum = NumeroCicloAcademicoEnum.get(cicloAcademicoDB.getNumeroCiclo());
        cicloAcademico.setEstadoEnum(CicloAcademicoEstadoEnum.CRE);
        cicloAcademicoDB.setYear(cicloAcademico.getYear());
        cicloAcademicoDB.setModalidadEstudio(cicloAcademico.getModalidadEstudio());
        cicloAcademicoDB.setDescripcion(numeroCicloAcademicoEnum.getDescripcion().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setDescripcion2(numeroCicloAcademicoEnum.getDescripcion2().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setDescripcion3(numeroCicloAcademicoEnum.getDescripcion3().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setCodigo(numeroCicloAcademicoEnum.getCodigo().replace("XXXX", cicloAcademico.getYear().toString()));
        cicloAcademicoDB.setFechaEntregaActas(cicloAcademico.getFechaEntregaActas());
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

    @Override
    public CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.findByCiclo(cicloAcademico);
    }

    @Override
    public List<CicloAcademico> allByDynatable(DynatableFilter filter) {
        if (filter.getQueries() == null) {
            filter.setFiltered(0);
            filter.setTotal(0);
            return new ArrayList();
        }
        return cicloAcademicoDAO.allByDynatable(filter);
    }

    @Override
    public List<ModalidadEstudio> allPrePostgrado(Compania cia) {
        return modalidadEstudioDAO.allPrePostgrado(cia);
    }

    @Override
    @Transactional
    public void anular(CicloAcademico cicloAcademico) {

        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.CFG.name().equalsIgnoreCase(cicloAcademicoDB.getEstado())
                || CicloAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser CONFIGURADO o ACTIVO");
        }

        List<GrupoSeccion> grupos = grupoSeccionDAO.allActivoByCiclo(cicloAcademicoDB);
        if (!grupos.isEmpty()) {
            throw new PhobosException("No puede anular un ciclo académico que contiene datos");
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.ANU);
        cicloAcademicoDB.setMotivoAnulacion(cicloAcademico.getMotivoAnulacion());
        cicloAcademicoDAO.update(cicloAcademicoDB);
    }

    @Override
    @Transactional
    public void desactivar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.CRE.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser CREADO");
        }

        List<GrupoSeccion> grupos = grupoSeccionDAO.allActivoByCiclo(cicloAcademicoDB);
        if (!grupos.isEmpty()) {
            throw new PhobosException("No puede desactivar un ciclo académico que contiene datos");
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.DES);
        cicloAcademicoDB.setMotivoAnulacion("No se usa el ciclo.");
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    @Transactional
    public void configurar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.CRE.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser CREADO");
        }
        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.CFG);
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    @Transactional
    public void activar(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.CFG.name().equalsIgnoreCase(cicloAcademicoDB.getEstado())
                || CicloAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser CONFIGURADO o ACTIVO");
        }

        CicloAcademico cicloAcademicoActivo = cicloAcademicoDAO.findActivoByModalidad(cicloAcademicoDB.getModalidadEstudio());
        if (cicloAcademicoActivo != null) {
            cicloAcademicoActivo.setEstadoEnum(CicloAcademicoEstadoEnum.PEND);
            cicloAcademicoDAO.update(cicloAcademicoActivo);
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.ACT);
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    @Transactional
    public void cerrar(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.PEND.name().equalsIgnoreCase(cicloAcademicoDB.getEstado())
                || CicloAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser PENDIENTE o ACTIVO");
        }

        List<GrupoSeccion> grupos = grupoSeccionDAO.allActivoByCicloGrupoNoCerrado(cicloAcademicoDB);
        if (!grupos.isEmpty()) {
            throw new PhobosException("No se puede cerrar el ciclo académico , aun contiene actas sin cerrar.");
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.CER);
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    @Transactional
    public void pendiente(CicloAcademico cicloAcademico) {
        CicloAcademico cicloAcademicoDB = cicloAcademicoDAO.findByCiclo(cicloAcademico);

        if (!(CicloAcademicoEstadoEnum.ACT.name().equalsIgnoreCase(cicloAcademicoDB.getEstado()))) {
            throw new PhobosException("Su estado previo debe ser ACTIVO");
        }

        cicloAcademicoDB.setEstadoEnum(CicloAcademicoEstadoEnum.PEND);
        cicloAcademicoDAO.update(cicloAcademicoDB);

    }

    @Override
    public List<Integer> allYear() {
        List<Integer> margen = new ArrayList<>();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer year = cal.get(Calendar.YEAR);
        margen.add(year - 1);
        margen.add(year);
        margen.add(year + 1);
        margen.add(year + 2);
        return margen;
    }

    @Override
    @Transactional
    public void changeVisiblelogin(CicloAcademico cicloAcademico) {
        CicloAcademico academico = cicloAcademicoDAO.find(cicloAcademico);
        academico.setVisibleLogin(academico.getVisibleLogin() ? false : true);
        cicloAcademicoDAO.update(academico);
    }

//    @Async
    @Override
    @Transactional
    public List<Alumno> ejecutarTramiteAcademicos(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        cicloAcademico = cicloAcademicoDAO.find(cicloAcademico);
        CicloAcademico academico = cicloAcademicoDAO.findAnteriorActivo(cicloAcademico);
        List<TramiteTraslado> tramiteTraslados = tramiteTrasladoDAO.findByCiclo(academico);
        List<Alumno> alumnos = new ArrayList<>();
        for (TramiteTraslado tramiteTraslado : tramiteTraslados) {
            Alumno alumno = tramiteTraslado.getTramite().getAlumno();
            alumno.setCarrera(tramiteTraslado.getCarrera());

            OrientacionCarrera orientacionCarrera = alumno.getOrientacionCarrera();
            List<PlanCurricular> planCurriculars = planCurricularDAO.allActivoByCarreraOrientacion(tramiteTraslado.getCarrera());
            Map<String, List<PlanCurricular>> mapPlanesByCiclo = TypesUtil.convertListToMapList("cicloInicioVigencia.codigo", planCurriculars);
            Map<String, CicloAcademico> mapCiclosPlanes = TypesUtil.convertListToMap("cicloInicioVigencia.codigo", "cicloInicioVigencia", planCurriculars);
            String codigoCicloAlumno = (String) ObjectUtil.getParentTree(alumno, "cicloIngreso.codigo");

            List<String> codigosCiclosPlanes = new ArrayList<String>(mapCiclosPlanes.keySet());

            Collections.sort(codigosCiclosPlanes);
            Collections.reverse(codigosCiclosPlanes);

            String codigoCicloPlan = this.getIndiceCicloAcademico(codigoCicloAlumno, codigosCiclosPlanes);
            List<PlanCurricular> planesBD = mapPlanesByCiclo.get(codigoCicloPlan);
            PlanCurricular planCurricularBD = null;
            for (PlanCurricular planCurricular : planesBD) {
                if (planCurricular.getOrientacionCarrera() == null) {
                    planCurricularBD = planCurricular;
                    alumno.setOrientacionCarrera(null);
                    break;
                } else {
                    if (orientacionCarrera != null && Objects.equals(planCurricular.getOrientacionCarrera().getId(), orientacionCarrera.getId())) {
                        alumno.setOrientacionCarrera(planCurricular.getOrientacionCarrera());
                        planCurricularBD = planCurricular;
                    }
                }
            }
            alumno.setPlanCurricular(planCurricularBD);
            alumnoDAO.updateColumns(alumno, "carrera", "planCurricular");

            alumnos.add(alumno);
        }

        return alumnos;

    }

    private String getIndiceCicloAcademico(String codigoCicloAlumno, List<String> codigosCiclosPlanes) {
        for (String codigoCicloPlan : codigosCiclosPlanes) {
            if (codigoCicloAlumno.compareTo(codigoCicloPlan) >= 0) {
                return codigoCicloPlan;
            }
        }
        return null;
    }

}
