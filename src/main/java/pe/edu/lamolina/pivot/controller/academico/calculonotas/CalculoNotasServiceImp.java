package pe.edu.lamolina.pivot.controller.academico.calculonotas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.math.Fraxtion;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoEvaluacion;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.NotaLetra;
import pe.edu.lamolina.model.academico.ResumenAlumnoEvaluacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoEvaluacion;
import pe.edu.lamolina.model.enums.AlumnoEvaluacionEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.MotivoAnulacionEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
import pe.edu.lamolina.pivot.dao.academico.AlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.NotaLetraDAO;
import pe.edu.lamolina.pivot.dao.academico.ResumenAlumnoEvaluacionDAO;
import pe.edu.lamolina.pivot.zelper.misc.MapUtil;

@Service
@Transactional(readOnly = true)
public class CalculoNotasServiceImp implements CalculoNotasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    EvaluacionDAO evaluacionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoEvaluacionDAO alumnoEvaluacionDAO;

    @Autowired
    ResumenAlumnoEvaluacionDAO resumenAlumnoEvaluacionDAO;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;

    @Autowired
    EvaluacionExpandidaDAO evaluacionExpandidaDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    NotaLetraDAO notaLetraDAO;

    @Override
    @Transactional
    public void calcularNotasLista(List<MatriculaSeccion> matriculasSeccion, DataSessionPivot ds) {
        for (MatriculaSeccion matSecc : matriculasSeccion) {
            GrupoSeccion gpoSeccion = matSecc.getSeccion().getGrupoSeccion();
            Curso curso = gpoSeccion.getCurso();
            CicloAcademico ciclo = gpoSeccion.getCicloAcademico();
            Alumno alumno = matSecc.getMatriculaResumen().getAlumno();
            calcularNotasAlumno(alumno, gpoSeccion, curso, ciclo, ds.getUsuario());
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recalcularAllResumenEvalAlumno(Alumno alumno, GrupoSeccion grupoSeccion, int envio, DataSessionPivot ds) {

        visorCalculoNotas.incrementarCantidad();
        Curso curso = grupoSeccion.getCurso();
        calcularNotasAlumno(alumno, grupoSeccion, curso, grupoSeccion.getCicloAcademico(), ds.getUsuario());

        visorCalculoNotas.incrementarProcesados();
        visorCalculoNotas.reporte();

    }

    @Override
    @Transactional
    public void calcularNotasAlumno(Alumno alumno, GrupoSeccion grupoSeccion, Curso curso, CicloAcademico ciclo, Usuario usuario) {

        logger.debug("\n\n\n");
        logger.debug("Calcular nota alumno {} gpoSecc {} curso {} ciclo {}", alumno.getId(), grupoSeccion.getId(), curso.getId(), ciclo.getId());

        grupoSeccion = grupoSeccionDAO.find(grupoSeccion.getId());
        MatriculaCurso matriculaCurso = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, ciclo);

        Map<String, NotaLetra> mapNotaLetra = new HashMap<>();
        List<NotaLetra> notasLetras = notaLetraDAO.all();
        for (NotaLetra notasLetra : notasLetras) {
            mapNotaLetra.put(notasLetra.getLetra(), notasLetra);
        }

        int cant = 0;
        List<AlumnoEvaluacion> evaluacionesAlumno = alumnoEvaluacionDAO.allByAlumnoCursoCiclo(alumno, curso, ciclo);
        for (AlumnoEvaluacion nota : evaluacionesAlumno) {
            if (nota.getEstadoEnum() != AlumnoEvaluacionEstadoEnum.CALC) {
                cant++;
            }
        }

        if ((curso.isTieneCreditosVariables() || curso.isCreditosZero()) && cant == 1) {
            AlumnoEvaluacion aEvaluacionLetra = evaluacionesAlumno.get(0);
            aEvaluacionLetra = alumnoEvaluacionDAO.findByFilter(aEvaluacionLetra.getId(), null, null);
            Evaluacion evaluacion = evaluacionDAO.find(aEvaluacionLetra.getEvaluacion().getId());

            //     matriculaCurso.setNotaFinal(aEvaluacionLetra.getValorLetra());
            if (curso.isCreditosZero()) {
                if (aEvaluacionLetra.getValorLetra().equals("A")) {
                    matriculaCurso.setCreditosAprobados(matriculaCurso.getCreditos());
                } else if (aEvaluacionLetra.getValorLetra().equals("D")) {
                    matriculaCurso.setCreditosAprobados(BigDecimal.ZERO.intValue());
                }
            } else if (curso.isTieneCreditosVariables()) {
                matriculaCurso.setCreditosAprobados(new BigDecimal(aEvaluacionLetra.getNota()).intValue());
            }

            String notaLetra = "";
            if (aEvaluacionLetra.getValorLetra().equals("A")) {
                notaLetra = "AP";
            } else if (aEvaluacionLetra.getValorLetra().equals("D")) {
                notaLetra = "DE";
            }

            matriculaCurso.setNotaAvance(notaLetra);
            matriculaCurso.setNotaAcumulada(notaLetra);
            matriculaCurso.setPorcentajeAvanceNota(100);
            matriculaCurso.setNotaFinal(notaLetra);

            matriculaCurso.setNotaAvanceFull(aEvaluacionLetra.getNota());
            matriculaCurso.setNotaAcumuladaFull(aEvaluacionLetra.getNota());
            matriculaCursoDAO.update(matriculaCurso);

            ResumenAlumnoEvaluacion resumenAlumnoEvaluacion
                    = resumenAlumnoEvaluacionDAO.findByAlumnoGrupoTipo(alumno, grupoSeccion, evaluacion.getTipoEvaluacion());
            if (resumenAlumnoEvaluacion != null) {
                resumenAlumnoEvaluacion.setCreditos(matriculaCurso.getCreditosAprobados());
                resumenAlumnoEvaluacion.setNota(aEvaluacionLetra.getNotaLetra());
                resumenAlumnoEvaluacionDAO.update(resumenAlumnoEvaluacion);
            } else {
                resumenAlumnoEvaluacion = new ResumenAlumnoEvaluacion();
                resumenAlumnoEvaluacion.setAlumno(alumno);
                resumenAlumnoEvaluacion.setCreditos(matriculaCurso.getCreditosAprobados());
                resumenAlumnoEvaluacion.setEvaluaciones(BigDecimal.ONE.intValue());
                resumenAlumnoEvaluacion.setGrupoSeccion(grupoSeccion);
                resumenAlumnoEvaluacion.setNota(aEvaluacionLetra.getNotaLetra());
                resumenAlumnoEvaluacion.setTipoEvaluacion(evaluacion.getTipoEvaluacion());
                resumenAlumnoEvaluacionDAO.save(resumenAlumnoEvaluacion);
            }
            return;
        }

        if (cant == 0) {
            matriculaCurso.setNotaAvance(NumberFormat.notaDecimal4Decimals(BigDecimal.ZERO));
            matriculaCurso.setNotaAcumulada(NumberFormat.notaDecimal4Decimals(BigDecimal.ZERO));
            matriculaCurso.setPorcentajeAvanceNota(0);
            matriculaCurso.setNotaFinal("0");

            matriculaCurso.setNotaAvanceFull(NumberFormat.notaDecimal10Decimals(BigDecimal.ZERO));
            matriculaCurso.setNotaAcumuladaFull(NumberFormat.notaDecimal10Decimals(BigDecimal.ZERO));
            matriculaCursoDAO.update(matriculaCurso);

            for (AlumnoEvaluacion nota : evaluacionesAlumno) {
                nota.setValorNumerico(Fraxtion.ZERO.getValue(2, RoundingMode.HALF_UP));
                nota.setNota(NumberFormat.notaDecimal(nota.getValorNumerico()));
                alumnoEvaluacionDAO.update(nota);
            }
            return;
        }

        List<Evaluacion> evaluaciones = evaluacionDAO.allByGrupoSeccionAlumno(grupoSeccion, alumno);
        joinConfiguracionEvaluaciones(evaluaciones, evaluacionesAlumno);

        List<EvaluacionExpandida> configPrimerNivel = allConfigByNivel(evaluaciones, 1);
        Fraxtion pesoTotal = Fraxtion.ZERO;
        for (EvaluacionExpandida cfgEval : configPrimerNivel) {
            pesoTotal = pesoTotal.add(cfgEval.getPeso());
        }

        List<Fraxtion> notas = new ArrayList();
        List<Fraxtion> pesos = new ArrayList();
        for (EvaluacionExpandida cfgEval : configPrimerNivel) {
            logger.debug("\tCalculando notas de {}", cfgEval.getId());
            calcularNotaEvaluacion(cfgEval, pesoTotal, pesoTotal, notas, pesos);
        }

        Fraxtion dividendo = Fraxtion.ZERO;
        Fraxtion pesoConNota = Fraxtion.ZERO;
        for (int i = 0; i < notas.size(); i++) {
            dividendo = dividendo.add(notas.get(i).multiply(pesos.get(i)));
            pesoConNota = pesoConNota.add(pesos.get(i));
        }

        Fraxtion avance = dividendo.divide(pesoConNota);
        Fraxtion prom = dividendo.divide(pesoTotal);
        matriculaCurso.setNotaAvance(NumberFormat.notaDecimal4Decimals(avance.getValue()));
        matriculaCurso.setNotaAcumulada(NumberFormat.notaDecimal4Decimals(prom.getValue()));
        matriculaCurso.setPorcentajeAvanceNota(pesoConNota.getValue().intValue());
        matriculaCurso.setNotaFinal("0");

        avance = dividendo.divide(pesoConNota);
        prom = dividendo.divide(pesoTotal);

        matriculaCurso.setNotaAvanceFull(NumberFormat.notaDecimal10Decimals(avance.getValue(18)));
        matriculaCurso.setNotaAcumuladaFull(NumberFormat.notaDecimal10Decimals(prom.getValue(18)));
        logger.debug("peso con nota {} peso total {}", pesoConNota, pesoTotal);
        if (pesoConNota.compareTo(pesoTotal) == 0) {
            BigDecimal notaFinal = calularNota(dividendo, pesoTotal, 0);
            matriculaCurso.setNotaFinal(NumberFormat.nota(notaFinal));
        }
        matriculaCursoDAO.update(matriculaCurso);

        for (Evaluacion eval : evaluaciones) {
            if (eval.getAlumnoEvaluacion().isEmpty()) {
                continue;
            }
            AlumnoEvaluacion nota = eval.getAlumnoEvaluacion().get(0);
            if (nota.getId() == null) {
                nota.setAlumno(alumno);
                nota.setEsIngresoRegular(0);
                nota.setFechaIngresoNota(new Date());
                nota.setUsuarioIngresoNota(usuario);
                nota.setNota(NumberFormat.notaDecimal(nota.getValorNumerico()));
                alumnoEvaluacionDAO.save(nota);

            } else {
                alumnoEvaluacionDAO.update(nota);
            }
        }

        List<ResumenAlumnoEvaluacion> resumenes = resumenAlumnoEvaluacionDAO.allByAlumnoGrupoSeccion(alumno, grupoSeccion);
        Map<Long, ResumenAlumnoEvaluacion> mapResumenes = MapUtil.storeItems("tipoEvaluacion.id", resumenes);
        Map<Long, EvaluacionExpandida> mapConfigPrimerNivel = MapUtil.storeItems("tipoEvaluacion.id", configPrimerNivel);
        for (ResumenAlumnoEvaluacion resumen : resumenes) {
            EvaluacionExpandida cfgEval = mapConfigPrimerNivel.get(resumen.getTipoEvaluacion().getId());
            if (cfgEval == null) {
                resumenAlumnoEvaluacionDAO.delete(resumen);
            }
        }

        for (EvaluacionExpandida cfgEval : configPrimerNivel) {
            ResumenAlumnoEvaluacion resumen = mapResumenes.get(cfgEval.getTipoEvaluacion().getId());
            if (resumen == null) {
                List<AlumnoEvaluacion> notax = cfgEval.getEvaluaciones().get(0).getAlumnoEvaluacion();
                if (notax.isEmpty()) {
                    continue;
                }

                AlumnoEvaluacion nota = notax.get(0);
                resumen = new ResumenAlumnoEvaluacion();
                resumen.setAlumno(alumno);
                resumen.setGrupoSeccion(grupoSeccion);
                resumen.setTipoEvaluacion(cfgEval.getTipoEvaluacion());

                if (grupoSeccion.getPlanCalificacion().getSistemaNotas().isNumerico()) {
                    resumen.setNota(NumberFormat.notaDecimal(nota.getValorNumerico()));
                } else if (grupoSeccion.getPlanCalificacion().getSistemaNotas().isLetras()) {
                    resumen.setNota(nota.getNotaLetra());
                    resumen.setCreditos(Integer.valueOf(NumberFormat.nota(nota.getValorNumerico())));
                }
                resumenAlumnoEvaluacionDAO.save(resumen);

            } else {
                List<AlumnoEvaluacion> notax = cfgEval.getEvaluaciones().get(0).getAlumnoEvaluacion();
                if (notax.isEmpty()) {
                    resumenAlumnoEvaluacionDAO.delete(resumen);
                    continue;
                }

                AlumnoEvaluacion nota = notax.get(0);
                if (grupoSeccion.getPlanCalificacion().getSistemaNotas().isNumerico()) {
                    resumen.setNota(NumberFormat.notaDecimal(nota.getValorNumerico()));

                } else if (grupoSeccion.getPlanCalificacion().getSistemaNotas().isLetras()) {
                    resumen.setNota(nota.getNotaLetra());
                    resumen.setCreditos(Integer.valueOf(NumberFormat.nota(nota.getValorNumerico())));
                }

                resumenAlumnoEvaluacionDAO.update(resumen);
            }
        }

        logger.debug("Finalizó calculo notas del alumno {} gpoSecc {} curso {} ciclo {}", alumno.getId(), grupoSeccion.getId(), curso.getId(), ciclo.getId());
    }

    private void calcularNotaEvaluacion(EvaluacionExpandida configEvaluacion, Fraxtion pesoGrupo, Fraxtion pesoPadre, List<Fraxtion> notas, List<Fraxtion> pesos) {
        List<EvaluacionExpandida> configEvaluacionesHijas = configEvaluacion.getEvaluacionesExpandidas();
        Fraxtion pesoNota = configEvaluacion.getPesoFraxtion().multiply(pesoPadre).divide(pesoGrupo);
        Fraxtion pesoNota2 = configEvaluacion.getPesoFraxtion().multiply(pesoPadre).divide(pesoGrupo);
        logger.debug("\tpeso-nota: {}", pesoNota);
        if (pesoNota.getValue().compareTo(pesoNota2.getValue(16)) != 0) {
            logger.debug("\tEs con decimales indefinidos");
        }
        if (configEvaluacionesHijas.isEmpty()) {
            Evaluacion evaluacion = configEvaluacion.getEvaluaciones().get(0);
            List<AlumnoEvaluacion> notax = evaluacion.getAlumnoEvaluacion();
            if (notax.isEmpty()) {
                return;
            }

            AlumnoEvaluacion nota = notax.get(0);
            notas.add(new Fraxtion(nota.getValorNumerico()));
            pesos.add(pesoNota);
            return;
        }

        Fraxtion pesoGrupoHijos = Fraxtion.ZERO;
        for (EvaluacionExpandida cfgEval : configEvaluacionesHijas) {
            pesoGrupoHijos = pesoGrupoHijos.add(cfgEval.getPeso());
        }

        if (configEvaluacion.getNotaMinimaAnulable() == 0) {
            for (EvaluacionExpandida cfgEval : configEvaluacionesHijas) {
                calcularNotaEvaluacion(cfgEval, pesoGrupoHijos, pesoNota, notas, pesos);
            }
            promediarNotaDeHijos(configEvaluacion);
        }

        if (configEvaluacion.getNotaMinimaAnulable() > 0) {
            if (todosTienenNota(configEvaluacionesHijas)) {
                for (EvaluacionExpandida cfgEval : configEvaluacionesHijas) {
                    calcularNotaEvaluacion(cfgEval, pesoGrupoHijos, pesoGrupoHijos, new ArrayList(), new ArrayList());
                }

                promediarNotaConAnulables(configEvaluacion);
                AlumnoEvaluacion nota = configEvaluacion.getEvaluaciones().get(0).getAlumnoEvaluacion().get(0);
                notas.add(new Fraxtion(nota.getValorNumerico()));
                pesos.add(pesoNota);

            } else {
                for (EvaluacionExpandida cfgEval : configEvaluacionesHijas) {
                    calcularNotaEvaluacion(cfgEval, pesoGrupoHijos, pesoNota, notas, pesos);
                }
                promediarNotaDeHijos(configEvaluacion);
            }
        }

    }

    private void promediarNotaConAnulables(EvaluacionExpandida configEvaluacion) {
        List<EvaluacionExpandida> configEvaluacionesHijas = configEvaluacion.getEvaluacionesExpandidas();
        List<AlumnoEvaluacion> notasHijas = allNotasHijos(configEvaluacionesHijas);

        List<List<Integer>> permutaciones = crearPermutaciones(notasHijas, configEvaluacion.getNotaMinimaAnulable());
        for (List<Integer> permu : permutaciones) {
            Collections.sort(permu, Collections.reverseOrder());
        }

        Map<String, Fraxtion> mapPromedios = new LinkedHashMap();
        Map<String, List<Integer>> mapPermutaciones = new LinkedHashMap();
        for (List<Integer> permu : permutaciones) {
            List<AlumnoEvaluacion> copiaNotas = clonarLista(notasHijas);
            for (Integer index : permu) {
                copiaNotas.remove(index.intValue());
            }
            Fraxtion prom = calcularPonderado(copiaNotas);
            mapPromedios.put(permu.toString(), prom);
            mapPermutaciones.put(permu.toString(), permu);
        }

        List<Fraxtion> promedios = new ArrayList();
        for (Map.Entry<String, Fraxtion> entry : mapPromedios.entrySet()) {
            promedios.add(entry.getValue());
        }

        Collections.sort(promedios, new Fraxtion.OrdenReverso());
        Fraxtion promFinal = promedios.get(0);

        List<Integer> perm = null;
        for (Map.Entry<String, Fraxtion> entry : mapPromedios.entrySet()) {
            String indices = entry.getKey();
            Fraxtion prom = entry.getValue();
            if (prom == promFinal) {
                perm = mapPermutaciones.get(indices);
                break;
            }
        }

        for (AlumnoEvaluacion nota : notasHijas) {
            nota.setEstadoEnum(AlumnoEvaluacionEstadoEnum.ACT);
            nota.setMotivoAnulacion("");
            nota.setFechaAnulacion(null);
        }
        for (Integer idx : perm) {
            AlumnoEvaluacion nota = notasHijas.get(idx.intValue());
            nota.setEstadoEnum(AlumnoEvaluacionEstadoEnum.ANM);
            nota.setMotivoAnulacion(MotivoAnulacionEnum.NOTA_MIN.name());
            nota.setFechaAnulacion(new Date());
        }

        Evaluacion evaluacion = configEvaluacion.getEvaluaciones().get(0);
        List<AlumnoEvaluacion> notas = evaluacion.getAlumnoEvaluacion();

        AlumnoEvaluacion nota = notas.isEmpty() ? null : notas.get(0);
        if (nota != null) {
            nota.setValorNumerico(promFinal.getValue(2, RoundingMode.HALF_UP));
            nota.setEstadoEnum(AlumnoEvaluacionEstadoEnum.CALC);
            return;
        }

        nota = new AlumnoEvaluacion();
        nota.setValorNumerico(promFinal.getValue(2, RoundingMode.HALF_UP));
        nota.setEvaluacion(evaluacion);
        nota.setEstadoEnum(AlumnoEvaluacionEstadoEnum.CALC);
        notas.add(nota);

    }

    private List<List<Integer>> crearPermutaciones(List<AlumnoEvaluacion> notas, Integer anulables) {
        List<Integer> items = new ArrayList();
        for (int i = 0; i < notas.size(); i++) {
            items.add(i);
        }
        List<Integer> tempo = new ArrayList();
        Map<String, List<Integer>> mapeados = new LinkedHashMap();
        List<List<Integer>> buscados = new ArrayList();
        permutar(anulables, 1, items, tempo, mapeados, buscados);
        return buscados;
    }

    private void permutar(int cant, int nivel, List<Integer> items, List<Integer> tomados, Map<String, List<Integer>> mapeados, List<List<Integer>> buscados) {
        for (Integer item : items) {
            if (tomados.contains(item)) {
                continue;
            }
            if (esPosible(item, tomados, mapeados)) {
                continue;
            }

            tomados.add(item);
            Collections.sort(tomados);
            mapeados.put(tomados.toString(), tomados);
            if (tomados.size() == cant) {
                List<Integer> buscado = clonarLista(tomados);
                buscados.add(buscado);
            }

            if (cant == nivel) {

            } else {
                List<Integer> copiaItems = clonarLista(items);
                List<Integer> copiaTomados = clonarLista(tomados);
                copiaItems.remove(new Integer(item));
                permutar(cant, nivel + 1, copiaItems, copiaTomados, mapeados, buscados);
            }
            tomados.remove(new Integer(item));
        }
    }

    private boolean esPosible(Integer item, List<Integer> tomados, Map<String, List<Integer>> mapeados) {
        List<Integer> copia = clonarLista(tomados);
        copia.add(item);
        Collections.sort(copia);
        List<Integer> existe = mapeados.get(copia.toString());
        return (existe != null);
    }

    private List clonarLista(List lista) {
        List clonada = new ArrayList();
        for (Object item : lista) {
            clonada.add(item);
        }
        return clonada;
    }

    private void promediarNotaDeHijos(EvaluacionExpandida configEvaluacion) {
        List<EvaluacionExpandida> configEvaluacionesHijas = configEvaluacion.getEvaluacionesExpandidas();
        List<AlumnoEvaluacion> notasHijas = allNotasHijos(configEvaluacionesHijas);
        Fraxtion prom = calcularPonderado(notasHijas);

        Evaluacion evaluacion = configEvaluacion.getEvaluaciones().get(0);
        List<AlumnoEvaluacion> notas = evaluacion.getAlumnoEvaluacion();
        if (notas.isEmpty() && prom == null) {
            return;
        }

        AlumnoEvaluacion nota = notas.isEmpty() ? null : notas.get(0);

        if (nota != null && prom == null) {
            alumnoEvaluacionDAO.delete(nota);
            evaluacion.getAlumnoEvaluacion().remove(nota);
            return;
        }

        if (nota != null && prom != null) {
            nota.setValorNumerico(prom.getValue(2, RoundingMode.HALF_UP));
            nota.setNota(NumberFormat.notaDecimal(prom.getValue(2, RoundingMode.HALF_UP)));
            return;
        }

        nota = new AlumnoEvaluacion();
        nota.setValorNumerico(prom.getValue(2, RoundingMode.HALF_UP));
        nota.setNota(NumberFormat.notaDecimal(prom.getValue(2, RoundingMode.HALF_UP)));
        evaluacion.getAlumnoEvaluacion().add(nota);
        nota.setEvaluacion(evaluacion);
        nota.setEstadoEnum(AlumnoEvaluacionEstadoEnum.CALC);
    }

    private Fraxtion calcularPonderado(List<AlumnoEvaluacion> notas) {
        if (notas.isEmpty()) {
            return null;
        }
        int cantidad = 0;
        for (AlumnoEvaluacion nota : notas) {
            if (nota.getValorNumerico() == null) {
                continue;
            }
            cantidad++;
        }
        if (cantidad == 0) {
            return null;
        }

        Fraxtion dividendo = Fraxtion.ZERO;
        Fraxtion divisor = Fraxtion.ZERO;
        for (AlumnoEvaluacion nota : notas) {
            if (nota.getValorNumerico() == null) {
                continue;
            }
            EvaluacionExpandida cfgEval = nota.getEvaluacion().getEvaluacionExpandida();
            dividendo = dividendo.add(nota.getValorNumerico().multiply(cfgEval.getPeso()));
            divisor = divisor.add(cfgEval.getPeso());
        }
        Fraxtion promedio = dividendo.divide(divisor);
        return promedio;
    }

    private boolean todosTienenNota(List<EvaluacionExpandida> configEvaluaciones) {
        for (EvaluacionExpandida cfgEval : configEvaluaciones) {
            if (cfgEval.getEstadoEnum() != EstadoEnum.ACT) {
                continue;
            }
            Evaluacion evaluacion = cfgEval.getEvaluaciones().get(0);
            if (evaluacion.getAlumnoEvaluacion().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private List<AlumnoEvaluacion> allNotasHijos(List<EvaluacionExpandida> configEvaluaciones) {
        List<AlumnoEvaluacion> notas = new ArrayList();
        for (EvaluacionExpandida cfgEval : configEvaluaciones) {
            if (cfgEval.getEstadoEnum() != EstadoEnum.ACT) {
                continue;
            }
            Evaluacion evaluacion = cfgEval.getEvaluaciones().get(0);
            if (evaluacion.getAlumnoEvaluacion().isEmpty()) {
                continue;
            }
            AlumnoEvaluacion nota = evaluacion.getAlumnoEvaluacion().get(0);
            notas.add(nota);
        }
        return notas;
    }

    private List<EvaluacionExpandida> allConfigByNivel(List<Evaluacion> evaluaciones, int nivel) {
        Map<Long, EvaluacionExpandida> mapConfiguraciones = MapUtil.storeItems("evaluacionExpandida.id", "evaluacionExpandida", evaluaciones);
        List<EvaluacionExpandida> configuraciones = new ArrayList();
        for (EvaluacionExpandida cfgEval : mapConfiguraciones.values()) {
            if (cfgEval.getNivel() != nivel) {
                continue;
            }
            configuraciones.add(cfgEval);
        }
        return configuraciones;
    }

    private void joinConfiguracionEvaluaciones(List<Evaluacion> evaluaciones, List<AlumnoEvaluacion> notasAlumno) {
        for (Evaluacion eval : evaluaciones) {
            eval.setAlumnoEvaluacion(new ArrayList());
            EvaluacionExpandida cfgEval = eval.getEvaluacionExpandida();
            cfgEval.setEvaluacionesExpandidas(new ArrayList());
            cfgEval.setEvaluaciones(new ArrayList());
            cfgEval.getEvaluaciones().add(eval);
        }

        Map<Long, EvaluacionExpandida> mapConfigEval = MapUtil.storeItems("evaluacionExpandida.id", "evaluacionExpandida", evaluaciones);

        for (Evaluacion eval : evaluaciones) {
            EvaluacionExpandida cfgEval = eval.getEvaluacionExpandida();
            if (cfgEval.getEvaluacionSuperior() != null) {
                EvaluacionExpandida superior = mapConfigEval.get(cfgEval.getEvaluacionSuperior().getId());
                cfgEval.setEvaluacionSuperior(superior);
                superior.getEvaluacionesExpandidas().add(cfgEval);
            }
        }

        Map<Long, Evaluacion> mapEvaluaciones = MapUtil.storeItems("id", evaluaciones);
        for (AlumnoEvaluacion evalAlumno : notasAlumno) {
            Evaluacion eval = mapEvaluaciones.get(evalAlumno.getEvaluacion().getId());

            eval.getAlumnoEvaluacion().add(evalAlumno);
            evalAlumno.setEvaluacion(eval);
        }

        for (Evaluacion eval : evaluaciones) {
            List<AlumnoEvaluacion> notas = eval.getAlumnoEvaluacion();
        }
    }

    private BigDecimal calularNota(Fraxtion ponderado, Fraxtion pesoTotal, int redondeo) {
        if (pesoTotal.isZero()) {
            return BigDecimal.ZERO;
        }

        Fraxtion nota = ponderado.divide(pesoTotal);
        return nota.getValue(redondeo, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(readOnly = false)
    public void calcularNotas(EvaluacionExpandida evaluacionExpandida, CicloAcademico cicloAcademico, Usuario usuario) {
        evaluacionExpandida = evaluacionExpandidaDAO.find(evaluacionExpandida.getId());
        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allByGpoSeccion(evaluacionExpandida.getEvaluacionSeccion().getGrupoSeccion(), cicloAcademico);

        for (MatriculaSeccion ms : matriculasSeccion) {
            Seccion seccion = ms.getSeccion();
            GrupoSeccion gpoSecc = seccion.getGrupoSeccion();
            Alumno alumno = ms.getMatriculaResumen().getAlumno();

            if (gpoSecc.getPlanCalificacion() == null) {
                break;
            }

            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.PCUR) {
                continue;
            }

            this.calcularNotasAlumno(alumno, gpoSecc, gpoSecc.getCurso(), gpoSecc.getCicloAcademico(), usuario);

        }
    }

}
