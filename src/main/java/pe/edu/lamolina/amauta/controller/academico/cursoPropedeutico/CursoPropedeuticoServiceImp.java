package pe.edu.lamolina.amauta.controller.academico.cursoPropedeutico;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoPropedeuticoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.aporte.AporteCicloDAO;
import pe.edu.lamolina.amauta.dao.aporte.AporteDAO;
import pe.edu.lamolina.amauta.dao.aporte.ResumenAporteAlumnoDAO;
import pe.edu.lamolina.amauta.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.amauta.dao.finanza.DeudaAlumnoDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCursoPropedeutico;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.AporteCiclo;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.enums.AportesEnum;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoAporteEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoDeudaEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
@Transactional(readOnly = true)
public class CursoPropedeuticoServiceImp implements CursoPropedeuticoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AlumnoCursoPropedeuticoDAO alumnoCursoPropedeuticoDAO;

    @Autowired
    ResumenAporteAlumnoDAO resumenAporteAlumnoDAO;

    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;

    @Autowired
    AporteCicloDAO aporteCicloDAO;

    @Autowired
    AporteDAO aporteDAO;

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    AcreenciaDAO acreenciaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Override
    public List<MatriculaResumen> findMatriculaResumen(String nombre, CicloAcademico cicloAcademico) {

        return matriculaResumenDAO.allByNombreAndCiclo(nombre, cicloAcademico);
    }

    @Override
    public List<Seccion> findSeccion(String nombre, CicloAcademico cicloAcademico) {
        return seccionDAO.allByNombreAndCiclo(nombre, cicloAcademico);
    }

    @Override
    @Transactional
    public void save(AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, CicloAcademico cicloAcademico, Usuario usuario) {

        Seccion seccion = alumnoCursoPropedeuticoBean.getSeccion();
        BigDecimal precio = alumnoCursoPropedeuticoBean.getPrecio();
        TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.CPRO);

        for (MatriculaResumen matriculaResumen : alumnoCursoPropedeuticoBean.getMatriculaResumens()) {

            Alumno alumno = matriculaResumen.getAlumno();

            AlumnoCursoPropedeutico alumnoCursoPropedeutico = new AlumnoCursoPropedeutico();
            alumnoCursoPropedeutico.setEstado(EstadoEnum.ACT.name());
            alumnoCursoPropedeutico.setMatriculaResumen(matriculaResumen);
            alumnoCursoPropedeutico.setSeccion(seccion);
            alumnoCursoPropedeutico.setFechaRegistro(new Date());
            alumnoCursoPropedeutico.setPrecio(precio);
            alumnoCursoPropedeutico.setUserRegistro(usuario);
            alumnoCursoPropedeuticoDAO.save(alumnoCursoPropedeutico);

            ResumenAporteAlumno resumenAporteAlumno = resumenAporteAlumnoDAO.findByMatriculaResumen(matriculaResumen);
            if (resumenAporteAlumno == null) {
                resumenAporteAlumno = new ResumenAporteAlumno(matriculaResumen, usuario);
                resumenAporteAlumno.setMontoInicial(precio);
                resumenAporteAlumno.setMontoPendiente(precio);
                resumenAporteAlumno.setMontoTotal(precio);
                resumenAporteAlumnoDAO.save(resumenAporteAlumno);
            } else {
                resumenAporteAlumno.setMontoInicial(resumenAporteAlumno.getMontoInicial().add(precio));
                resumenAporteAlumno.setMontoPendiente(resumenAporteAlumno.getMontoPendiente().add(precio));
                resumenAporteAlumno.setMontoTotal(resumenAporteAlumno.getMontoTotal().add(precio));
                resumenAporteAlumnoDAO.update(resumenAporteAlumno);
            }
            Aporte aporte = aporteDAO.findByCode(AportesEnum.A53);
            AporteCiclo aporteCiclo = aporteCicloDAO.findByCicloAcademicoAporte(cicloAcademico, aporte);

            Date fechaVencimiento = new DateTime().plusDays(5).toDate();

            DeudaAlumno deudaAlumno = new DeudaAlumno();

            AporteAlumnoCiclo aporteAlumnoCiclo = new AporteAlumnoCiclo(aporteCiclo, resumenAporteAlumno, deudaAlumno);

            ObjectNode detalleJson = createDetalleJson(aporteAlumnoCiclo);
            deudaAlumno.setConcepto("Deuda Académica");
            deudaAlumno.setNumeroCuota(1);
            deudaAlumno.setAlumno(alumno);
            deudaAlumno.setCuentaBancaria(aporteCiclo.getCuentaBancaria());
            deudaAlumno.setEstadoEnum(DeudaEstadoEnum.DEU);
            deudaAlumno.setMonto(precio);
            deudaAlumno.setTipoDeudaEnum(TipoDeudaEnum.APO);
            deudaAlumno.setDetalleJson(detalleJson.toString());
            deudaAlumno.setUserRegistro(usuario);
            deudaAlumno.setFechaRegistro(new Date());
            deudaAlumno.setAbono(BigDecimal.ZERO);
            deudaAlumno.setFechaVencimiento(fechaVencimiento);
            deudaAlumno.setFechaEmision(new Date());
            deudaAlumnoDAO.save(deudaAlumno);
            //logger.info("\tSe genero deuda-alumno con un monto de {}", monto);

            Acreencia acreencia = new Acreencia();

            acreencia.setDescripcion("Deuda Académica");
            acreencia.setOficina(new Oficina(OficinaEnum.OBUAE.getId()));
            acreencia.setTablaEnum(NombreTablasEnum.FIN_DEUDA_ALUMNO);
            acreencia.setInstanciaTabla(deudaAlumno.getId());
            acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
            acreencia.setMonto(precio);
            acreencia.setAbono(BigDecimal.ZERO);
            acreencia.setPersona(alumno.getPersona());
            acreencia.setCuentaBancaria(aporteCiclo.getCuentaBancaria());
            acreencia.setFechaDocumento(new Date());
            acreencia.setUsuarioRegistro(usuario);
            acreencia.setFechaVencimiento(fechaVencimiento);
            acreencia.setFechaRegistro(new Date());
            acreenciaDAO.save(acreencia);

            aporteAlumnoCiclo.setFechaVencimiento(fechaVencimiento);
            aporteAlumnoCiclo.setSaldo(precio);
            aporteAlumnoCiclo.setMonto(precio);
            aporteAlumnoCicloDAO.save(aporteAlumnoCiclo);

            Curso curso = seccion.getGrupoSeccion().getCurso();

            AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculaDAO.findByAlumnoCurso(alumno, curso);
            if (alumnoCursoCurricula == null) {

                alumnoCursoCurricula = new AlumnoCursoCurricula();
                alumnoCursoCurricula.setAlumno(alumno);
                alumnoCursoCurricula.setCreditos(curso.getCreditos());
                alumnoCursoCurricula.setCurso(curso);
                alumnoCursoCurricula.setEsSimultaneo(Boolean.FALSE);
                alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.HAB);
                alumnoCursoCurricula.setEstadoRegistroEnum(EstadoEnum.ACT);
                alumnoCursoCurricula.setNumeroCiclo(10);
                alumnoCursoCurricula.setTipoCursoCurricula(tipoCursoCurricula);
                alumnoCursoCurricula.setUserHabilitaCurso(usuario);
                alumnoCursoCurricula.setFechaHabilitaCurso(new Date());
                alumnoCursoCurricula.setValidado(true);
                alumnoCursoCurricula.setVecesCursado(0);
                alumnoCursoCurriculaDAO.save(alumnoCursoCurricula);
            }

        }

    }

    @Override
    @Transactional
    public void update(AlumnoCursoPropedeuticoBean alumnoCursoPropedeuticoBean, CicloAcademico cicloAcademico, Usuario usuario) {

        List<AlumnoCursoPropedeutico> alumnoCursoPropedeuticosDB = alumnoCursoPropedeuticoDAO.allBySeccion(alumnoCursoPropedeuticoBean.getSeccion());

        ListsInspector inspector = TypesUtil.analizeLists(alumnoCursoPropedeuticosDB, alumnoCursoPropedeuticosDB, "id");
        List<AlumnoCursoPropedeutico> acpDead = inspector.getDeadList();
        List<AlumnoCursoPropedeutico> acpNew = inspector.getNewList();

        for (AlumnoCursoPropedeutico alumnoCursoPropedeutico : acpDead) {
            alumnoCursoPropedeutico.setEstado(EstadoEnum.INA.name());
            alumnoCursoPropedeuticoDAO.update(alumnoCursoPropedeutico);
        }
        for (AlumnoCursoPropedeutico alumnoCursoPropedeuticoForm : acpNew) {
            AlumnoCursoPropedeutico alumnoCursoPropedeutico = new AlumnoCursoPropedeutico();
            alumnoCursoPropedeutico.setEstado(EstadoEnum.ACT.name());
            alumnoCursoPropedeutico.setMatriculaResumen(alumnoCursoPropedeuticoForm.getMatriculaResumen());
            alumnoCursoPropedeutico.setSeccion(alumnoCursoPropedeuticoBean.getSeccion());
            alumnoCursoPropedeutico.setFechaRegistro(new Date());
            alumnoCursoPropedeutico.setUserRegistro(usuario);
            alumnoCursoPropedeuticoDAO.save(alumnoCursoPropedeutico);
        }

    }

    @Override
    public List<AlumnoCursoPropedeutico> list(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return alumnoCursoPropedeuticoDAO.allBySeccionDynatable(filter, cicloAcademico);
    }

    private ObjectNode createDetalleJson(AporteAlumnoCiclo aporteAlumnoCiclo) {
        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);

        ObjectNode node = JsonHelper.createJson(aporteAlumnoCiclo, JsonNodeFactory.instance, new String[]{
            "*",
            "aporteCiclo.*",
            "aporteCiclo.aporte.*",
            "resumenAporteAlumno.*",
            "resumenAporteAlumno.matriculaResumen.*"
        });

        json.set("data", node);
        return json;
    }

    @Override
    @Transactional
    public void eliminarDeudaAlumnoCursoPropedeutico(Long idAlumnoCursoPropedeutico, CicloAcademico cicloAcademico, Usuario usuario) {

        AlumnoCursoPropedeutico alumnoCursoPropedeutico = alumnoCursoPropedeuticoDAO.find(idAlumnoCursoPropedeutico);

        Alumno alumno = alumnoCursoPropedeutico.getMatriculaResumen().getAlumno();

        CicloAcademico ciclo = cicloAcademicoDAO.findActivoByModalidadEstudio(alumno.getModalidadEstudio().getCodigoEnum());
        logger.info("alumno anular {} en el ciclo {}", alumno.getId(), ciclo.getId());

        AporteCiclo aporteCiclo = aporteCicloDAO.findByCodigoCiclo(AportesEnum.A53, ciclo);
        Assert.isNotNull(aporteCiclo, "No tiene el aporte");
        logger.info("aporteCiclo anular {}", aporteCiclo.getId());

        logger.info("exonerar aporte {} al alumno {}", aporteCiclo.getAporte().getNombre(), alumno.getId());
        ResumenAporteAlumno resumen = resumenAporteAlumnoDAO.findByAlumnoCicloAcademico(alumno, ciclo);
        Assert.isNotNull(resumen, "Aun no se ha generado los aportes del alumno");

        logger.debug("buscando aporte-ciclo {} en resumen {}", aporteCiclo.getId(), resumen.getId());
        AporteAlumnoCiclo aporteAlu = aporteAlumnoCicloDAO.findByAporteCicloResumen(aporteCiclo, resumen);
        if (aporteAlu == null) {
            logger.info("No existe el aporte");
            throw new PhobosException("No existe el aporte");
            //return;
        }
        if (aporteAlu.getEstadoEnum() != EstadoAporteEnum.DEBE) {
            logger.info("Aporte exonerado o anulado");
            return;
        }

        DeudaAlumno deudaAlumno = null;
        CuentaBancaria cta = aporteCiclo.getCuentaBancaria();
        List<DeudaAlumno> deudas = deudaAlumnoDAO.allByAlumnoCiclo(alumno, ciclo);
        for (DeudaAlumno deuda : deudas) {
            if (deuda.getEstadoEnum() == DeudaEstadoEnum.DEU
                    && deuda.getCuentaBancaria().getId() == cta.getId().longValue()
                    && deuda.getNumeroCuota() == 1) {
                deudaAlumno = deuda;
                logger.info("\talumno {} ya tiene deuda pendiente de {}", alumno.getId(), deudaAlumno.getMonto());
                break;
            }
        }

        if (deudaAlumno == null) {
            return;
        }

        BigDecimal precio = alumnoCursoPropedeutico.getPrecio();

        resumen.setMontoTotal(resumen.getMontoTotal().subtract(precio));
        resumen.setMontoInicial(resumen.getMontoInicial().subtract(precio));
        resumen.setMontoPendiente(resumen.getMontoPendiente().subtract(precio));
        resumenAporteAlumnoDAO.update(resumen);

        BigDecimal monto = deudaAlumno.getMonto();
        
        monto = monto.subtract(precio);
        
        logger.info("\tmonto de deuda del alumno {} se va a modificar de {} a {}", alumno.getId(), deudaAlumno.getMonto(), monto);

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            deudaAlumno.setEstadoEnum(DeudaEstadoEnum.ANU);
        }

        Date fechaHoraRegistro = new Date();
        Acreencia acreenciaOld = acreenciaDAO.findDebeByDeudaAlumno(deudaAlumno);
        acreenciaOld.setEstadoEnum(DeudaEstadoEnum.ANU);
        acreenciaOld.setFechaAnulacion(fechaHoraRegistro);
        acreenciaDAO.update(acreenciaOld);
        logger.info("\tanulandao acreenia {} del alumno {}", acreenciaOld.getId(), alumno.getId());

        if (monto.compareTo(BigDecimal.ZERO) > 0) {

            Acreencia acreencia = new Acreencia();
            acreencia.setDescripcion(acreenciaOld.getDescripcion());
            acreencia.setOficina(acreenciaOld.getOficina());
            acreencia.setTablaEnum(acreenciaOld.getTablaEnum());
            acreencia.setInstanciaTabla(acreenciaOld.getInstanciaTabla());
            acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
            acreencia.setMonto(monto);
            acreencia.setAbono(BigDecimal.ZERO);
            acreencia.setPersona(alumno.getPersona());
            acreencia.setCuentaBancaria(cta);
            acreencia.setFechaDocumento(fechaHoraRegistro);
            acreencia.setFechaVencimiento(acreenciaOld.getFechaVencimiento());
            acreencia.setFechaRegistro(fechaHoraRegistro);
            acreenciaDAO.save(acreencia);
            deudaAlumno.setAcreencia(acreencia);
            logger.info("\tcreando nueva acreencia {} con {} del alumno {}", acreencia.getId(), acreencia.getMonto(), alumno.getId());
        }

        deudaAlumno.setMonto(monto);
        deudaAlumnoDAO.update(deudaAlumno);

        aporteAlu.setEstadoEnum(EstadoAporteEnum.ANU);
        aporteAlu.setEstadoRegistroEnum(EstadoEnum.ANU);
        aporteAlu.setUsuarioAnula(usuario);
        aporteAlu.setFechaAnula(fechaHoraRegistro);
        logger.info("************************************ apo alm ciclo " + aporteAlu.getId());
        aporteAlumnoCicloDAO.updateColumns(aporteAlu, "estado", "estadoRegistro");

    }

}
