package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.precioseccion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.albatross.zelpers.miscelanea.math.Fraxtion;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SituacionDocenteEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.PCUR;
import static pe.edu.lamolina.model.enums.TipoSeccionEnum.TCUR;
import pe.edu.lamolina.model.finanzas.PagoHoraDocente;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.finanza.PagoHoraDocenteDAO;
import pe.edu.lamolina.pivot.dao.general.TipoCarpetaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PrecioSeccionServiceImp implements PrecioSeccionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    TipoCarpetaDAO tipoCarpetaDAO;

    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    EventoAcademicoDAO eventoAcademicoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    PagoHoraDocenteDAO pagoHoraDocenteDAO;

    @Override
    public DocenteSeccion findDocenteSeccion(DocenteSeccion docenteSeccionForm) {
        DocenteSeccion docenteSeccion = docenteSeccionDAO.find(docenteSeccionForm.getId());
        Seccion seccion = docenteSeccion.getSeccion();
        seccion.setSeccion(new ArrayList());
        if (seccion.getTipoSeccionEnum() == TCUR) {
            List<Seccion> seccionesByGpoSecc = seccionDAO.allActivosByGpoSeccion(seccion.getGrupoSeccion());
            for (Seccion secc : seccionesByGpoSecc) {
                if (secc.getTipoSeccionEnum() == PCUR) {
                    seccion.getSeccion().add(secc);
                }
            }
        }
        return docenteSeccion;
    }

    @Override
    public CursoCicloAcademico findCursoCiclo(Curso curso, CicloAcademico ciclo) {
        return cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);
    }

    @Override
    public void savePrecioSeccion(Seccion seccionForm, DataSessionPivot ds) {

        Seccion seccionBD = seccionDAO.find(seccionForm);
        Curso curso = seccionBD.getGrupoSeccion().getCurso();
        CicloAcademico ciclo = seccionBD.getGrupoSeccion().getCicloAcademico();

        Assert.isTrue(ciclo.getTipoEnum() == TipoCicloEnum.NIV, "Sólo se aplica en ciclos de nivelación");

        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);

        BigDecimal total = cursoCiclo.getPrecio().add(cursoCiclo.getPrecioAdicional());

        if (total.compareTo(seccionForm.getPrecio()) == 0) {
            seccionForm.setPrecioPersonalizado(Boolean.FALSE);
            seccionForm.setUserPrecio(null);
            seccionForm.setFechaPrecio(null);
        } else {
            if (seccionBD.getPrecio().compareTo(seccionForm.getPrecio()) != 0) {
                seccionForm.setPrecioPersonalizado(Boolean.TRUE);
                seccionForm.setUserPrecio(ds.getUsuario());
                seccionForm.setFechaPrecio(new Date());
            } else {
                return;
            }
        }
        seccionDAO.updatePrecioBySeccion(seccionForm);
    }

    @Override
    @Transactional
    public void asignarHorasAdicionales(Seccion seccionForm, DataSessionPivot ds) {
        seccionDAO.updateColumns(seccionForm, "horasAdicionales");
    }

    @Override
    public List<TipoCarpeta> allTipoCarpetaByNombre(String nombre) {
        return tipoCarpetaDAO.allByNombre(nombre);
    }

    @Override
    @Transactional
    public void saveTipoCarpetaSeccion(Seccion seccionForm, DataSessionPivot ds) {
        ObjectUtil.eliminarAttrSinId(seccionForm, "tipoCarpeta");
        seccionDAO.updateColumns(seccionForm, "tipoCarpeta");
    }

    @Override
    @Transactional
    public TipoCarpeta findTipoCarpetaSeccion(Seccion seccionForm) {

        Seccion seccion = seccionDAO.find(seccionForm);

        TipoCarpeta tipoCarpeta = seccion.getTipoCarpeta();

        if (tipoCarpeta != null) {
            return tipoCarpeta;
        }

        CicloAcademico cicloAcademico = seccion.getGrupoSeccion().getCicloAcademico();
        logger.debug("cicloAcademico {}", cicloAcademico != null ? cicloAcademico.getId() : 0);

        Curso curso = seccion.getGrupoSeccion().getCurso();
        logger.debug("curso {}", curso != null ? curso.getId() : 0);

        TipoSeccionEnum tipoSeccionEnum = seccion.getTipoSeccionEnum();
        logger.debug("tipoSeccionEnum {}", tipoSeccionEnum != null ? tipoSeccionEnum.name() : "");

        CursoCicloAcademico cursoCicloAcademico = cursoCicloAcademicoDAO.findByCursoCiclo(curso, cicloAcademico);
        logger.debug("cursoCicloAcademico {}", cursoCicloAcademico != null ? cursoCicloAcademico.getId() : 0);

        if (tipoSeccionEnum == TipoSeccionEnum.PCUR || tipoSeccionEnum == TipoSeccionEnum.PRA) {

            tipoCarpeta = cursoCicloAcademico != null ? cursoCicloAcademico.getTipoCarpetaPractica() : null;
            tipoCarpeta = tipoCarpeta != null ? tipoCarpeta : curso.getTipoCarpetaPractica();

        } else if (tipoSeccionEnum == TipoSeccionEnum.TCUR || tipoSeccionEnum == TipoSeccionEnum.TEO) {

            tipoCarpeta = cursoCicloAcademico != null ? cursoCicloAcademico.getTipoCarpetaTeoria() : null;
            tipoCarpeta = tipoCarpeta != null ? tipoCarpeta : curso.getTipoCarpetaTeoria();

        }

        if (tipoCarpeta != null && seccion.getTipoCarpeta() == null) {
            // ANTES
//            seccionDAO.updateColumns(seccionForm, "tipoCarpeta");
            // INICIO AHORA CAMBIO HECHO POR DAVID PINEDA
            seccion.setTipoCarpeta(tipoCarpeta);
            seccionDAO.updateColumns(seccion, "tipoCarpeta");
            // FIN AHORA CAMBIO HECHO POR DAVID PINEDA
        }

        return tipoCarpeta;
    }

    @Override
    @Transactional
    public void asignarGrupoSeccionModular(GrupoSeccion grupoSeccionForm, DataSessionPivot ds) {

        GrupoSeccion grupoSeccion = grupoSeccionDAO.find(grupoSeccionForm.getId());

        if (!grupoSeccionForm.isTipoDictadoCheck()) {

            grupoSeccion.setFechaFinModular(null);
            grupoSeccion.setFechaInicioModular(null);
            grupoSeccion.setTipoDictado(TipoDictadoGrupoSeccionEnum.SEM.name());
            grupoSeccionDAO.update(grupoSeccion);
            this.regenerarFechas(grupoSeccion);
            return;

        }

        if (grupoSeccionForm.getFechaFinModular() == null || grupoSeccionForm.getFechaInicioModular() == null) {
            throw new PhobosException("Las fechas no son validas");
        }

        if (grupoSeccionForm.getFechaInicioModular().after(grupoSeccionForm.getFechaFinModular())) {
            throw new PhobosException("Las fechas no son validas");
        }

        grupoSeccion.setFechaFinModular(grupoSeccionForm.getFechaFinModular());
        grupoSeccion.setFechaInicioModular(grupoSeccionForm.getFechaInicioModular());
        grupoSeccion.setTipoDictado(TipoDictadoGrupoSeccionEnum.MOD.name());

        grupoSeccionDAO.update(grupoSeccion);
        this.reordenarFechas(grupoSeccion);

    }

    private void regenerarFechas(GrupoSeccion grupoSeccion) {

        CicloAcademico cicloAcademico = grupoSeccion.getCicloAcademico();
        AnexoBoletin anxSup = grupoSeccion.getAnexoBoletin().getAnexoSuperior();

        EventoCicloAcademico eventoCicloAcademico;

        if (anxSup.isAnexoCursosPostgrado()) {
            eventoCicloAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.CLASES_EPG);
        } else {
            eventoCicloAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.CLASES_PRE);
        }

        List<HorarioSeccion> horariosSeccions = horarioSeccionDAO.allByGrupoSeccion(grupoSeccion);

        for (HorarioSeccion horariosSeccion : horariosSeccions) {
            horariosSeccion.setFechaInicio(eventoCicloAcademico.getFechaInicio());
            horariosSeccion.setFechaFin(eventoCicloAcademico.getFechaFin());
            horarioSeccionDAO.update(horariosSeccion);
        }

        List<DocenteSeccion> docenteSecciones = docenteSeccionDAO.allByGrupoSeccionForUpdateFecha(grupoSeccion);

        Map<Long, List<DocenteSeccion>> docentesSeccionMap = new LinkedHashMap();

        for (DocenteSeccion docenteSeccion : docenteSecciones) {

            docenteSeccion.setFechaInicio(eventoCicloAcademico.getFechaInicio());
            docenteSeccion.setFechaFin(eventoCicloAcademico.getFechaFin());
            docenteSeccionDAO.update(docenteSeccion);

            Long key = (Long) ObjectUtil.getParentTree(docenteSeccion, "seccion.id");
            List<DocenteSeccion> docentesSecciones = docentesSeccionMap.get(key);
            if (docentesSecciones == null) {
                docentesSecciones = new ArrayList();
            }
            docentesSecciones.add(docenteSeccion);
            docentesSeccionMap.put(key, docentesSecciones);
        }

        List<Seccion> secciones = seccionDAO.allActivosByGpoSeccion(grupoSeccion);

        Map<Long, Aula> aulasMAp = TypesUtil.convertListToMap("aula.id", "aula", secciones);

        List<Aula> aulas = aulasMAp.values().stream().collect(Collectors.toList());

        List<HorarioAula> misHorarioAulas = horarioAulaDAO.allByAulasAndSecciones(aulas, secciones);

        List<HorarioAula> horarioAulas = horarioAulaDAO.allByAulasAndNotInSecciones(aulas, secciones, eventoCicloAcademico.getFechaInicio(), eventoCicloAcademico.getFechaFin());
        Map<String, HorarioAula> horarioAulasCruceMap = TypesUtil.convertListToMap("key", horarioAulas);

        for (HorarioAula horarioAula : misHorarioAulas) {

            String key = horarioAula.getKey();
            HorarioAula cruce = horarioAulasCruceMap.get(key);

            if (horarioAula.getAula().getPermiteCruce() == 1 || cruce == null) {

                horarioAula.setFechaInicio(eventoCicloAcademico.getFechaInicio());
                horarioAula.setFechaFin(eventoCicloAcademico.getFechaFin());
                horarioAulaDAO.update(horarioAula);
            }
        }
    }

    private void reordenarFechas(GrupoSeccion grupoSeccion) {

        List<HorarioSeccion> horariosSeccions = horarioSeccionDAO.allByGrupoSeccion(grupoSeccion);

        for (HorarioSeccion horariosSeccion : horariosSeccions) {
            horariosSeccion.setFechaInicio(grupoSeccion.getFechaInicioModular());
            horariosSeccion.setFechaFin(grupoSeccion.getFechaFinModular());
            horarioSeccionDAO.update(horariosSeccion);
        }

        List<DocenteSeccion> docenteSecciones = docenteSeccionDAO.allByGrupoSeccionForUpdateFecha(grupoSeccion);

        Map<Long, List<DocenteSeccion>> docentesSeccionMap = new LinkedHashMap();

        for (DocenteSeccion docenteSeccion : docenteSecciones) {

            docenteSeccion.setFechaInicio(grupoSeccion.getFechaInicioModular());
            docenteSeccion.setFechaFin(grupoSeccion.getFechaFinModular());
            docenteSeccionDAO.update(docenteSeccion);

            Long key = (Long) ObjectUtil.getParentTree(docenteSeccion, "seccion.id");
            List<DocenteSeccion> docentesSecciones = docentesSeccionMap.get(key);
            if (docentesSecciones == null) {
                docentesSecciones = new ArrayList();
            }
            docentesSecciones.add(docenteSeccion);
            docentesSeccionMap.put(key, docentesSecciones);
        }

        List<Seccion> secciones = seccionDAO.allActivosByGpoSeccion(grupoSeccion);

        Map<Long, Aula> aulasMAp = TypesUtil.convertListToMap("aula.id", "aula", secciones);

        List<Aula> aulas = aulasMAp.values().stream().collect(Collectors.toList());

        List<HorarioAula> misHorarioAulas = horarioAulaDAO.allByAulasAndSecciones(aulas, secciones);

        List<HorarioAula> horarioAulas = horarioAulaDAO.allByAulasAndNotInSecciones(aulas, secciones, grupoSeccion.getFechaInicioModular(), grupoSeccion.getFechaFinModular());

        Map<String, HorarioAula> horarioAulasCruceMap = TypesUtil.convertListToMap("key", horarioAulas);

        for (HorarioAula horarioAula : misHorarioAulas) {

            String key = horarioAula.getKey();
            HorarioAula cruce = horarioAulasCruceMap.get(key);

            if (horarioAula.getAula().getPermiteCruce() == 1 || cruce == null) {

                horarioAula.setFechaInicio(grupoSeccion.getFechaInicioModular());
                horarioAula.setFechaFin(grupoSeccion.getFechaFinModular());
                horarioAulaDAO.update(horarioAula);
            }
        }
    }

    @Override
    @Transactional
    public String generarPagoDocente(
            DocenteSeccion docenteSeccionBD,
            CursoCicloAcademico cursoCiclo,
            List<PagoHoraDocente> pagosDocenteByHora,
            CicloAcademico ciclo, DataSessionPivot ds) {

        logger.debug(" **** update docenteSeccion {} ", docenteSeccionBD.getId());
        //DocenteSeccion docenteSeccionBD = docenteSeccionDAO.find(docenteSeccionForm.getId());
        Docente docente = docenteSeccionBD.getDocente();
        Seccion seccion = docenteSeccionBD.getSeccion();

        CicloAcademico cicloAcademico = seccion.getGrupoSeccion().getCicloAcademico();
        Assert.isTrue(ciclo.getId().longValue() == cicloAcademico.getId(), "El cálculo que desea realizar no corresponde al ciclo que está trabajando.");

        Long semanasClases = cicloAcademico.getSemanasClases();
        Assert.isNotNull(semanasClases, "No está configurado la cantidad de semanas del dictado de clases del ciclo.");

        Integer horasSemanales = seccion.getHorasSemanales();
        Assert.isNotNull(horasSemanales, "No está configurado la cantidad de horas semanas del dictado de clases de la sección " + seccion.getCodigo2() + ".");

        BigDecimal porcentajeCarga = docenteSeccionBD.getPorcentajeCarga();
        Assert.isNotNull(porcentajeCarga, "No ha configurado el porcentaje de carga del docente " + docente.getCodigo() + " en la sección " + seccion.getCodigo2() + ".");

        String porcentajeCargaFraccion = docenteSeccionBD.getPorcentajeCargaFraccion();
        Assert.isNotNull(porcentajeCargaFraccion, "Error en la configuración del porcentaje de carga del docente " + docente.getCodigo() + " en la sección " + seccion.getCodigo2() + ".");

        Integer matriculados = 0;
        if (seccion.getTipoSeccionEnum() == TCUR) {
            List<Seccion> seccionesPCUR = seccion.getSeccion();
            for (Seccion secc : seccionesPCUR) {
                Integer matriculadosSecc = secc.getMatriculados();
                if (secc.getAbonoVerano().compareTo(secc.getPrecioBase()) < 0) {
                    docenteSeccionBD.setPagoVerano(null);
                    docenteSeccionDAO.update(docenteSeccionBD);
                    return "Los matriculados no lograron abonar el precio base de la sección " + secc.getCodigo2() + ".";
                }
                matriculadosSecc = (matriculadosSecc < cursoCiclo.getMinimoAlumnos().intValue()) ? cursoCiclo.getMinimoAlumnos().intValue() : matriculadosSecc;
                matriculados += matriculadosSecc;
            }

        } else {
            matriculados = seccion.getMatriculados();
            if (seccion.getAbonoVerano().compareTo(seccion.getPrecioBase()) < 0) {
                docenteSeccionBD.setPagoVerano(null);
                docenteSeccionDAO.update(docenteSeccionBD);
                return "Los matriculados no lograron abonar el precio base de la sección.";
            }
            matriculados = (matriculados < cursoCiclo.getMinimoAlumnos().intValue()) ? cursoCiclo.getMinimoAlumnos().intValue() : matriculados;
        }

        PagoHoraDocente pagoHoraDocente = findPagoDocenteByMatriculados(matriculados, pagosDocenteByHora);
        Assert.isNotNull(pagoHoraDocente, "No se encuentra configurado el pago de horas por docente para este ciclo.");

        Fraxtion porcentajeFraxtion = new Fraxtion(porcentajeCargaFraccion);
        BigDecimal precioHora = pagoHoraDocente.getMontoHora();
        BigDecimal horasSemanalesDecimal = new BigDecimal(horasSemanales);
        BigDecimal semanasClasesDecimal = new BigDecimal(semanasClases);

        BigDecimal montoPagarSeccion = precioHora
                .multiply(horasSemanalesDecimal)
                .multiply(semanasClasesDecimal);
        logger.info("precio-seccion => {}={}*{}*{}", montoPagarSeccion, precioHora, horasSemanales, semanasClasesDecimal);

        BigDecimal factor = new BigDecimal("0.01");
        BigDecimal montoPagarDocente = porcentajeFraxtion.multiply(montoPagarSeccion).multiply(factor).getValue(2, RoundingMode.HALF_DOWN);

        logger.debug("docenteSeccion {} monto generador a pagar es {}", docenteSeccionBD.getId(), montoPagarSeccion);
        docenteSeccionBD.setPagoVerano(montoPagarDocente);
        docenteSeccionDAO.update(docenteSeccionBD);
        return "Importe calculado satisfactoriamente";
    }

    private PagoHoraDocente findPagoDocenteByMatriculados(Integer matriculados, List<PagoHoraDocente> pagosDocenteByHora) {
        long mats = Long.valueOf(matriculados);
        logger.debug("buscando pago-hora-docente para {} matriculados", mats);
        for (PagoHoraDocente pagoHora : pagosDocenteByHora) {
            logger.debug("Buscando rango {} al {}", pagoHora.getAlumnosInicio(), pagoHora.getAlumnosFin());
            if (pagoHora.getAlumnosInicio() <= mats && mats <= pagoHora.getAlumnosFin()) {
                logger.debug("retornando {}", pagoHora.getMontoHora());
                return pagoHora;
            }
        }
        return null;
    }

    @Override
    public List<PagoHoraDocente> allPagosDocenteByCiclo(CicloAcademico cicloAcademico) {
        return pagoHoraDocenteDAO.allByCiclo(cicloAcademico);
    }

    @Override
    @Transactional
    public void generarPagoDocenteCiclo(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        List<PagoHoraDocente> pagosDocenteByHora = pagoHoraDocenteDAO.allByCiclo(cicloAcademico);
        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allActivosByCiclo(cicloAcademico);
        List<Seccion> secciones = docentesSecciones.stream().map(x -> x.getSeccion()).distinct().collect(Collectors.toList());
        Map<Long, Seccion> mapSeccion = TypesUtil.convertListToMap("id", secciones);
        Map<Long, List<Seccion>> mapSeccionByGpoSecc = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);
        for (Seccion seccion : secciones) {
            seccion.setSeccion(new ArrayList());
            if (seccion.getTipoSeccionEnum() == TCUR) {
                List<Seccion> seccionGpo = TypesUtil.getListNotNull(mapSeccionByGpoSecc.get(seccion.getGrupoSeccion().getId()));
                for (Seccion secc : seccionGpo) {
                    if (secc.getTipoSeccionEnum() == PCUR) {
                        seccion.getSeccion().add(secc);
                    }
                }
            }
        }

        for (DocenteSeccion docSeccion : docentesSecciones) {
            Seccion seccion = mapSeccion.get(docSeccion.getSeccion().getId());
            docSeccion.setSeccion(seccion);
        }

        List<CursoCicloAcademico> cursosCiclo = cursoCicloAcademicoDAO.allByCiclo(cicloAcademico);
        Map<Long, CursoCicloAcademico> mapCursoCiclo = TypesUtil.convertListToMap("curso.id", cursosCiclo);

        for (DocenteSeccion docSeccion : docentesSecciones) {
            Curso curso = docSeccion.getSeccion().getGrupoSeccion().getCurso();
            this.generarPagoDocente(docSeccion, mapCursoCiclo.get(curso.getId()), pagosDocenteByHora, cicloAcademico, ds);
        }

    }

}
