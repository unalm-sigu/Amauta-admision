package pe.edu.lamolina.pivot.controller.general.aula;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.notify.Notificaciones;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.enums.TipoAmbienteEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/aula")
public class AulaController {
    
    @Autowired
    AulaService service;
    
    @Autowired
    PdfHorariosAula pdfHorariosAula;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        
        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });
        
        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("tiposAmbiente", TipoAmbienteEnum.values());
        return "general/aula/aula";
    }
    
    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatableee(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            
            List<Aula> aulas = service.allByDynatable(filter);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            
            ArrayNode array = new ArrayNode(jFactory);
            
            for (Aula aula : aulas) {
                ObjectNode node = new ObjectNode(jFactory);
                
                node.put("id", aula.getId());
                node.put("codigo", aula.getCodigo());
                node.put("nombre", aula.getNombre());
                node.put("tipoAmbienteEnum", aula.getTipoAmbienteEnum().getValue());
                node.put("tipoAmbiente", aula.getTipoAmbiente());
                node.put("piso", aula.getPiso());
                node.put("pisos", aula.getPisos());
                node.put("aforo", aula.getAforo());
                node.put("pabellon", (String) ObjectUtil.getParentTree(aula, "aulaSuperior.nombre"));
                node.put("capacidad", aula.getCapacidadAula());
                node.put("sede", aula.getSede() != null ? aula.getSede().getNombre() : "");
                node.put("tipoAula", aula.getTipoAula() != null ? aula.getTipoAula().getNombre() : "");
                node.put("tipoCarpeta", aula.getTipoCarpeta() != null ? "" + ObjectUtil.getParentTree(aula, "tipoCarpeta.nombre") : "");
                node.put("gestor", aula.getOficinaSupervisora() != null ? aula.getOficinaSupervisora().getNombre() : "");
                node.put("estado", aula.getEstado());
                node.put("estadoEnum", aula.getEstadoEnum().getValue());
                node.put("motivo", aula.getMotivoAnulacion());
                node.put("aulasContenido", aula.getAulasContenido().size());
                
                ArrayNode arrayHijas = new ArrayNode(jFactory);
                List<Aula> aulasHijas = aula.getAulasContenido();
                for (Aula aulaHija : aulasHijas) {
                    ObjectNode nodeHija = new ObjectNode(jFactory);
                    nodeHija.put("codigo", aulaHija.getCodigo());
                    nodeHija.put("nombre", aulaHija.getNombre());
                    arrayHijas.add(nodeHija);
                }
                node.set("aulasHijas", arrayHijas);
                
                ArrayNode inventariosHijas = new ArrayNode(jFactory);
                List<ResumenInventario> inventarios = aula.getInventario();
                logger.debug("aula {} items {}", aula.getId(), inventarios != null ? inventarios.size() : 0);
                if (inventarios != null) {
                    for (ResumenInventario inventario : inventarios) {
                        ObjectNode jinventario = JsonHelper.createJson(inventario, jFactory, new String[]{"*", "producto.*"});
                        inventariosHijas.add(jinventario);
                    }
                }
                node.set("inventarios", inventariosHijas);
                node.put("cantidadinventarios", inventariosHijas.size());
                
                array.add(node);
            }
            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());
            
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

