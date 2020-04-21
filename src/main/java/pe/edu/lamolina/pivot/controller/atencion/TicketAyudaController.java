package pe.edu.lamolina.pivot.controller.atencion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.atencion.MensajeTicketAyuda;
import pe.edu.lamolina.model.atencion.TicketAyuda;
import pe.edu.lamolina.model.atencion.TrasladoAtencionTicket;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.constant.Messages;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("atencion/ticket")
public class TicketAyudaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TicketAyudaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Oficina oficina = ds.getOficinaMain();
        logger.debug("OFICINA MAIN {}",oficina.getId());
        model.addAttribute("resumen", service.findResumen(oficina));
        return "atencion/atencion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Oficina oficina = ds.getOficinaMain();

            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            List<MensajeTicketAyuda> tickets = service.allByDynatable(filter, oficina);

            ArrayNode array = new ArrayNode(jFactory);

            for (MensajeTicketAyuda mensaje : tickets) {

                ObjectNode node = JsonHelper.createJson(mensaje, jFactory, true, new String[]{
                    "*",
                    "ticketAyuda.*",
                    "ticketAyuda.oficina.*",
                    "ticketAyuda.persona.*",
                    "ticketAyuda.colaborador.*",
                    "ticketAyuda.colaborador.persona.*"
                });

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
        return "atencion/ticket";
    }

    @ResponseBody
    @RequestMapping("saverespuesta")
    public JsonResponse saverespuesta(MensajeTicketAyuda mensaje, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            MensajeTicketAyuda mensajeDb = service.saverespuesta(mensaje, ds);

            ObjectNode node = JsonHelper.createJson(mensajeDb, jFactory, true, new String[]{
                "*"
            });

            response.setData(node);
            response.setMessage(GlobalMessages.CREATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @RequestMapping("{idticket}/respuesta")
    public String respuesta(@PathVariable("idticket") Long idticket, Model model, HttpSession session) {
        model.addAttribute("idticket", idticket);
        return "atencion/respuesta";
    }

    @ResponseBody
    @RequestMapping("find")
    public JsonResponse find(TicketAyuda ticketForm, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ObjectNode data = new ObjectNode(jFactory);

            TicketAyuda ticket = service.find(ticketForm);

            ObjectNode jTicket = JsonHelper.createJson(ticket, jFactory, true, new String[]{
                "*",
                "persona.*",
                "oficina.*",
                "colaborador.*",
                "mensajeTicketAyuda.*",
                "mensajeTicketAyuda.archivos.*",
                "mensajesTicketAyuda.*"});

            if (ticket.getColaborador() == null) {

                List<Colaborador> colaboradores = service.allColaboradorByOficina(ticket.getOficina());
                ArrayNode arrayColaboradores = new ArrayNode(jFactory);

                for (Colaborador colaboradore : colaboradores) {

                    ObjectNode jColaborador = JsonHelper.createJson(colaboradore, jFactory, true, new String[]{
                        "*",
                        "persona.*",
                        "oficina.*",
                        "cargo.*"
                    });

                    arrayColaboradores.add(jColaborador);

                }

                jTicket.set("colaboradores", arrayColaboradores);

            }

            jTicket.put("basepath", Constantine.S3_LINK + Constantine.S3_DIR_ARCHIVO_ATENCION);

            data.set("ticket", jTicket);

            response.setMessage(Messages.APPROVED);
            response.setData(data);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("upload")
    public JsonResponse upload(@RequestParam("file") MultipartFile archivo, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ObjectNode json = new ObjectNode(jsonFactory);

            String fileExt = TypesUtil.getClean(FilenameUtils.getExtension(archivo.getOriginalFilename())).toLowerCase();
            String fileName = TypesUtil.getUnixTime() + "." + fileExt;
            String absoluteName = GlobalConstantine.TMP_DIR + fileName;

            logger.debug("guardando imagen ...");

            FileHelper.saveToDisk(archivo, absoluteName);

            json.put("ruta", fileName);
            json.put("tipo", TypesUtil.getClean(FilenameUtils.getExtension(archivo.getOriginalFilename())));
            json.put("size", archivo.getSize());
            json.put("nombre", archivo.getOriginalFilename());

            response.setData(json);
            response.setSuccess(true);
            response.setMessage("Carga satisfactoria del archivo");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("alloficina")
    public JsonResponse alloficina(HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ObjectNode data = new ObjectNode(jFactory);

            List<Oficina> oficinas = service.allOficinaTicketAyuda();
            logger.debug("CANTIDAD OFICINAS {}", oficinas.size());

            ArrayNode jOficinas = new ArrayNode(jFactory);

            for (Oficina oficina : oficinas) {
                ObjectNode node = JsonHelper.createJson(oficina, jFactory, true, new String[]{
                    "*"
                });
                jOficinas.add(node);
            }

            data.set("oficinas", jOficinas);

            response.setMessage(Messages.APPROVED);
            response.setData(data);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("asignarme")
    public JsonResponse asignarme(TicketAyuda ticket, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.asignarme(ticket, ds);

            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("asignarColaborador")
    public JsonResponse asignarColaborador(TicketAyuda ticket, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            service.asignarColaborador(ticket, ds);

            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("savenota")
    public JsonResponse savenota(MensajeTicketAyuda mensaje, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            MensajeTicketAyuda mensajeDb = service.saverespuesta(mensaje, ds);

            ObjectNode node = JsonHelper.createJson(mensajeDb, jFactory, true, new String[]{
                "*"
            });

            response.setData(node);
            response.setMessage(GlobalMessages.CREATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("findoficina")
    public JsonResponse findoficina(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Compania compania = ds.getCompania();

            List<Oficina> oficinas = service.findoficina(nombre, compania);
            ArrayNode array = new ArrayNode(jsonFactory);
            for (Oficina oficina : oficinas) {
                ObjectNode a = new ObjectNode(jsonFactory);
                a.put("id", oficina.getId());
                a.put("codigo", oficina.getCodigo());
                a.put("nombre", oficina.getNombre());
                array.add(a);
            }

            response.setData(array);
            response.setTotal(array.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("trasladooficina")
    public JsonResponse trasladooficina(TrasladoAtencionTicket traslado, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.trasladooficina(traslado, ds);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("trasladocolaborador")
    public JsonResponse trasladocolaborador(TrasladoAtencionTicket traslado, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.trasladocolaborador(traslado, ds);
            response.setMessage(GlobalMessages.UPDATED);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allcolaborador")
    public JsonResponse allcolaborador(TicketAyuda ticketForm, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jFactory = JsonNodeFactory.instance;
            ObjectNode data = new ObjectNode(jFactory);

            TicketAyuda ticket = service.find(ticketForm);

            List<Colaborador> colaboradores = service.allColaboradorByOficina(ticket.getOficina());
            ArrayNode arrayColaboradores = new ArrayNode(jFactory);

            for (Colaborador colaboradore : colaboradores) {

                ObjectNode jColaborador = JsonHelper.createJson(colaboradore, jFactory, true, new String[]{
                    "*",
                    "persona.*",
                    "oficina.*",
                    "cargo.*"
                });

                arrayColaboradores.add(jColaborador);

            }

            data.set("colaboradores", arrayColaboradores);

            response.setMessage(Messages.APPROVED);
            response.setData(data);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
