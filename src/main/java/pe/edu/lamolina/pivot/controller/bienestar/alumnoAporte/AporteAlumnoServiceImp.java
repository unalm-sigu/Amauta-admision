package pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.AporteCiclo;
import pe.edu.lamolina.model.aporte.AporteSemestral;
import pe.edu.lamolina.model.aporte.GeneracionAportes;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import pe.edu.lamolina.model.enums.AportesEnum;
import pe.edu.lamolina.model.enums.CuentaBancariaEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoAporteEnum;
import pe.edu.lamolina.model.enums.GeneracionAportesEstadoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo.ResponseRestService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteCicloDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteSemestralDAO;
import pe.edu.lamolina.pivot.dao.aporte.GeneracionAportesDAO;
import pe.edu.lamolina.pivot.dao.aporte.ResumenAporteAlumnoDAO;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.pivot.dao.finanza.DeudaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AporteAlumnoServiceImp implements AporteAlumnoService {

    @Autowired
    GeneracionAportesDAO generacionAportesDAO;

    @Autowired
    ResumenAporteAlumnoDAO resumenAporteAlumnoDAO;

    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    AporteCicloDAO aporteCicloDAO;

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    AporteSemestralDAO aporteSemestralDAO;

    @Autowired
    AcreenciaDAO acreenciaDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    ResponseRestService responseRestService;

    @Override
    public void generarAportes(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds) {

        GeneracionAportes generador = generacionAportesDAO.findByCicloAcademico(ciclo);
        if (generador == null) {
            return;
        }
        if (!Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN).contains(generador.getEstadoEnum())) {
            return;
        }

        JsonResponse jsonResponse = responseRestService.generarAporte(alumno, ds);

        Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al eliminar la matrícula. Comuniquese con mesa de ayuda.");

//
//        MatriculaResumen matriResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
//        ResumenAporteAlumno aportante = resumenAporteAlumnoDAO.findByAlumnoCicloAcademico(alumno, ciclo);
//        if (aportante == null) {
//            aportante = new ResumenAporteAlumno();
//        }
//
//        aportante.setMatriculaResumen(matriResumen);
//        aportante.setMontoAFavor(BigDecimal.ZERO);
//        aportante.setMontoCancelado(BigDecimal.ZERO);
//        aportante.setMontoExonerado(BigDecimal.ZERO);
//        aportante.setMontoFraccionado(BigDecimal.ZERO);
//        aportante.setMontoInicial(BigDecimal.ZERO);
//        aportante.setMontoPendiente(BigDecimal.ZERO);
//        aportante.setMontoTotal(BigDecimal.ZERO);
//        aportante.setFechaRegistro(new Date());
//        aportante.setUserRegistro(ds.getUsuario());
//
//        if (aportante.getId() == null) {
//            resumenAporteAlumnoDAO.save(aportante);
//        }
//
//        if (Arrays.asList(GeneracionAportesEstadoEnum.BOL, GeneracionAportesEstadoEnum.GEN).contains(generador.getEstadoEnum())) {
//            List<AporteCiclo> aportesCiclo = aporteCicloDAO.allByCicloAcademico(ciclo);
//            for (AporteCiclo aporteCiclo : aportesCiclo) {
//
//                if (aporteCiclo.getMontoVariable()) {
//                    createAporteVariable(aporteCiclo, aportante);
//                } else if (!aporteCiclo.getPersonalizado() && !aporteCiclo.getMontoVariable()) {
//                    createAporteFijo(aporteCiclo, aportante);
//                } else if (aporteCiclo.getPersonalizado() && !aporteCiclo.getMontoVariable()) {
//                    createAporteFijoPersonalizado(aporteCiclo, aportante);
//                }
//                aporteCicloDAO.update(aporteCiclo);
//            }
//        }
//
//        resumenAporteAlumnoDAO.update(aportante);
//        if (generador.getEstadoEnum() != GeneracionAportesEstadoEnum.BOL) {
//            return;
//        }
//
//        Alumno alumnoBD = alumnoDAO.find(alumno.getId());
//        List<AporteAlumnoCiclo> deudasAlu = aporteAlumnoCicloDAO.allByAlumnoCiclo(alumno, ciclo);
//        Map<Long, List<AporteAlumnoCiclo>> mapDeudasByCta = TypesUtil.convertListToMapList("aporteCiclo.cuentaBancaria.id", deudasAlu);
//        Map<Long, CuentaBancaria> mapCtaBanco = TypesUtil.convertListToMap("aporteCiclo.cuentaBancaria.id", "aporteCiclo.cuentaBancaria", deudasAlu);
//        List<CuentaBancaria> ctasBanco = new ArrayList(mapCtaBanco.values());
//        for (CuentaBancaria ctaBanco : ctasBanco) {
//            List<AporteAlumnoCiclo> deudasCta = mapDeudasByCta.get(ctaBanco.getId());
//
//            if (deudasCta == null) {
//                continue;
//            }
//
//            BigDecimal monto = BigDecimal.ZERO;
//            for (AporteAlumnoCiclo aporteAlumnoCiclo : deudasCta) {
//                monto = monto.add(aporteAlumnoCiclo.getMonto());
//            }
//
//            DeudaAlumno deudaAlumno = new DeudaAlumno();
//            if (ctaBanco.getCodigo().equals(CuentaBancariaEnum.MAT_UNALM.getCodigoServ())) {//credipago matricula
//                deudaAlumno.setConcepto("Deuda Académica");
//            } else if (ctaBanco.getCodigo().equals(CuentaBancariaEnum.MAT_FDA.getCodigoServ())) {//credipago bienestar
//                deudaAlumno.setConcepto("Deuda Bienestar");
//            }
//
//            ObjectNode detalleJson = createDetalleJson(deudasAlu);
//            deudaAlumno.setAlumno(alumnoBD);
//            deudaAlumno.setCuentaBancaria(ctaBanco);
//            deudaAlumno.setEstadoEnum(DeudaEstadoEnum.DEU);
//            deudaAlumno.setMonto(monto);
//            deudaAlumno.setDetalleJson(detalleJson.toString());
//            deudaAlumno.setUserRegistro(ds.getUsuario());
//            deudaAlumno.setFechaRegistro(new Date());
//            deudaAlumno.setAbono(BigDecimal.ZERO);
//            deudaAlumnoDAO.save(deudaAlumno);
//
//            for (AporteAlumnoCiclo aporteAlu : deudasCta) {
//                aporteAlu.setDeudaAlumno(deudaAlumno);
//                aporteAlumnoCicloDAO.update(aporteAlu);
//            }
//
//            Acreencia acreencia = new Acreencia();
//            if (ctaBanco.getCodigo().equals(CuentaBancariaEnum.MAT_UNALM.getCodigoServ())) {//credipago matricula
//                acreencia.setDescripcion("Deuda Académica");
//            } else if (ctaBanco.getCodigo().equals(CuentaBancariaEnum.MAT_FDA.getCodigoServ())) {//credipago bienestar
//                acreencia.setDescripcion("Deuda Bienestar");
//            }
//
//            acreencia.setOficina(new Oficina(OficinaEnum.OBUAE.getId()));
//            acreencia.setTablaEnum(NombreTablasEnum.FIN_DEUDA_ALUMNO);
//            acreencia.setInstanciaTabla(deudaAlumno.getId());
//            acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
//            acreencia.setMonto(monto);
//            acreencia.setAbono(BigDecimal.ZERO);
//            acreencia.setPersona(alumnoBD.getPersona());
//            acreencia.setCuentaBancaria(ctaBanco);
//            acreencia.setFechaDocumento(new Date());
//            acreencia.setUsuarioRegistro(ds.getUsuario());
//            acreencia.setFechaRegistro(new Date());
//            acreenciaDAO.save(acreencia);
//        }
    }

    private void createAporteVariable(AporteCiclo aporteCiclo, ResumenAporteAlumno aportante) {
        Aporte aporte = aporteCiclo.getAporte();
        Alumno alumno = aportante.getMatriculaResumen().getAlumno();

        /* Aporte Semestral */
        if (aporte.getCodigoEnum() == AportesEnum.A01) {
            AporteSemestral apoSem = aporteSemestralDAO.findActivoByAlumno(alumno, aporte);
            Assert.isNotNull(apoSem, "El alumno no cuenta con aporte semestral");
            AporteAlumnoCiclo aac = new AporteAlumnoCiclo();
            aac.setAporteCiclo(aporteCiclo);
            aac.setEstadoEnum(EstadoAporteEnum.DEBE);
            aac.setFraccionado(BigDecimal.ZERO);
            aac.setMonto(apoSem.getCategoriaBienestar().getMonto());
            aac.setNumeroCuota(1);
            aac.setPagado(BigDecimal.ZERO);
            aac.setResumenAporteAlumno(aportante);
            aac.setSaldo(apoSem.getCategoriaBienestar().getMonto());
            aporteAlumnoCicloDAO.save(aac);

            aportante.setMontoInicial(aportante.getMontoInicial().add(aac.getMonto()));
            aportante.setMontoTotal(aportante.getMontoTotal().add(aac.getMonto()));
            aportante.setMontoPendiente(aportante.getMontoPendiente().add(aac.getMonto()));

            aporteCiclo.setAportantes(aporteCiclo.getAportantes() + 1);
        }

        /* Aporte Voluntario - Solo cachimbos */
        if (aporte.getCodigoEnum() == AportesEnum.A14
                && Arrays.asList(S_8, S_9).contains(alumno.getSituacionAcademica().getCodigoEnum())) {
            AporteSemestral apoSem = aporteSemestralDAO.findActivoByAlumno(alumno, aporte);
            AporteAlumnoCiclo aac = new AporteAlumnoCiclo();
            aac.setAporteCiclo(aporteCiclo);
            aac.setEstadoEnum(EstadoAporteEnum.DEBE);
            aac.setFraccionado(BigDecimal.ZERO);
            if (apoSem == null) {
                throw new PhobosException("Llenar aporte voluntario");
            }
            aac.setMonto(apoSem.getMonto());
            aac.setNumeroCuota(1);
            aac.setPagado(BigDecimal.ZERO);
            aac.setResumenAporteAlumno(aportante);
            aac.setSaldo(apoSem.getMonto());
            aporteAlumnoCicloDAO.save(aac);

            aportante.setMontoInicial(aportante.getMontoInicial().add(aac.getMonto()));
            aportante.setMontoTotal(aportante.getMontoTotal().add(aac.getMonto()));
            aportante.setMontoPendiente(aportante.getMontoPendiente().add(aac.getMonto()));

            aporteCiclo.setAportantes(aporteCiclo.getAportantes() + 1);
        }
    }

    private void createAporteFijo(AporteCiclo aporteCiclo, ResumenAporteAlumno aportante) {
        BigDecimal monto = aporteCiclo.getMontoFijo();
        AporteAlumnoCiclo aac = new AporteAlumnoCiclo();
        aac.setAporteCiclo(aporteCiclo);
        aac.setEstadoEnum(EstadoAporteEnum.DEBE);
        aac.setFraccionado(BigDecimal.ZERO);
        aac.setMonto(monto);
        aac.setNumeroCuota(1);
        aac.setPagado(BigDecimal.ZERO);
        aac.setResumenAporteAlumno(aportante);
        aac.setSaldo(monto);
        aporteAlumnoCicloDAO.save(aac);

        aportante.setMontoInicial(aportante.getMontoInicial().add(monto));
        aportante.setMontoTotal(aportante.getMontoTotal().add(monto));
        aportante.setMontoPendiente(aportante.getMontoPendiente().add(monto));

        aporteCiclo.setAportantes(aporteCiclo.getAportantes() + 1);
    }

    private void createAporteFijoPersonalizado(AporteCiclo aporteCiclo, ResumenAporteAlumno aportante) {
        if (!aporteCiclo.getPersonalizado()) {
            return;
        }

        Aporte aporte = aporteCiclo.getAporte();
        Alumno alumno = aportante.getMatriculaResumen().getAlumno();
        CicloAcademico ciclo = aportante.getMatriculaResumen().getCicloAcademico();

        /* Reincorporacion */
        if (aporte.getCodigoEnum() == AportesEnum.A37) {
            Reincorporacion reincorpora = reincorporacionDAO.findByAlumnoCiclo(alumno, ciclo);
            if (reincorpora != null) {
                BigDecimal monto = aporteCiclo.getMontoFijo();
                AporteAlumnoCiclo aac = new AporteAlumnoCiclo();
                aac.setAporteCiclo(aporteCiclo);
                aac.setEstadoEnum(EstadoAporteEnum.DEBE);
                aac.setFraccionado(BigDecimal.ZERO);
                aac.setMonto(monto);
                aac.setNumeroCuota(1);
                aac.setPagado(BigDecimal.ZERO);
                aac.setResumenAporteAlumno(aportante);
                aac.setSaldo(monto);
                aporteAlumnoCicloDAO.save(aac);

                aportante.setMontoInicial(aportante.getMontoInicial().add(monto));
                aportante.setMontoTotal(aportante.getMontoTotal().add(monto));
                aportante.setMontoPendiente(aportante.getMontoPendiente().add(monto));

                aporteCiclo.setAportantes(aporteCiclo.getAportantes() + 1);
            }
        }

        /* Observacion Academica */
        if (aporte.getCodigoEnum() == AportesEnum.A02
                && Arrays.asList(S_1, S_2, S_2U).contains(alumno.getSituacionAcademica().getCodigoEnum())) {

        }

        /* Viene de Suspension */
        if (aporte.getCodigoEnum() == AportesEnum.A43
                && Arrays.asList(S_3, S_3U).contains(alumno.getSituacionAcademica().getCodigoEnum())) {

        }
        /* A pesar de estar Suspendidos van a estudiar */
        if (aporte.getCodigoEnum() == AportesEnum.A43
                && Arrays.asList(S_6U, S_6, S_4, S_4U).contains(alumno.getSituacionAcademica().getCodigoEnum())) {

        }

    }

    private ObjectNode createDetalleJson(List<AporteAlumnoCiclo> deudasAlumno) {
        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (AporteAlumnoCiclo aporteAlu : deudasAlumno) {
            ObjectNode node = JsonHelper.createJson(aporteAlu, JsonNodeFactory.instance, new String[]{
                "*",
                "aporteCiclo.*",
                "aporteCiclo.aporte.*",
                "resumenAporteAlumno.*",
                "resumenAporteAlumno.matriculaResumen.*"
            });
            array.add(node);
        }
        json.set("data", array);
        return json;
    }
}
