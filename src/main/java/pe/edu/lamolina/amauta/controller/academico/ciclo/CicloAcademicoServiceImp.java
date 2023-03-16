package pe.edu.lamolina.amauta.controller.academico.ciclo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
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
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTrasladoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class CicloAcademicoServiceImp implements CicloAcademicoService {

    private final AlumnoDAO alumnoDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final GrupoSeccionDAO grupoSeccionDAO;
    private final ModalidadEstudioDAO modalidadEstudioDAO;
    private final PlanCurricularDAO planCurricularDAO;
    private final TramiteTrasladoDAO tramiteTrasladoDAO;

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
    public void save(CicloAcademico cicloForm, DataSessionPivot ds) {

        ObjectUtil.eliminarAttrSinId(cicloForm, "modalidadEstudio");
        Assert.isNotNull(cicloForm.getModalidadEstudio(), "Tiene que especificar la modalidad de estudio.");
        Assert.isNotNull(cicloForm.getModalidadEstudio().getId(), "Tiene que especificar la modalidad de estudio.");
        Assert.isNotNull(cicloForm.getYear(), "Tiene que especificar el año.");

        ModalidadEstudio modalidad = modalidadEstudioDAO.find(cicloForm.getModalidadEstudio().getId());
        List<CicloAcademico> ciclosExistentes = cicloAcademicoDAO.allByYearModalidadEnum(cicloForm.getYear(), modalidad.getCodigoEnum());
        Assert.isTrue(ciclosExistentes.isEmpty(), "Ya se han creado los ciclos para el año " + cicloForm.getYear() + " de la modalidad " + modalidad.getNombre());

        for (NumeroCicloAcademicoEnum nroEnum : NumeroCicloAcademicoEnum.values()) {
            CicloAcademico cicloNew = new CicloAcademico();

            cicloNew.setEstadoEnum(CicloAcademicoEstadoEnum.CRE);
            cicloNew.setEstadoObuEnum(CicloAcademicoEstadoEnum.CRE);
            cicloNew.setEstadoAdmisionEnum(CicloAcademicoEstadoEnum.CRE);
            cicloNew.setEstadoSubvencionesEnum(CicloAcademicoEstadoEnum.CRE);

            cicloNew.setNumeroCiclo(nroEnum.getValue());
            cicloNew.setTipo(this.getTipo(cicloNew.getNumeroCiclo()));

            cicloNew.setDescripcion(nroEnum.getDescripcion().replace("XXXX", cicloForm.getYear().toString()));
            cicloNew.setDescripcion2(nroEnum.getDescripcion2().replace("XXXX", cicloForm.getYear().toString()));
            cicloNew.setDescripcion3(nroEnum.getDescripcion3().replace("XXXX", cicloForm.getYear().toString()));
            cicloNew.setCodigo(nroEnum.getCodigo().replace("XXXX", cicloForm.getYear().toString()));
            cicloNew.setCodigoAnterior(this.getCodigoAnterior(cicloForm, nroEnum));
            cicloNew.setYear(cicloForm.getYear());
            cicloNew.setModalidadEstudio(cicloForm.getModalidadEstudio());
            cicloNew.setFechaEntregaActas(cicloForm.getFechaEntregaActas());

            cicloNew.setUserRegistro(ds.getUsuario());
            cicloNew.setFechaRegistro(new Date());
            cicloAcademicoDAO.save(cicloNew);
        }
    }

    private TipoCicloEnum getTipo(String numeroCiclo) {
        List<String> nrosRegulares = Arrays.asList("1", "2");
        if (nrosRegulares.contains(numeroCiclo)) {
            return TipoCicloEnum.REG;
        }
        return TipoCicloEnum.NIV;
    }

    private String getCodigoAnterior(CicloAcademico cicloForm, NumeroCicloAcademicoEnum nroEnum) {
        TipoCicloEnum tipoEnum = this.getTipo(nroEnum.getValue());
        if (tipoEnum == TipoCicloEnum.REG) {
            return cicloForm.getYear() + nroEnum.getValue();
        }
        if (tipoEnum == TipoCicloEnum.NIV) {
            return cicloForm.getYear() + nroEnum.getNumeroCiclo();
        }
        return null;
    }

    @Override
    @Transactional
    public void update(CicloAcademico cicloForm, DataSessionPivot ds) {
        CicloAcademico cicloDB = cicloAcademicoDAO.findByCiclo(cicloForm);
        ObjectUtil.eliminarAttrSinId(cicloForm, "modalidadEstudio");
        Assert.isNotNull(cicloForm.getModalidadEstudio(), "Tiene que especificar la modalidad de estudio.");

        NumeroCicloAcademicoEnum nroEnum = NumeroCicloAcademicoEnum.get(cicloDB.getNumeroCiclo());
        cicloForm.setEstadoEnum(CicloAcademicoEstadoEnum.CRE);
        cicloDB.setYear(cicloForm.getYear());
        cicloDB.setModalidadEstudio(cicloForm.getModalidadEstudio());
        cicloDB.setDescripcion(nroEnum.getDescripcion().replace("XXXX", cicloForm.getYear().toString()));
        cicloDB.setDescripcion2(nroEnum.getDescripcion2().replace("XXXX", cicloForm.getYear().toString()));
        cicloDB.setDescripcion3(nroEnum.getDescripcion3().replace("XXXX", cicloForm.getYear().toString()));
        cicloDB.setCodigo(nroEnum.getCodigo().replace("XXXX", cicloForm.getYear().toString()));
        cicloDB.setFechaEntregaActas(cicloForm.getFechaEntregaActas());
        cicloAcademicoDAO.update(cicloDB);
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
    public List<MargenYear> allMargenesByYearModalidad(Integer yearBase, ModalidadEstudio modalidad) {
        Integer yearActual = new DateTime().getYear();
        List<MargenYear> margenes = new ArrayList();
        for (int year = yearBase - 2; year < yearBase + 3; year++) {
            MargenYear margen = new MargenYear(year, year == yearActual);
            List<CicloAcademico> ciclos = cicloAcademicoDAO.allByYearModalidadEnum(year, modalidad.getCodigoEnum());
            margen.setConDatos(!ciclos.isEmpty());
            margenes.add(margen);
        }

        return margenes;
    }

    @Override
    public ModalidadEstudio findModalidadEstudio(ModalidadEstudio modalidadEstudio) {
        return modalidadEstudioDAO.find(modalidadEstudio.getId());
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
