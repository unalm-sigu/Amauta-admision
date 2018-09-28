package pe.edu.lamolina.pivot.controller.academico.profesor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import org.thymeleaf.context.Context;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.spring4.SpringTemplateEngine;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PlanCalificacion;
import pe.edu.lamolina.model.academico.PlanCalificacionCurso;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.misc.FotoHelper;
import pe.edu.lamolina.pivot.controller.academico.notasacademicas.CargaAcademicaService;
import pe.edu.lamolina.pivot.controller.academico.visitante.AlumnoHelper;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/profesor")
public class ProfesorController {

    @Autowired
    ProfesorService service;

    @Autowired
    SpringTemplateEngine springHtml;

    @Autowired
    CargaAcademicaService cargaAcademicaService;

    @Autowired
    VerificadorService verificadorService;

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
        return "academico/profesor/profesor";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse allDocente(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        try {

            List<Facultad> facultades = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.FAC, request, ds);

            List<Docente> docentes;

            if (facultades.isEmpty()) {
                docentes = service.allByDynatable(filter, ds.getDepartamentos());
            } else {
                docentes = service.allByFacultadesDynatable(filter, facultades);
            }

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
                node.put("tipoDoc", (String) ObjectUtil.getParentTree(persona, "tipoDocumento.simbolo"));
                node.put("nroDocumento", persona.getNumeroDocIdentidad());
                node.put("telefono", persona.getTelefono());
                node.put("celular", persona.getCelular());
                node.put("email", persona.getEmail());
                node.put("emailEmpresa", persona.getEmailCompania());
                node.put("rutaFoto", persona.getRutaFoto());
                node.put("tipoFoto", persona.getTipoFoto());

                node.put("facultad", fa.getNombre());
                node.put("departamentoAcademico", da.getNombre());
                node.put("situacion", "Contratado");

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
        return "academico/profesor/profesorInfo";
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        Compania compania = ds.getCompania();

        Docente docente = new Docente();
        docente.setPersona(new Persona());
        model.addAttribute("documentos", service.allDocumentos());
        model.addAttribute("modalidades", service.allModalidadEstudioByCodes(Arrays.asList(ModalidadEstudioEnum.PRE, ModalidadEstudioEnum.EPG), compania));
        model.addAttribute("docente", docente);
        model.addAttribute("helper", new AlumnoHelper());
        return "academico/profesor/profesorForm";

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
        model.addAttribute("helper", new AlumnoHelper());
        return "academico/profesor/profesorForm";

    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(Docente docente, HttpSession session, RedirectAttributes redirectAttr) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        try {
            ObjectUtil.printAttr(docente);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            if (docente.getId() == null) {
                service.save(docente, ds);
                response.setMessage("Docente creado satisfactoriamente");
            } else {
                service.update(docente, ds);
                response.setMessage("Docente modificado satisfactoriamente");
            }

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
            Persona persona = service.findPersonaByDocIdentidad(docente.getPersona());
            Compania compania = ds.getCompania();

            node.put("existePersona", (persona != null));

            if (persona == null) {
                persona = new Persona();
            }

            node.put("name", persona.getApellidosNombres());
            Docente docenteDb = null;

            if (persona.getId() != null) {

                node.put("simboloDoc", persona.getTipoDocumento().getSimbolo());
                node.put("numeroDoc", persona.getNumeroDocIdentidad());
                if (docente.getId() != null) {
                    docenteDb = service.findDocenteByDocente(docente);
                }
            }

            node.put("existeDocente", (docenteDb != null));

            if (docenteDb == null) {
                docenteDb = new Docente();
                docenteDb.setPersona(persona);
            }

            Context ctx = new Context();
            ctx.setVariable("docente", docenteDb);
            ctx.setVariable("helper", new AlumnoHelper());
            ctx.setVariable("documentos", service.allDocumentos());
            ctx.setVariable("modalidades", service.allModalidadEstudio(compania));

            String htmlContent = springHtml.process("academico/profesor/profesorForm", ctx, new DOMSelectorFragmentSpec("#formDocente"));

            node.put("html", htmlContent);
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
    @RequestMapping("findPersonaProfesor")
    public JsonResponse findPersonaProfesor(Docente docente, HttpSession session) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        response.setSuccess(false);

        try {

            Persona persona = service.findPersonaByDocIdentidad(docente.getPersona());
            Persona personaBD = null;
            if (persona != null) {
                personaBD = service.findPersona(persona);
            }
            AlumnoHelper helper = new AlumnoHelper();

            if (personaBD != null) {
                node.put("idPersona", personaBD.getId());
                node.put("foto", personaBD.getFoto());
                node.put("tipoDocumentoId", personaBD.getTipoDocumento().getId());
                node.put("numeroDoc", personaBD.getNumeroDocIdentidad());
                node.put("paterno", personaBD.getPaterno());
                node.put("materno", personaBD.getMaterno());
                node.put("nombres", personaBD.getNombres());
                node.put("emailCompania", personaBD.getEmailCompania());
                node.put("sexo", personaBD.getSexo());
                node.put("paisNacerId", personaBD.getPaisNacer() != null ? personaBD.getPaisNacer().getId() : null);
                node.put("paisNacerNombre", personaBD.getPaisNacer() != null ? personaBD.getPaisNacer().getNombre() + " | "
                        + helper.showCodigoPais(personaBD.getPaisNacer()) : null);
                node.put("ubicacionNacerId", personaBD.getUbicacionNacer() != null ? personaBD.getUbicacionNacer().getId() : null);
                node.put("ubicacionNacerNombre", personaBD.getUbicacionNacer() != null ? personaBD.getUbicacionNacer().getDistrito() : null);
                node.put("fechaNacer", personaBD.getFechaNacer() != null ? TypesUtil.getStringDate(personaBD.getFechaNacer(), "dd/MM/yyyy") : "");
                node.put("nacionalidadId", personaBD.getNacionalidad() != null ? personaBD.getNacionalidad().getId() : null);
                node.put("nacionalidadNombre", personaBD.getNacionalidad() != null ? personaBD.getNacionalidad().getNombre() : null);
                node.put("telefono", personaBD.getTelefono());
                node.put("celular", personaBD.getCelular());
                node.put("email", personaBD.getEmail());
                node.put("paisDomiciliodId", personaBD.getPaisDomicilio() != null ? personaBD.getPaisDomicilio().getId() : null);
                node.put("paisDomicilioNombre", personaBD.getPaisDomicilio() != null ? personaBD.getPaisDomicilio().getNombre() : null);
                node.put("ubicaiconDomiciliodId", personaBD.getUbicacionDomicilio() != null ? personaBD.getUbicacionDomicilio().getId() : null);
                node.put("ubicacionDomicilioNombre", personaBD.getUbicacionDomicilio() != null ? personaBD.getUbicacionDomicilio().getDistrito() : null);
                node.put("direccion", personaBD.getDireccion());
                node.put("foto", personaBD.getFoto());

                response.setSuccess(true);
            }

            response.setData(node);

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

    @ResponseBody
    @RequestMapping(value = "view/{file:.*}")
    public byte[] showAvatar(@PathVariable("file") String file, HttpServletRequest reextencionquest, HttpSession session) throws IOException {

        FileInputStream in;
        try {
            String foto = Constantine.TMP_DIR + file;
            in = new FileInputStream(foto);

            File photo = new File(foto);
            if (!photo.exists()) {
                in = new FileInputStream(foto);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            in = new FileInputStream("/phobos/images/unalm/male.png");
        }

        byte[] img = IOUtils.toByteArray(in);

        in.close();

        return img;
    }

    @RequestMapping("{idDocente}/cargaacademica")
    public String index(@PathVariable("idDocente") Long idDocente, Model model, HttpSession session) {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        Docente docente = service.find(new Docente(idDocente));

        model.addAttribute("docente", docente);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        //    cargaAcademicaService.createEvaluacionSeccionPorDocente(ds.getDocente(), ds);
        model.addAttribute("dptoAcad", docente.getDepartamentoAcademico());
        return "academico/profesor/cargaAcademicaProfesor";
    }

    @ResponseBody
    @RequestMapping("{idDocente}/listCargaAcademicaDocente")
    public DynatableResponse list(@PathVariable("idDocente") Long idDocente, DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        Docente docente = service.find(new Docente(idDocente));

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            CicloAcademico ciclo = ds.getCicloAcademico();

            List<GrupoSeccion> gruposSeccion = service.allGpoSecciones(docente, ciclo, ds);

            for (GrupoSeccion grupoSeccion : gruposSeccion) {
                ObjectNode node = JsonHelper.createJson(grupoSeccion, JsonNodeFactory.instance, true, new String[]{
                    "id", "estadoEnum",
                    "curso.codigo",
                    "curso.nombre",
                    "curso.tpc",
                    "secciones.tipoSeccionEnum",
                    "secciones.codigo2",
                    "secciones.matriculados",
                    "secciones.aula.codigo",
                    "secciones.aula.nombre",
                    "secciones.grupoHoras.codigo",
                    "secciones.docenteSeccion.*"
                });

                array.add(node);
            }

            json.setData(array);
            json.setTotal(gruposSeccion.size());
            json.setFiltered(gruposSeccion.size());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

}
