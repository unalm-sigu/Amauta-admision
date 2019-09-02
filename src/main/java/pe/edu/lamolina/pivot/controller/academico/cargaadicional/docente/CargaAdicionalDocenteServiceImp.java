package pe.edu.lamolina.pivot.controller.academico.cargaadicional.docente;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteCiclo;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Factor1CargaAdicional;
import pe.edu.lamolina.model.academico.Factor2CargaAdicional;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.ConfiguraCargaAdicionalEstadoEnum;
import pe.edu.lamolina.model.enums.DocumentoPdfEnum; 
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.dao.academico.ConfiguraCargaAdicionalDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.Factor1CargaAdicionalDAO;
import pe.edu.lamolina.pivot.dao.academico.Factor2CargaAdicionalDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.PdfContent;
import pe.edu.lamolina.pivot.zelper.pdf.PdfGenerator;
import pe.edu.lamolina.pivot.zelper.pdf.TipoPdfEnum;

@Service
@Transactional(readOnly = true)
public class CargaAdicionalDocenteServiceImp implements CargaAdicionalDocenteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DocenteCicloDAO docenteCicloDAO;

    @Autowired
    ConfiguraCargaAdicionalDAO configuraCargaAdicionalDAO;

    @Autowired
    Factor1CargaAdicionalDAO factor1CargaAdicionalDAO;

    @Autowired
    Factor2CargaAdicionalDAO factor2CargaAdicionalDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    PdfGenerator pdfGenerator;

    @Override
    public List<DocenteCiclo> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return docenteCicloDAO.allByDynatableCicloAcademico(filter, cicloAcademico);
    }

    @Override
    public ConfiguraCargaAdicional findConfiguracionByCicloAcademico(CicloAcademico cicloAcademico) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf == null) {
            return new ConfiguraCargaAdicional();
        }
        return conf;
    }

    @Override
    @Transactional
    public void eliminarCarga(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf.getEstadoEnum() != ConfiguraCargaAdicionalEstadoEnum.CARGA) {
            return;
        }
        conf.setEstado(ConfiguraCargaAdicionalEstadoEnum.CRE);
        docenteCicloDAO.deshacerCarga(cicloAcademico);
        configuraCargaAdicionalDAO.update(conf);
    }

    @Override
    @Transactional
    public void eliminarMontos(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf.getEstadoEnum() != ConfiguraCargaAdicionalEstadoEnum.MONTO) {
            return;
        }
        conf.setEstado(ConfiguraCargaAdicionalEstadoEnum.CARGA);
        docenteCicloDAO.deshacerMontos(cicloAcademico);
        configuraCargaAdicionalDAO.update(conf);
    }

    private BigDecimal getCreditos(Seccion seccion) {
        switch (seccion.getTipoSeccionEnum()) {
            case TCUR:
            case TEO:
                return new BigDecimal(seccion.getGrupoSeccion().getCurso().getHorasTeoria());
            case PCUR:
            case PRA:
                return new BigDecimal(seccion.getGrupoSeccion().getCurso().getHorasPractica() / 2);
            default:
                throw new AssertionError();
        }
    }

    @Override
    @Transactional
    public void generarCarga(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf.getEstadoEnum() != ConfiguraCargaAdicionalEstadoEnum.CRE) {
            return;
        }

        List<Factor1CargaAdicional> factores1 = factor1CargaAdicionalDAO.allByCicloAcademico(cicloAcademico);
        Map<String, Factor1CargaAdicional> mapFactor1 = factores1.stream().collect(Collectors.toMap(x -> String.format("%s-%s", x.getSituacionDocente().getCodigo(), x.getCategoriaDocente().getCodigo()), x -> x));

        List<Factor2CargaAdicional> factores2 = factor2CargaAdicionalDAO.allByCicloAcademico(cicloAcademico);
        checkFactor2(factores2);

        NavigableMap<Integer, Integer> map = new TreeMap<Integer, Integer>();

        RangeMap<BigDecimal, Factor2CargaAdicional> mapFactor2 = TreeRangeMap.create();
        for (Factor2CargaAdicional factor : factores2) {
            if (factor.getCantidadFin() != null) {
                mapFactor2.put(Range.closedOpen(new BigDecimal(factor.getCantidadInicio()), new BigDecimal(factor.getCantidadFin())), factor);
            } else {
                mapFactor2.put(Range.closedOpen(new BigDecimal(factor.getCantidadInicio()), new BigDecimal(999)), factor);
            }
        }

        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);

        List<DocenteSeccion> docenteSeccion = docenteSeccionDAO.allByModalidadEstudioCicloAcademico(modalidadEstudio, cicloAcademico);

        Map<Docente, List<DocenteSeccion>> mapDocenteSeccion = docenteSeccion.stream().collect(Collectors.groupingBy(DocenteSeccion::getDocente));

        BigDecimal cien = new BigDecimal(100L);

        for (Map.Entry<Docente, List<DocenteSeccion>> entry : mapDocenteSeccion.entrySet()) {
            Docente docente = entry.getKey();
            List<DocenteSeccion> docenteSeccions = entry.getValue();

            BigDecimal cantidadCreditos = BigDecimal.ZERO;
            BigDecimal promedioAlumnos = BigDecimal.ZERO;
            BigDecimal sumatoriaPorcentajeCarga = BigDecimal.ZERO;

            for (DocenteSeccion docenteSeccion1 : docenteSeccions) {
                BigDecimal creditos = getCreditos(docenteSeccion1.getSeccion());

                BigDecimal porcentajeCarga
                        = docenteSeccion1.getPorcentajeCarga() != null
                        ? docenteSeccion1.getPorcentajeCarga()
                        : BigDecimal.ZERO;

                BigDecimal matriculados
                        = docenteSeccion1.getSeccion().getMatriculados() != null
                        ? new BigDecimal(docenteSeccion1.getSeccion().getMatriculados())
                        : BigDecimal.ZERO;

                cantidadCreditos = cantidadCreditos.add(porcentajeCarga.multiply(creditos).divide(cien, 4, RoundingMode.HALF_UP));
                promedioAlumnos = promedioAlumnos.add(porcentajeCarga.multiply(matriculados));
                sumatoriaPorcentajeCarga = sumatoriaPorcentajeCarga.add(porcentajeCarga);
            }

            if (sumatoriaPorcentajeCarga.compareTo(BigDecimal.ZERO) > 0) {
                promedioAlumnos = promedioAlumnos.divide(sumatoriaPorcentajeCarga, 4, RoundingMode.HALF_UP);
            } else {
                promedioAlumnos = BigDecimal.ZERO;
            }

            DocenteCiclo profeCiclo = new DocenteCiclo();

            profeCiclo.setDocente(docente);
            profeCiclo.setCicloAcademico(cicloAcademico);
            profeCiclo.setPromedioAlumnos(promedioAlumnos);
            profeCiclo.setCreditosTotal(cantidadCreditos);
            profeCiclo.setModalidadEstudio(modalidadEstudio);
            profeCiclo.setCategoriaDocente(docente.getCategoria());
            profeCiclo.setSituacionDocente(docente.getSituacion());

            if (docente.getSituacion() == null || docente.getCategoria() == null) {
                profeCiclo.setFactor1(BigDecimal.ZERO);
                profeCiclo.setCreditosExceso(BigDecimal.ZERO);
            } else {
                Factor1CargaAdicional factor = mapFactor1.get(String.format("%s-%s", docente.getSituacion().getCodigo(), docente.getCategoria().getCodigo()));
                if (factor == null) {
                    profeCiclo.setFactor1(BigDecimal.ZERO);
                    profeCiclo.setCreditosExceso(BigDecimal.ZERO);
                } else {
                    profeCiclo.setFactor1(factor.getFactor());
                    profeCiclo.setCreditosExceso(profeCiclo.getCreditosTotal().subtract(new BigDecimal(factor.getCreditosMinimo())));
                    if (profeCiclo.getCreditosExceso().compareTo(BigDecimal.ZERO) < 0) {
                        profeCiclo.setCreditosExceso(BigDecimal.ZERO);
                    }
                }
            }

            if (promedioAlumnos.equals(BigDecimal.ZERO)) {
                profeCiclo.setFactor2(BigDecimal.ZERO);
            } else {
                Factor2CargaAdicional factor = mapFactor2.get(profeCiclo.getPromedioAlumnos());
                if (factor != null) {
                    profeCiclo.setFactor2(factor.getFactor());
                } else {
                    profeCiclo.setFactor2(BigDecimal.ZERO);
                }
            }

            profeCiclo.setFechaRegistro(new Date());
            profeCiclo.setUserRegistro(ds.getUsuario());
            docenteCicloDAO.save(profeCiclo);

        }

        conf.setUserCalculaCarga(ds.getUsuario());
        conf.setFechaCalculaCarga(new Date());
        conf.setEstado(ConfiguraCargaAdicionalEstadoEnum.CARGA);
        configuraCargaAdicionalDAO.update(conf);
    }

    private void checkFactor2(List<Factor2CargaAdicional> factores) {
        factores.sort(Comparator.comparing(Factor2CargaAdicional::getCantidadInicio));
        for (int i = 0; i < factores.size(); i++) {
            Factor2CargaAdicional factor = factores.get(i);
            if (i == 0) {
                Assert.isTrue(factor.getCantidadInicio() == 0, "El factor 2 no está configurado");
            } else if (i == factores.size() - 1) {
                Factor2CargaAdicional factorAnterior = factores.get(i - 1);
                Assert.isNull(factor.getCantidadFin(), "El factor 2 no está configurado");
                Assert.isTrue(factor.getCantidadInicio() == factorAnterior.getCantidadFin(), "El factor 2 no está configurado");
            } else {
                Factor2CargaAdicional factorAnterior = factores.get(i - 1);
                Assert.isTrue(factor.getCantidadInicio() != 1, "El factor 2 no está configurado");
                Assert.isTrue(factor.getCantidadInicio() == factorAnterior.getCantidadFin(), "El factor 2 no está configurado");
                Assert.isNotNull(factor.getCantidadFin(), "Los intervalos del factor 2 se cruzan");
            }
        }
    }

    @Override
    @Transactional
    public void generarMontos(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf.getEstadoEnum() != ConfiguraCargaAdicionalEstadoEnum.CARGA) {
            return;
        }
        docenteCicloDAO.generarMontos(cicloAcademico, conf.getRca());

        conf.setUserCalculaMontos(ds.getUsuario());
        conf.setFechaCalculaMontos(new Date());
        conf.setEstado(ConfiguraCargaAdicionalEstadoEnum.MONTO);
        configuraCargaAdicionalDAO.update(conf);
    }

    @Override
    @Transactional
    public void saveConfiguracion(ConfiguraCargaAdicional configuraCargaAdicional, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional confBD = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);

        if (confBD == null) {
            configuraCargaAdicional.setEstado(ConfiguraCargaAdicionalEstadoEnum.CRE);
            configuraCargaAdicional.setCicloAcademico(ds.getCicloAcademico());

            configuraCargaAdicional.setUserRegistro(ds.getUsuario());
            configuraCargaAdicional.setFechaRegistro(new Date());

            configuraCargaAdicionalDAO.save(configuraCargaAdicional);
        } else {
            Assert.isTrue(confBD.getEstadoEnum() == ConfiguraCargaAdicionalEstadoEnum.CRE, "La configuración ya ha sido aceptada");

            confBD.setMinimoAlumnos(configuraCargaAdicional.getMinimoAlumnos());
            confBD.setRca(configuraCargaAdicional.getRca());

            configuraCargaAdicionalDAO.update(confBD);
        }
    }

    @Override
    public String reporte(CicloAcademico cicloAcademico) {
        ConfiguraCargaAdicional confBD = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        Assert.isTrue(confBD.getEstadoEnum() == ConfiguraCargaAdicionalEstadoEnum.MONTO || confBD.getEstadoEnum() == ConfiguraCargaAdicionalEstadoEnum.CERR, "Los montos aun no han sido generados");

        SimpleDateFormat formateador = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

        List<DocenteCiclo> docenteCiclos = docenteCicloDAO.allActivoByCicloAcademico(cicloAcademico);
        Map<Facultad, List<DocenteCiclo>> mapFacultades = docenteCiclos.stream().collect(Collectors.groupingBy(x -> x.getDocente().getDepartamentoAcademico().getFacultad()));
        Map<Facultad, BigDecimal> mapTotales = new HashMap<>();
        for (Map.Entry<Facultad, List<DocenteCiclo>> entry : mapFacultades.entrySet()) {
            Facultad key = entry.getKey();
            BigDecimal total = BigDecimal.ZERO;
            for (DocenteCiclo docenteCiclo : entry.getValue()) {
                total = total.add(docenteCiclo.getMonto());
            }
            mapTotales.put(key, total);
        }

        Context ctx = new Context();
        ctx.setVariable("mapFacultades", mapFacultades);
        ctx.setVariable("mapTotales", mapTotales);
        ctx.setVariable("cicloAcademico", cicloAcademico);
        ctx.setVariable("fecha", String.format("La Molina, %s", formateador.format(new Date())));

        PdfContent pdfContent = new PdfContent();
        pdfContent.setContext(ctx);
        pdfContent.setTipoPdfEnum(TipoPdfEnum.SUBVENCION_CARGA_ADICIONAL);

        String src = pdfGenerator.generateDocument(pdfContent, "tmp");
        return src;
    }

    @Override
    @Transactional
    public void cerrar(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        ConfiguraCargaAdicional conf = configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
        if (conf.getEstadoEnum() != ConfiguraCargaAdicionalEstadoEnum.MONTO) {
            return;
        }
        conf.setUserCierre(ds.getUsuario());
        conf.setFechaCierre(new Date());
        conf.setEstado(ConfiguraCargaAdicionalEstadoEnum.CERR);
        configuraCargaAdicionalDAO.update(conf);
    }

}
