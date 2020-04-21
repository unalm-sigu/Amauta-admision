package pe.edu.lamolina.amauta.controller.academico.convenio;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraConvenio;
import pe.edu.lamolina.model.academico.ConvenioBeca;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/convenio")
public class ConvenioController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConvenioService service;

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
        return "academico/convenio/convenio";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            CicloAcademico cicloAcademico = ds.getCicloAcademico();
            logger.debug("cicloAcademico {} {}", cicloAcademico.getId(), cicloAcademico.getDescripcion());

            List<ConvenioBeca> convenios = service.allByDynatable(filter);
            Map<Long, List<CarreraConvenio>> carreraConveniosMap = service.allByCarreraConvenio(convenios);

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ArrayNode array = new ArrayNode(jsonFactory);

            for (ConvenioBeca convenio : convenios) {

                ObjectNode node = new ObjectNode(jsonFactory);

                node.put("id", convenio.getId());
                node.put("nombre", convenio.getNombre());
                node.put("descripcion", convenio.getDescripcion());

                node.put("pais", convenio.getPais() != null ? convenio.getPais().getNombre() : "");
                node.put("institucion", convenio.getInstitucion() != null ? convenio.getInstitucion().getRazonSocial() : "");
                node.put("estado", convenio.getEstado());
                String prettyName = convenio.getRutaDocumento();
                prettyName = prettyName.substring(20);
                prettyName = prettyName.replaceAll("[\\-]", " ");
                node.put("nameDocumento", prettyName);

                StringBuilder link = new StringBuilder();
                link.append(AcademicoConstantine.S3_URL_ACADEMICO);
                link.append(AcademicoConstantine.S3_DIR_CONVENIO);
                link.append(convenio.getRutaDocumento());

                node.put("linkDocumento", link.toString());

                node.put("inicio", convenio.getInicioVigencia() != null
                        ? new DateTime(convenio.getInicioVigencia()).toString("dd/MM/yyyy") : "");

                node.put("fin", convenio.getFinVigencia() != null
                        ? new DateTime(convenio.getFinVigencia()).toString("dd/MM/yyyy") : "");

                List<CarreraConvenio> carreraConvenios = carreraConveniosMap.get(convenio.getId());
                logger.debug("***cantidad {}", carreraConvenios.size());
                ArrayNode arrayCarrera = new ArrayNode(jsonFactory);
                for (CarreraConvenio carreraConvenio : carreraConvenios) {
                    ObjectNode objectCarrera = new ObjectNode(jsonFactory);
                    objectCarrera.put("id", carreraConvenio.getId());
                    objectCarrera.put("nombre", carreraConvenio.getCarrera().getNombreCorto());
                    arrayCarrera.add(objectCarrera);
                }
                node.put("carreras", arrayCarrera);
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
        ConvenioBeca convenioBeca = new ConvenioBeca();
        model.addAttribute("convenioBeca", convenioBeca);
        model.addAttribute("helper", new ConvenioHelper());
        return "academico/convenio/convenioform";
    }

    @RequestMapping("{convenioBeca}/update")
    public String update(@PathVariable("convenioBeca") Long idConvenioBeca, Model model, HttpSession session) {
        ConvenioBeca convenioBeca = service.findConvenioBeca(idConvenioBeca);
        model.addAttribute("convenioBeca", convenioBeca);
        model.addAttribute("helper", new ConvenioHelper());
        return "academico/convenio/convenioform";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(ConvenioBeca convenioBeca, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            if (convenioBeca.getId() == null) {
                service.save(convenioBeca, ds);
                response.setMessage("Convenio Beca guardado satisfactoriamente");
            } else {
                service.update(convenioBeca, ds);
                response.setMessage("Convenio Beca actualizado satisfactoriamente");
            }

            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(ConvenioBeca convenioBeca) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(convenioBeca);
            response.setMessage("Convenio Beca eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveInstitucion")
    public JsonResponse saveInstitucion(Empresa institucion, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ObjectNode node = new ObjectNode(jsonFactory);
            service.saveInstitucion(institucion);
            node.put("id", institucion.getId());
            node.put("razonSocial", institucion.getRazonSocial());
            node.put("paisUbicacion", (Long) ObjectUtil.getParentTree(institucion, "paisUbicacion.id"));
            response.setData(node);
            response.setMessage("Institucion guardada satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allCarrera")
    public JsonResponse allCarrera(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Compania cia = ds.getCompania();
            List<Carrera> carreras = service.allCarreraByName(nombre, cia);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Carrera carrera : carreras) {
                ModalidadEstudio modalidadEstudio = carrera.getModalidadEstudio();

                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", carrera.getId());
                json.put("nombre", carrera.getNombre());
                json.put("codigo", carrera.getCodigo());
                json.put("modalidad", modalidadEstudio.getNombre());
                if (modalidadEstudio.getCodigo().equalsIgnoreCase(ModalidadEstudioEnum.EPG.name())) {
                    json.put("tipo", carrera.getTipoEnum().getValue());
                }
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
    @RequestMapping("allCarrerasAfines")
    public JsonResponse allCarrerasAfines(@ModelAttribute("convenioBeca") ConvenioBeca convenioBeca, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            List<CarreraConvenio> carreras = service.allCarreraConvenioByConvenioBeca(convenioBeca);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (CarreraConvenio carreraConvenio : carreras) {
                Carrera carrera = carreraConvenio.getCarrera();
                ModalidadEstudio modalidad = carrera.getModalidadEstudio();
                ObjectNode json = new ObjectNode(jsonFactory);
                json.put("id", carrera.getId());
                json.put("nombre", carrera.getNombre());
                json.put("codigo", carrera.getCodigo());
                json.put("modalidad", modalidad.getNombre());
                if (modalidad.getCodigo().equalsIgnoreCase(ModalidadEstudioEnum.EPG.name())) {
                    json.put("tipo", carrera.getTipoEnum().getValue());
                }
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
    @RequestMapping("uploadFile")
    public JsonResponse uploadFile(@RequestParam("file") MultipartFile file) {
        JsonResponse response = new JsonResponse();
        try {

            String cleanName = service.getCleanName(file.getOriginalFilename());
            FileHelper.createDirectory(GlobalConstantine.TMP_DIR);
            String absoluteName = GlobalConstantine.TMP_DIR + cleanName;
            FileHelper.saveToDisk(file, absoluteName);

            response.setMessage("Importación finalizada.");
            response.setData(cleanName);
            response.setSuccess(true);

        } catch (IOException e) {
            response.setSuccess(false);
            response.setMessage(GlobalMessages.ERROR_GENERAL);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(GlobalMessages.ERROR_GENERAL);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("changeEstado")
    public JsonResponse changeEstado(@RequestParam("id") Long id) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);

        try {
            service.changeEstado(id);
            response.setMessage("Se cambio de estado.");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

}
