package pe.edu.lamolina.pivot.controller.academico.loadprogramacion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Evaluacion;
import pe.edu.lamolina.model.academico.EvaluacionExpandida;
import pe.edu.lamolina.model.academico.EvaluacionPlan;
import pe.edu.lamolina.model.academico.EvaluacionSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionExpandidaDAO;
import pe.edu.lamolina.pivot.dao.academico.EvaluacionPlanDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.zelper.misc.MapUtil;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EvaluacionExpandidaServiceImpl implements EvaluacionExpandidaService {

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;
    @Autowired
    EvaluacionPlanDAO evaluacionPlanDAO;
    @Autowired
    EvaluacionDAO evaluacionDAO;
    @Autowired
    EvaluacionExpandidaDAO evaluacionExpandidaDAO;

    @Override
    @Transactional
    public void recalcularNivel(CicloAcademico ciclo, DataSessionPivot ds) {
        List<GrupoSeccion> gposSeccion = grupoSeccionDAO.allByCiclo(ciclo);
        gposSeccion.forEach((gpoSec) -> {
            gpoSec.setSecciones(new ArrayList());
            gpoSec.setEvaluacionSecciones(new ArrayList());
        });

        Map<Long, GrupoSeccion> mapGpoSeccion = MapUtil.storeItems("id", gposSeccion);
        Map<Long, PlanCalificacion> mapPlanes = MapUtil.storeItems("planCalificacion.id", "planCalificacion", gposSeccion);
        List<PlanCalificacion> planes = new ArrayList(mapPlanes.values());
        List<EvaluacionPlan> evalucionesPlanes = evaluacionPlanDAO.allByPlanes(planes);
        Map<Long, List<EvaluacionPlan>> mapEvaluacionPlan = MapUtil.storeLists("planCalificacion.id", evalucionesPlanes);
        planes.forEach((plan) -> {
            List<EvaluacionPlan> evaluacionesPlan = mapEvaluacionPlan.get(plan.getId());
            plan.setEvaluacionPlan(evaluacionesPlan);
        });

        gposSeccion.forEach((gpoSecc) -> {
            PlanCalificacion plan = gpoSecc.getPlanCalificacion();
            if (!(plan == null)) {
                gpoSecc.setPlanCalificacion(mapPlanes.get(plan.getId()));
            }
        });

        List<Evaluacion> evaluaciones = evaluacionDAO.allByCiclo(ciclo);
        Map<Long, List<Evaluacion>> mapListaEvalucionByExpan = MapUtil.storeLists("evaluacionExpandida.id", evaluaciones);
        Map<Long, EvaluacionExpandida> mapEvalExpandida = MapUtil.storeItems("evaluacionExpandida.id", "evaluacionExpandida", evaluaciones);
        List<EvaluacionExpandida> evaluacionesExpandidas = new ArrayList(mapEvalExpandida.values());
        evaluacionesExpandidas.forEach((evalExpan) -> {
            List<Evaluacion> evals = mapListaEvalucionByExpan.get(evalExpan.getId());
            evalExpan.setEvaluaciones(evals);
            evals.forEach((eval) -> {
                eval.setEvaluacionExpandida(evalExpan);
            });
            if (evalExpan.getEvaluacionSuperior() != null) {

            }
        });

        Map<Long, List<EvaluacionExpandida>> mapListaEvalExpandida = MapUtil.storeLists("evaluacionSeccion.id", evaluacionesExpandidas);
        Map<Long, EvaluacionSeccion> mapEvalSeccion = MapUtil.storeItems("evaluacionSeccion.id", "evaluacionSeccion", evaluacionesExpandidas);
        List<EvaluacionSeccion> evaluacionesSeccion = new ArrayList(mapEvalSeccion.values());
        evaluacionesSeccion.forEach((evalSecc) -> {
            List<EvaluacionExpandida> evalsExpan = mapListaEvalExpandida.get(evalSecc.getId());
            evalSecc.setEvaluacionExpandida(evalsExpan);
            evalsExpan.forEach((evalExpan) -> {
                evalExpan.setEvaluacionSeccion(evalSecc);
            });

            PlanCalificacion plan = mapPlanes.get(evalSecc.getPlanCalificacion().getId());
            GrupoSeccion gpoSecc = mapGpoSeccion.get(evalSecc.getGrupoSeccion().getId());
            evalSecc.setPlanCalificacion(plan);
            evalSecc.setGrupoSeccion(gpoSecc);
            gpoSecc.getEvaluacionSecciones().add(evalSecc);
        });

        Map<Long, Seccion> mapSeccion = MapUtil.storeItems("seccionResponsable.id", "seccionResponsable", evaluaciones);
        Map<Long, List<Evaluacion>> mapListaEvalucionBySeccion = MapUtil.storeLists("seccionResponsable.id", evaluaciones);
        List<Seccion> secciones = new ArrayList(mapSeccion.values());
        secciones.forEach((secc) -> {
            List<Evaluacion> evalsSeccion = mapListaEvalucionBySeccion.get(secc.getId());
            secc.setEvaluacion(evalsSeccion);
            evalsSeccion.forEach((eval) -> {
                eval.setSeccionResponsable(secc);
            });
            GrupoSeccion gpoSecc = mapGpoSeccion.get(secc.getGrupoSeccion().getId());
            secc.setGrupoSeccion(gpoSecc);
        });

        for (GrupoSeccion gpoSecc : gposSeccion) {
            procesarGrupoSeccion(gpoSecc, ds);
        }
    }

    private void procesarGrupoSeccion(GrupoSeccion gpoSecc, DataSessionPivot ds) {
        System.out.println("Actualizarion Gpo-Seccion " + gpoSecc.getId());
        PlanCalificacion plan = gpoSecc.getPlanCalificacion();
        EvaluacionSeccion evalSecc = findEvaluacionSeccion(gpoSecc);
        if (plan == null || evalSecc == null) {
            return;
        }
        List<EvaluacionExpandida> evalsExpan = evalSecc.getEvaluacionExpandida();
        if (tieneNivelAbuelo(evalsExpan)) {
            return;
        }
        List<EvaluacionExpandida> evalsExpan2nivel = allSegundoNivel(evalsExpan);
        Map<Long, List<EvaluacionExpandida>> mapEvalByTipo = MapUtil.storeLists("tipoEvaluacion.id", evalsExpan2nivel);

        List<EvaluacionPlan> evaluasPlan = plan.getEvaluacionPlan();
        for (EvaluacionPlan evalPlan : evaluasPlan) {
            List<EvaluacionExpandida> evalsByTipo = mapEvalByTipo.get(evalPlan.getTipoEvaluacion().getId());
            if (evalPlan.getCantidadEvaluaciones() != evalsByTipo.size()) {
                throw new PhobosException("Incoherencia en la cantidad de evaluaciones en gpoSeccion " + gpoSecc.getId() + " para el tipo " + evalPlan.getTipoEvaluacion().getId());
            }
            if (evalPlan.getCantidadEvaluaciones() == 1) {
                continue;
            }

            EvaluacionExpandida eex = evalsByTipo.get(0);
//            System.out.println("\tNotaMinimaAnulable: " + evalPlan.getNotaMinimaAnulable());

            EvaluacionExpandida evalExpan = new EvaluacionExpandida();
            evalExpan.setEstaDesagregado(1);
            evalExpan.setEstadoEnum(EstadoEnum.ACT);
            evalExpan.setEvaluacionSeccion(evalSecc);
            evalExpan.setFechaDesagregar(new Date());
            evalExpan.setNotasIngresadas(0);
            evalExpan.setPorcentajeVariable(0);
//            evalExpan.setPorcentajeVariable(evalPlan.getIndPorcentajeVariable());
            evalExpan.setNivel(1);
//            evalExpan.setNotaMinimaAnulable(evalPlan.getNotaMinimaAnulable());
            evalExpan.setNotaMinimaAnulable(0);
            evalExpan.setNumero(0);
            evalExpan.setPeso(evalPlan.getPesoTotal());
            evalExpan.setTipoEvaluacion(evalPlan.getTipoEvaluacion());
            evalExpan.setTipoSeccion(eex.getTipoSeccion());
            evalExpan.setUsuarioDesagregar(ds.getUsuario());
            evaluacionExpandidaDAO.save(evalExpan);
            System.out.println("\tSe creo EvaluacionExpandida: " + evalExpan.getId() + "");
            evalsExpan.add(evalExpan);

            for (EvaluacionExpandida evalExpanTipo : evalsByTipo) {
                evalExpanTipo.setEvaluacionSuperior(evalExpan);
                evaluacionExpandidaDAO.update(evalExpanTipo);
                System.out.println("\tSe puso a eval-expan:" + evalExpanTipo.getId() + " como superior a " + evalExpan.getId());
            }

            List<Seccion> seccionesExpan = allSeccionesByExpandidas(evalsByTipo);
            for (Seccion seccion : seccionesExpan) {
                Evaluacion eval = new Evaluacion();
                eval.setEstaDesagregado(1);
                eval.setEvaluacionExpandida(evalExpan);
                eval.setEvaluacionSeccion(evalSecc);
                eval.setEvaluados(0);
                eval.setFechaDesagregar(new Date());
                eval.setPorcentajeVariable(0);
//                eval.setPorcentajeVariable(evalPlan.getIndPorcentajeVariable());
                eval.setNumero(0);
                eval.setPeso(evalPlan.getPesoTotal());
                eval.setSeccionResponsable(seccion);
                eval.setTipoEvaluacion(evalPlan.getTipoEvaluacion());
                eval.setTipoSeccion(eex.getTipoSeccion());
                eval.setUsuarioDesagregar(ds.getUsuario());
                evaluacionDAO.save(eval);
                evalExpan.setEvaluaciones(new ArrayList());
                evalExpan.getEvaluaciones().add(eval);

                System.out.println("\tSe crea Evaluacion: " + eval.getId());

                for (EvaluacionExpandida evalExpTipo : evalsByTipo) {
                    List<Evaluacion> evaluacionesExpTipo = allEvaluacionesBySeccion(seccion, evalExpTipo.getEvaluaciones());
                    for (Evaluacion evaluacion : evaluacionesExpTipo) {
                        evaluacion.setEvaluacionSuperior(eval);
                        evaluacionDAO.update(eval);
                        System.out.println("\tSe puso a Evaluacion:" + evaluacion.getId() + " como superior a " + eval.getId());
                    }
                }
            }

        }
    }

    private List<Evaluacion> allEvaluacionesBySeccion(Seccion seccion, List<Evaluacion> evaluaciones) {
        List<Evaluacion> lista = new ArrayList();
        for (Evaluacion eval : evaluaciones) {
            if (seccion.getId().longValue() == eval.getSeccionResponsable().getId()) {
                lista.add(eval);
            }
        }
        return lista;
    }

    private List<Seccion> allSeccionesByExpandidas(List<EvaluacionExpandida> evalsExpan) {
        Map<Long, Seccion> mapSeccion = new LinkedHashMap();
        evalsExpan.stream().map((evalExp) -> evalExp.getEvaluaciones()).forEachOrdered((evals) -> {
            evals.forEach((eval) -> {
                mapSeccion.put(eval.getSeccionResponsable().getId(), eval.getSeccionResponsable());
            });
        });
        return new ArrayList(mapSeccion.values());
    }

    private List<EvaluacionExpandida> allSegundoNivel(List<EvaluacionExpandida> evalsExpan) {
        List<EvaluacionExpandida> lista = new ArrayList();
        evalsExpan.stream()
                .filter((evalExpan) -> (evalExpan.getNivel() == 2))
                .forEachOrdered((evalExpan) -> {
                    lista.add(evalExpan);
                });
        return lista;
    }

    private boolean tieneNivelAbuelo(List<EvaluacionExpandida> evalsExpan) {
        return evalsExpan.stream().anyMatch((evalExpan) -> (evalExpan.getNivel() == 1));
    }

    private EvaluacionSeccion findEvaluacionSeccion(GrupoSeccion gpoSecc) {
        List<EvaluacionSeccion> evalsSecc = gpoSecc.getEvaluacionSecciones();
        if (evalsSecc == null) {
            return null;
        }
        if (evalsSecc.isEmpty()) {
            return null;
        }
        if (evalsSecc.size() > 1) {
            throw new PhobosException("El gpoSecc " + gpoSecc.getId() + " tiene " + evalsSecc.size() + " evaluaciones-secciones");
        }
        return evalsSecc.get(0);
    }

    @Override
    public void analizarLogCarga() {
        Map<String, String> mapAlumnos = new LinkedHashMap();

        String filename = "/Users/joss/Desktop/albatross/files/otros/log.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("vamos a bloquear alumno")) {
                    String idMain;
                    String codigo;
                    if (line.contains("::::")) {
                        idMain = line.substring(line.indexOf(':') + 5, line.indexOf(32, 47));
                        codigo = "por sacar";
                    } else {
                        idMain = line.substring(0, line.indexOf(32));
                        codigo = line.substring(line.indexOf(' ', 25) + 1, line.indexOf(' ', 25) + 8);
                    }
//                    mapAlumnos.put(idMain, codigo);
                    System.out.print("Bloq: ");
                    System.out.println(idMain + " " + codigo);
                }
                if (line.startsWith("\t") && !line.contains("...") && !line.contains("name") && line.contains("desbloqueado")) {
                    String id = line.substring(1, line.indexOf(32));
                    mapAlumnos.remove(id);
                    System.out.println("des" + id);
                }
            }
//            for (Map.Entry<String, String> alumno : mapAlumnos.entrySet()) {
//                System.out.println(alumno.getKey() + "/" + alumno.getValue());
//            }
            reader.close();
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
        }

    }

}
