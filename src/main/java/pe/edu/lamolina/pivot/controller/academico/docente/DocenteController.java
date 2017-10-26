package pe.edu.lamolina.pivot.controller.academico.docente;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.controller.general.foto.FotoHelper;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.Facultad;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Persona;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/docente")
public class DocenteController {

    @Autowired
    DocenteService service;

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
    public String index(Model model) {

        return "academico/docente/docente/docente";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse allDocente(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            FotoHelper helper = new FotoHelper();
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            List<Docente> docentes = service.allByDynatable(filter);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Docente docente : docentes) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                Persona persona = docente.getPersona();
                DepartamentoAcademico da = docente.getDepartamentoAcademico();
                Facultad fa = da.getFacultad();

                node.put("id", docente.getId());
                node.put("codigo", docente.getCodigo());
                node.put("estado", docente.getEstado());

                node.put("nombre", persona.getApellidosNombres());
                node.put("simbolo", persona.getTipoDocumento().getSimbolo());
                node.put("tipodocid", persona.getTipoDocumento().getId());
                node.put("documento", persona.getNumeroDocIdentidad());
                node.put("telefono", persona.getTelefono());
                node.put("celular", persona.getCelular());
                node.put("email", persona.getEmail());
                node.put("emailEmpresa", persona.getEmailCompania());
                node.put("rutaFoto", helper.getRutaFoto(null, persona.getSexo()));

                node.put("facultad", fa.getNombre());
                node.put("departamentoAcademico", da.getNombre());
                node.put("situacion", "situacion");

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

    @RequestMapping("info")
    public String info(@RequestParam("docente") Long idDocente, Model model) {
        model.addAttribute("docente", service.find(new Docente(idDocente)));
        model.addAttribute("fotoHelper", new FotoHelper());
        return "academico/docente/docente/docenteInfo";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();

        Docente docente = new Docente();
        docente.setPersona(new Persona());
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("modalidades", service.allModalidadEstudio(compania));
        model.addAttribute("docente", docente);
        return "academico/docente/docente/docenteForm";

    }

    @RequestMapping("{docente}/update")
    public String update(@PathVariable("docente") Long idDocente, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();

        Docente docente = service.find(new Docente(idDocente));
        model.addAttribute("docente", docente);
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("fotoHelper", new FotoHelper());
        model.addAttribute("modalidades", service.allModalidadEstudio(compania));
        return "academico/docente/docente/docenteForm";

    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(Docente docente, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            response.setMessage("Docente modificado satisfactoriamente");
            if (docente.getId() == null) {
                response.setMessage("Docente creado satisfactoriamente");
            }

            service.save(docente, ds);

            response.setSuccess(true);
            response.setData(node);

            node.put("personaId", docente.getId());
            node.put("nombreCompleto", docente.getPersona().getApellidosNombres());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("validarEmail")
    public JsonResponse validarEmail(@RequestParam("email") String email, @RequestParam("docente") Long idDocente) {

        JsonResponse response = new JsonResponse();
        try {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String msg = service.validarEmailByDocente(email, new Docente(idDocente));

            node.put("respuesta", msg);
            response.setData(node);
            response.setSuccess(StringUtils.isEmpty(msg));

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("validarEmailEmpresa")
    public JsonResponse validarEmailEmpresa(@RequestParam("email") String email, @RequestParam("docente") Long idDocente) {

        JsonResponse response = new JsonResponse();

        try {

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            String msg = service.validarEmailEmpresaByDocente(email, new Docente(idDocente));
            node.put("respuesta", msg);
            response.setData(node);
            response.setSuccess(StringUtils.isEmpty(msg));

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("estado")
    public JsonResponse estado(Docente docente) {

        JsonResponse response = new JsonResponse();

        try {

            service.estado(docente);
            response.setMessage("Docente actualizado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("findPersona")
    public JsonResponse findPersona(Docente docente, HttpSession session) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            Persona persona = service.findPersona(docente.getPersona());
            node.put("existePersona", (persona != null));
            if (persona != null) {
                node.put("name", persona.getNombreCompleto());
                Docente docenteDb = service.findDocenteByPersona(persona);
                node.put("existeDocente", (docenteDb != null));
                node.put("docModalidad", docenteDb.getModalidadEstudio().getId());
                node.put("docModalidadName", docenteDb.getModalidadEstudio().getNombre());
                node.put("docDepartamento", docenteDb.getDepartamentoAcademico().getId());
                node.put("docDepartamentoName", docenteDb.getDepartamentoAcademico().getNombre());
                node.put("docDepartamentoCodigo", docenteDb.getDepartamentoAcademico().getCodigo());
            }
            response.setData(node);
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
    public JsonResponse upload(@RequestParam("file") MultipartFile archivo,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ObjectNode json = new ObjectNode(jsonFactory);

            String fileExt = TypesUtil.getClean(FilenameUtils.getExtension(archivo.getOriginalFilename())).toLowerCase();
            String fileName = TypesUtil.getUnixTime() + "." + fileExt;
            String absoluteName = Constantine.TMP_DIR + fileName;
            FileHelper.saveToDisk(archivo, absoluteName);
            json.put("name", archivo.getOriginalFilename());
            json.put("ruta", fileName);
            json.put("mime", TypesUtil.getClean(FilenameUtils.getExtension(archivo.getOriginalFilename())));
            json.put("size", archivo.getSize());
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

    @RequestMapping("view/{file:.*}")
    public void view(@PathVariable String file, HttpServletResponse response) throws Exception {

        String fileNameRoot = Constantine.AVATAR_DIR + file;

        File filex = new File(fileNameRoot);
        if (!filex.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.reset();
        response.setBufferSize(Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"" + file + "\"");

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {

            input = new BufferedInputStream(new FileInputStream(filex), Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            output = new BufferedOutputStream(response.getOutputStream(), Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            IOUtils.copy(input, output);
            response.flushBuffer();

        } finally {

            close(output);
            close(input);

        }
    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
