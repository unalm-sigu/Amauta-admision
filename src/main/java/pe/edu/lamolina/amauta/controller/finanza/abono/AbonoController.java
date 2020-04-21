package pe.edu.lamolina.amauta.controller.finanza.abono;

//package pe.edu.lamolina.pivot.controller.finanza.abono;
//
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.JsonNodeFactory;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import java.beans.PropertyEditorSupport;
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import javax.servlet.http.HttpSession;
//import org.joda.time.DateTime;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.InitBinder;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//import pe.albatross.octavia.dynatable.DynatableFilter;
//import pe.albatross.octavia.dynatable.DynatableResponse;
//import pe.albatross.zelpers.miscelanea.ExceptionHandler;
//import pe.albatross.zelpers.miscelanea.JsonResponse;
//import pe.albatross.zelpers.miscelanea.NumberFormat;
//import pe.albatross.zelpers.miscelanea.ObjectUtil;
//import pe.albatross.zelpers.miscelanea.PhobosException;
//import pe.edu.lamolina.pivot.model.general.Observado;
//import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
//import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
//import pe.edu.lamolina.model.inscripcion.CicloPostula;
//import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
//
//@Controller
//@RequestMapping("facturacion/abono")
//public class AbonoController {
//
//    @Autowired
//    AbonoService service;
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @InitBinder
//    public void initBinder(WebDataBinder dataBinder) {
//
//        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
//            @Override
//            public void setAsText(String value) {
//                try {
//                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
//                } catch (ParseException e) {
//                    setValue(null);
//                }
//            }
//        });
//
//        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
//            @Override
//            public void setAsText(String value) {
//                try {
//                    setValue(new BigDecimal(value.replaceAll(",", "")));
//                } catch (Exception e) {
//                    setValue(null);
//                }
//            }
//        });
//    }
//
//    @RequestMapping(method = RequestMethod.GET)
//    public String index(Model model, HttpSession session) {
//        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
//        model.addAttribute("ciclo", ds.getCicloAcademico());
//        return "facturacion/abono/abono";
//    }
//
////    @ResponseBody
////    @RequestMapping("list")
////    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
////        DynatableResponse json = new DynatableResponse();
////        try {
////            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
////            List<ItemCargaAbono> abonosPostulante = service.allAbonosByPostulante(ds.getCicloPostula(), filter);
////            NumberFormat format = new NumberFormat();
////            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
////            for (ItemCargaAbono abono : abonosPostulante) {
////                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
////
////                node.put("id", abono.getId());
////                node.put("postulante", (String) ObjectUtil.getParentTree(abono, "postulante.persona.nombreCompleto"));
////                node.put("ciclo", (String) ObjectUtil.getParentTree(abono, "postulante.cicloPostula.cicloAcademico.descripcion"));
////                node.put("codigo", (String) ObjectUtil.getParentTree(abono, "postulante.codigo"));
////                node.put("modalidad", (String) ObjectUtil.getParentTree(abono, "postulante.modalidadIngreso.nombre"));
////                node.put("descripcion", abono.getDescripcion());
////                node.put("numeroCtaBancaria", abono.getCuentaBancaria().getNumero());
////                node.put("nombreCtaBancaria", abono.getCuentaBancaria().getNombre());
////                node.put("voucher", abono.getNumeroOperacion());
////                node.put("sucursal", abono.getSucursal());
////                node.put("userBanco", abono.getUsuarioBanco());
////                node.put("fecha", new DateTime(abono.getFechaAbono()).toString("dd/MM/yyyy"));
////                node.put("importe", format.precio(abono.getImporte()));
////                node.put("estado", abono.getExtornado() == 1 ? "Extornado" : "Correcto");
////                node.put("extornado", abono.getExtornado());
////                array.add(node);
////            }
////
////            json.setData(array);
////            json.setTotal(filter.getTotal());
////            json.setFiltered(filter.getFiltered());
////
////        } catch (Exception e) {
////            e.printStackTrace();
////            json.setTotal(0);
////        }
////
////        return json;
////    }
//    @RequestMapping("loadArchivoDiario")
//    public String loadArchivoDiario(Model model, HttpSession session) {
//        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
////        model.addAttribute("ciclo", ds.getCicloPostula());
//        return "facturacion/abono/loadArchivoDiario";
//    }
//
//    @RequestMapping("loadArchivoHistorico")
//    public String loadArchivoHistorico(Model model, HttpSession session) {
//        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
////        model.addAttribute("ciclo", ds.getCicloPostula());
//        return "facturacion/abono/loadArchivoHistorico";
//    }
//
//    @ResponseBody
//    @RequestMapping("uploadArchivoDiario")
//    public JsonResponse uploadArchivoDiario(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
//        JsonResponse json = new JsonResponse();
//
//        try {
//            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
////            List<Observado> observados = service.loadArchivoDiario(file, ds.getCicloPostula(), ds);
//            List<Observado> observados = service.loadArchivoDiario(file, ds);
//
//            json.setSuccess(true);
//            if (!observados.isEmpty()) {
//                ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
//                for (Observado cargaAbono : observados) {
//                    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
//                    node.put("operacion", cargaAbono.getOperacion());
//                    node.put("descripcion", cargaAbono.getDescripcion());
//                    array.add(node);
//                }
//                json.setMessage("No se ha conseguido relacionar " + observados.size() + " registro(s) a postulantes.");
//                json.setData(array);
//            } else {
//                json.setMessage("Carga de pagos finalizada.");
//            }
//
//        } catch (PhobosException e) {
//            ExceptionHandler.handlePhobosEx(e, json);
//        } catch (Exception e) {
//            ExceptionHandler.handleException(e, json);
//
//        } finally {
//            return json;
//        }
//
//    }
//
////    @ResponseBody
////    @RequestMapping("uploadArchivoHistorico")
////    public JsonResponse uploadArchivoHistorico(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
////        JsonResponse json = new JsonResponse();
////
////        try {
////            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
////            List<Observado> observados = service.loadArchivoHistorico(file, ds.getCicloPostula(), ds);
////
////            json.setSuccess(true);
////            if (!observados.isEmpty()) {
////                ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
////                for (Observado cargaAbono : observados) {
////                    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
////                    node.put("operacion", cargaAbono.getOperacion());
////                    node.put("descripcion", cargaAbono.getDescripcion());
////                    array.add(node);
////                }
////                json.setMessage("No se ha conseguido relacionar " + observados.size() + " registro(s) a postulantes.");
////                json.setData(array);
////            } else {
////                json.setMessage("Carga de pagos finalizada.");
////            }
////
////        } catch (PhobosException e) {
////            ExceptionHandler.handlePhobosEx(e, json);
////        } catch (Exception e) {
////            ExceptionHandler.handleException(e, json);
////
////        } finally {
////            return json;
////        }
////
////    }
////
////    @ResponseBody
////    @RequestMapping("asginaPostulante")
////    public JsonResponse asginaPostulante(ItemCargaAbono itemCargaAbono, HttpSession session) {
////
////        JsonResponse response = new JsonResponse();
////        try {
////            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
////            CicloPostula ciclo = ds.getCicloPostula();
////            service.asignarPostulante(itemCargaAbono, ciclo, ds);
////            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
////            response.setData(node);
////            response.setSuccess(true);
////            response.setMessage("Postulante asignado");
////        } catch (PhobosException e) {
////            ExceptionHandler.handlePhobosEx(e, response);
////        } catch (Exception e) {
////            ExceptionHandler.handleException(e, response);
////        } finally {
////            return response;
////        }
////    }
////
////    @RequestMapping("extornoHtml")
////    public String requisitos(
////            ItemCargaAbono form, Model model) {
////        List<ItemCargaAbono> extornados = service.allExtornados(form);
////        model.addAttribute("extornador", form);
////        model.addAttribute("extornados", extornados);
////        return "facturacion/abono/extorno";
////    }
////
////    @ResponseBody
////    @RequestMapping("reasignarExtorno")
////    public JsonResponse reasignarExtorno(ItemCargaAbono extornado, HttpSession session) {
////        JsonResponse response = new JsonResponse();
////        try {
////            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
////            service.reasignarExtorno(extornado, ds);
////            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
////            response.setData(node);
////            response.setSuccess(true);
////            response.setMessage("Extorno reasignado");
////        } catch (PhobosException e) {
////            ExceptionHandler.handlePhobosEx(e, response);
////        } catch (Exception e) {
////            ExceptionHandler.handleException(e, response);
////        } finally {
////            return response;
////        }
////    }
//}