//    @ResponseBody
//    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Aula> aulas = service.allByDynatable(filter);
            
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            
            for (Aula aula : aulas) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                
                node.put("id", aula.getId());
                node.put("codigo", aula.getCodigo());
                node.put("nombre", aula.getNombre());
                node.put("tipoAmbienteEnum", aula.getTipoAmbienteEnum().getValue());
                node.put("piso", aula.getPiso());
                node.put("pisos", aula.getPisos());
                node.put("aforo", aula.getAforo());
                node.put("pabellon", (String) ObjectUtil.getParentTree(aula, "aulaSuperior.nombre"));
                node.put("capacidad", aula.getCapacidadAula());
                node.put("tipoAmbiente", aula.getTipoAmbiente());
                node.put("aulasContenido", aula.getAulasContenido().size());
                ObjectNode objSede = JsonHelper.createJson(aula.getSede(), JsonNodeFactory.instance, new String[]{
                    "*"
                });
                node.set("sede", objSede);
                ObjectNode objTipoAula = JsonHelper.createJson(aula.getTipoAula(), JsonNodeFactory.instance, new String[]{
                    "*"
                });
                node.set("tipoAula", objTipoAula);
                ObjectNode objOficina = JsonHelper.createJson(aula.getOficinaSupervisora(), JsonNodeFactory.instance, new String[]{
                    "*"
                });
                node.set("gestor", objOficina);
                node.put("estado", aula.getEstado());
                node.put("estadoEnum", aula.getEstadoEnum().getValue());
                node.put("motivo", aula.getMotivoAnulacion());
                
                ArrayNode arrayHijas = new ArrayNode(JsonNodeFactory.instance);
                List<Aula> aulasHijas = aula.getAulasContenido();
                for (Aula aulaHija : aulasHijas) {
                    ObjectNode nodeHija = new ObjectNode(JsonNodeFactory.instance);
                    nodeHija.put("codigo", aulaHija.getCodigo());
                    nodeHija.put("nombre", aulaHija.getNombre());
                    arrayHijas.add(nodeHija);
                }
                node.set("aulasHijas", arrayHijas);
                array.add(node);
            }
            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());
            
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }
    
    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Aula aula = new Aula();
        
        model.addAttribute("aula", aula);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("tiposAmbiente", TipoAmbienteEnum.values());
        model.addAttribute("tiposAula", service.allTiposAula());
        model.addAttribute("tiposCarpeta", service.allTipoCarpeta());
        model.addAttribute("sedes", service.allSedes());
        return "general/aula/aulaForm";
    }
    
    @RequestMapping("save")
    public String save(Aula aula, RedirectAttributes redirectAttr, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            String mensaje = aula.getId() != null ? Messages.UPDATED : Messages.CREATED;
            if (aula.getId() == null) {
                logger.debug(" tipo carpeta  {}", aula.getTipoCarpeta().getId());
                logger.debug(" tipo AMBIENTE  {}", aula.getTipoAmbiente());
                
                service.save(aula, ds.getUsuario());
            } else {
                service.update(aula, ds.getUsuario());
            }
            Notificaciones.crearMsg(mensaje, redirectAttr);
            
        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
            
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            
        }
        return "redirect:/general/aula";
    }
    
    @RequestMapping("editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        Aula aula = service.findAulaById(id);
        
        model.addAttribute("aula", aula);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("tiposAmbiente", TipoAmbienteEnum.values());
        model.addAttribute("tiposAula", service.allTiposAula());
        model.addAttribute("tiposCarpeta", service.allTipoCarpeta());
        model.addAttribute("sedes", service.allSedes());
        return "general/aula/aulaForm";
    }
    
    @ResponseBody
    @RequestMapping("allAulasSuperiores")
    public JsonResponse allAulasSuperiores(@RequestParam("nombre") String nombre, HttpSession session) {
        
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        
        try {
            List<Aula> aulasSuperiores = service.allAulasSuperioresByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            
            for (Aula aula : aulasSuperiores) {
                ObjectNode json = new ObjectNode(jsonFactory);
                
                json.put("id", aula.getId());
                json.put("nombre", aula.getNombre());
                
                jsonList.add(json);
                
            }
            
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("allGestores")
    public JsonResponse allGestores(@RequestParam("nombre") String nombre, HttpSession session) {
        
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        
        try {
            List<Oficina> gestores = service.allOficinasByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            
            for (Oficina gestor : gestores) {
                ObjectNode json = new ObjectNode(jsonFactory);
                
                json.put("id", gestor.getId());
                json.put("nombre", gestor.getNombre());
                json.put("codigo", gestor.getCodigo());
                
                jsonList.add(json);
                
            }
            
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("cambioEstado")
    public JsonResponse cambioEstadoOrientacionCarrera(Aula aula, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.cambioEstado(aula, ds);
            
            response.setMessage("Se cambio de estado satisfactoriamente.");
            response.setSuccess(true);
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("eliminar")
    public JsonResponse eliminar(Aula aula, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.eliminarAula(aula, ds);
            
            response.setMessage("Se cambio de estado satisfactoriamente.");
            response.setSuccess(true);
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, Messages.FK_ERROR);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping("loadModalAulaHorario")
    public JsonResponse loadModalAulaHorario(Aula aulaForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        
        try {
            
            JsonNodeFactory factory = JsonNodeFactory.instance;
            
            Aula aula = service.findAulaFull(aulaForm);
            
            List<Dia> dias = service.allDia();
            List<Hora> horasEncontradas = new ArrayList<>();
            for (HorarioAula horarioAula : aula.getHorariosAula()) {
                if (!horasEncontradas.contains(horarioAula.getHora())) {
                    horasEncontradas.add(horarioAula.getHora());
                }
            }
            
            Collections.sort(horasEncontradas, (p1, p2) -> p1.getNumero().compareTo(p2.getNumero()));
            
            ObjectNode data = new ObjectNode(factory);
            ArrayNode diasJson = new ArrayNode(factory);
            
            for (Dia dia : dias) {
                diasJson.add(JsonHelper.createJson(dia, factory));
            }
            
            List<HorarioAula> horarios = aula.getHorariosAula();
            
            Map<String, HorarioAula> diasHoras = horarios.stream().collect(Collectors.toMap(x -> x.getHora().getId() + "-" + x.getDia().getId(), x -> x, (f, s) -> s));
            
            for (Hora horasEncontrada : horasEncontradas) {
                List<Dia> diass = new ArrayList();
                for (Dia dia : dias) {
                    Dia diaClone = dia.clone();
                    diaClone.setMainHorarioAula(null);
                    String key = horasEncontrada.getId() + "-" + dia.getId();
                    HorarioAula horarioAula = diasHoras.get(key);
                    diaClone.setMainHorarioAula(horarioAula);
                    diass.add(diaClone);
                }
                horasEncontrada.setDias(diass);
            }
            
            ArrayNode horasJson = new ArrayNode(factory);
            for (Hora horasEncontrada : horasEncontradas) {
                ObjectNode jhora = JsonHelper.createJson(horasEncontrada, factory, true,
                        new String[]{
                            "*",
                            "dias.*",
                            "dias.mainHorarioAula.*",
                            "dias.mainHorarioAula.seccion.codigo2",
                            "dias.mainHorarioAula.seccion.sizeDocente",
                            "dias.mainHorarioAula.seccion.grupoSeccion.fechaInicioPeriodo",
                            "dias.mainHorarioAula.seccion.grupoSeccion.fechaFinPeriodo",
                            "dias.mainHorarioAula.seccion.grupoSeccion.tipoDictado",
                            "dias.mainHorarioAula.seccion.grupoSeccion.curso.codigo",
                            "dias.mainHorarioAula.seccion.grupoSeccion.curso.nombre",
                            "dias.mainHorarioAula.seccion.grupoSeccion.curso.tpc",
                            "dias.mainHorarioAula.seccion.docenteSeccion.docente.codigo",
                            "dias.mainHorarioAula.seccion.docenteSeccion.docente.persona.nomPaternoMat",
                            "dias.mainHorarioAula.seccion.docenteSeccion.docente.persona.apellidosNombres",
                            "dias.mainHorarioAula.seccion.grupoHoras.codigo",
                            "dias.mainHorarioAula.reservaAula.estado",
                            "dias.mainHorarioAula.reservaAula.motivo",
                            "dias.mainHorarioAula.reservaAula.tramite.numero",
                            "dias.mainHorarioAula.reservaAula.tramite.serie",
                            "dias.mainHorarioAula.reservaAula.tramite.tipoSolicitante",
                            "dias.mainHorarioAula.reservaAula.tramite.empresa.razonSocial",
                            "dias.mainHorarioAula.reservaAula.tramite.empresa.numeroDocIdentidad",
                            "dias.mainHorarioAula.reservaAula.tramite.docente.codigo",
                            "dias.mainHorarioAula.reservaAula.tramite.docente.persona.nombreCompleto",
                            "dias.mainHorarioAula.reservaAula.tramite.alumno.codigo",
                            "dias.mainHorarioAula.reservaAula.tramite.alumno.persona.nombreCompleto",
                            "dias.mainHorarioAula.reservaAula.tramite.oficina.nombre",
                            "dias.mainHorarioAula.reservaAula.tramite.oficina.codigo",});
                horasJson.add(jhora);
            }
            
            ObjectNode jaula = JsonHelper.createJson(aula, factory, true,
                    new String[]{
                        "id",
                        "codigo",
                        "nombre",
                        "capacidadAula",
                        "tipoAmbienteEnum",
                        "tipoAula.id",
                        "tipoAula.nombre",
                        "oficinaSupervisora.id",
                        "oficinaSupervisora.nombre",
                        "aulaSuperior.id",
                        "aulaSuperior.nombre"
                    });
            
            data.set("aula", jaula);
            data.set("dias", diasJson);
            data.set("horas", horasJson);
            
            response.setData(data);
            response.setSuccess(true);
            
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
    
    @RequestMapping("generatorpdf")
    public ModelAndView generatorpdf(HorariosAulaPDFBean horariosAulaPdfBean, Model model, HttpSession session, HttpServletResponse response) throws Exception {
        
        logger.debug("******** fin {}", horariosAulaPdfBean.getFechaFin());
        logger.debug("******** inicio {}", horariosAulaPdfBean.getFechaInicio());
        
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        
        Aula aulaForm = new ObjectMapper().readValue(horariosAulaPdfBean.getStrAula(), Aula.class);
        aulaForm.setFechaFin(horariosAulaPdfBean.getFechaFin());
        aulaForm.setFechaInicio(horariosAulaPdfBean.getFechaInicio());
        
        Aula aulaSuperiorForm = new ObjectMapper().readValue(horariosAulaPdfBean.getStrAulaSuperior(), Aula.class);
        List<Dia> dias = service.allDiaForPrinter();
        
        ObjectUtil.printAttr(aulaForm);
        ObjectUtil.printAttr(aulaSuperiorForm);
        
        Aula aulaBD = service.findAulaFull(aulaForm);
        List<DiaHoraGrupo> listdiaHoraGrupo = service.allDiaHoraGrupoByCicloRegular(ds.getCicloAcademico());
        
        List<Hora> horasEncontradas = new ArrayList<>();
        for (HorarioAula horarioAula : aulaBD.getHorariosAula()) {
            if (!horasEncontradas.contains(horarioAula.getHora())) {
                horasEncontradas.add(horarioAula.getHora());
            }
        }
        
        Collections.sort(horasEncontradas, (horas1, horas2) -> horas1.getNumero().compareTo(horas2.getNumero()));
        
        List<HorarioAula> horarios = aulaBD.getHorariosAula();
        
        Map<String, HorarioAula> diasHorasMap = horarios.stream().collect(Collectors.toMap(x -> x.getHora().getId() + "-" + x.getDia().getId(), x -> x, (f, s) -> s));
        
        Map<String, DiaHoraGrupo> diaHoraGrupoMap = listdiaHoraGrupo.stream().collect(Collectors.toMap(x -> x.getHora().getId() + "-" + x.getDia().getId(), x -> x, (f, s) -> s));
        
        for (Hora horasEncontrada : horasEncontradas) {
            List<Dia> diass = new ArrayList();
            for (Dia dia : dias) {
                Dia diaClone = dia.clone();
                diaClone.setMainHorarioAula(null);
                diaClone.setGrupohoras(null);
                String key = horasEncontrada.getId() + "-" + dia.getId();
                HorarioAula horarioAula = diasHorasMap.get(key);
                DiaHoraGrupo diaHoraGrupo = diaHoraGrupoMap.get(key);
                diaClone.setMainHorarioAula(horarioAula);
                if (diaHoraGrupo != null) {
                    ObjectUtil.printAttr(diaHoraGrupo.getGrupoHorario());
                    diaClone.setGrupohoras(diaHoraGrupo.getGrupoHorario());
                }
                diass.add(diaClone);
            }
            horasEncontrada.setDias(diass);
        }
        
        model.addAttribute("aula", aulaBD);
        model.addAttribute("aulaSuperior", aulaSuperiorForm);
        model.addAttribute("dias", dias);
        model.addAttribute("horas", horasEncontradas);
        model.addAttribute("fechaFin", horariosAulaPdfBean.getFechaFin());
        model.addAttribute("fechaInicio", horariosAulaPdfBean.getFechaInicio());
        
        return new ModelAndView(pdfHorariosAula);
    }
    
    @RequestMapping("horarios")
    public String oficinas(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        CicloAcademico ciclo = ds.getCicloAcademico();
        
        List<Aula> listAulaSuperior = service.allAulaByOficinaSuperior(ds);
        List<Aula> listAula = service.allAulaByAulaSuperior(listAulaSuperior);
        
        model.addAttribute("oficinas", createOficinasJSON(ds.getOficinas()).toString());
        model.addAttribute("listAulaSuperior", createListAulaJSON(listAulaSuperior).toString());
        model.addAttribute("listAula", createListAulaJSON(listAula).toString());
        model.addAttribute("ciclo", ciclo);
        
        return "general/aula/horarios";
    }
    
    private ArrayNode createOficinasJSON(List<Oficina> oficinas) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Oficina oficina : oficinas) {
            ObjectNode node = JsonHelper.createJson(oficina, JsonNodeFactory.instance, true, new String[]{
                "id", "nombre", "codigo"
            });
            array.add(node);
        }
        return array;
    }
    
    private ArrayNode createListAulaJSON(List<Aula> listAula) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Aula aula : listAula) {
            ObjectNode node = JsonHelper.createJson(aula, JsonNodeFactory.instance, true, new String[]{
                "id", "nombre", "codigo", "aulaSuperior.id"
            });
            array.add(node);
        }
        return array;
    }
    
}
