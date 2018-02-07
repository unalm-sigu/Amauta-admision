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
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.cargaacademica.CargaAcademicaService;
import pe.edu.lamolina.pivot.controller.academico.visitante.AlumnoHelper;
import pe.edu.lamolina.pivot.controller.general.foto.FotoHelper;
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
                node.put("tipoDoc", (String) ObjectUtil.getParentTree(persona, "tipoDocumento.simbolo"));
                node.put("nroDocumento", persona.getNumeroDocIdentidad());
                node.put("telefono", persona.getTelefono());
                node.put("celular", persona.getCelular());
                node.put("email", persona.getEmail());
                node.put("emailEmpresa", persona.getEmailCompania());
                node.put("rutaFoto", helper.getRutaFoto(persona.getFoto(), persona.getSexo()));

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
        model.addAttribute("modalidades", service.allModalidadEstudio(compania));
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

                docenteDb = service.findDocenteByPersona(persona);
            }

            node.put("existeDocente", (docenteDb != null));

            if (docenteDb == null) {
                docenteDb = new Docente();
                docenteDb.setPersona(persona);
            }

            Context ctx = new Context();
            ctx.setVariable("docente", docenteDb);
            ctx.setVariable("documentos", service.allDocumentos());
            ctx.setVariable("modalidades", service.allModalidadEstudio(compania));

            String htmlContent = springHtml.process("academico/profesor/profesorForm", ctx, new DOMSelectorFragmentSpec("#formularioDocente"));

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
    @RequestMapping("disponibilidad")
    public JsonResponse disponibilidadPersona(Docente docente, HttpSession session) {

        JsonResponse response = new JsonResponse();
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        try {

            Persona persona = service.findPersonaByDocIdentidad(docente.getPersona());
            Persona personaDb = service.findPersona(docente.getPersona());
            node.put("numeroDocOriginal", personaDb.getNumeroDocIdentidad());

            if (personaDb.getFechaValidacionReniec() != null) {
                logger.debug("No puede editar el numero del documento de un registro ya validado");
                response.setMessage("No puede editar el numero del documento de un registro ya validado");
                node.put("tipoDocOriginal", personaDb.getTipoDocumento().getNombre());
                node.put("tipoIdDocOriginal", personaDb.getTipoDocumento().getId());
                node.put("validadoReniec", (personaDb.getFechaValidacionReniec() != null));
            }

            node.put("existePersona", (persona != null));
            if (persona != null) {
                Persona personaForm = docente.getPersona();
                node.put("passPersona", (personaForm.getId().longValue() == persona.getId()));
                node.put("simboloDoc", persona.getTipoDocumento().getSimbolo());
                node.put("numeroDoc", persona.getNumeroDocIdentidad());

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

            List<GrupoSeccion> gruposSeccion = cargaAcademicaService.allGrupoByDocente(docente, ciclo, ds);
            logger.debug(this.getClass() + " Lista grupos por docente {}", gruposSeccion.size());

            for (GrupoSeccion grupoSeccion : gruposSeccion) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", grupoSeccion.getId());
                node.put("idCurso", grupoSeccion.getCurso().getId());
                node.put("tipoCiclo", grupoSeccion.getCicloAcademico().getTipoCicloEnum().getValue());
                node.put("nombre", grupoSeccion.getCurso().getNombre());
                node.put("codigo", grupoSeccion.getCurso().getCodigo());
                node.put("tpc", grupoSeccion.getCurso().getTpc());
                node.put("responsable", (String) ObjectUtil.getParentTree(grupoSeccion.getDocenteResponsable(), "persona.nombreCompleto"));
                node.put("codigo", grupoSeccion.getCurso().getCodigo());
                node.put("estadoGrupoEnum", grupoSeccion.getEstadoGrupoEnum().getValue());
                node.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());
                //(String) ObjectUtil.getParentTree(docSeccion, "seccion.aula.nombre")
                node.put("estadoGrupoCerrado", grupoSeccion.isEstadoGrupoCerrado());
                String secciones = "";

                for (Seccion seccion : grupoSeccion.getSecciones()) {
                    secciones += seccion.getId() + "|" + seccion.getCodigo2() + "|";

                    if (ObjectUtil.getParentTree(seccion, "grupoHoras") != null) {
                        secciones += seccion.getGrupoHoras().getId() + "|" + seccion.getGrupoHoras().getCodigo() + "|";
                        //grupoHoras += seccion.getGrupoHoras().getId() + "|" + seccion.getGrupoHoras().getCodigo() + ",";
                    } else {
                        secciones += " | |";
                    }
                    secciones += (seccion.getVerInformacion() ? "VER" : "NO-VER") + ",";
                }
                node.put("secciones", secciones.substring(0, secciones.length() - 1));
//                if (!"".equals(grupoHoras)) {
//                    grupoHoras = grupoHoras.substring(0, grupoHoras.length() - 1);
//                }
//                node.put("grupoHoras", grupoHoras);

                boolean tienePlanCalificacion = false;
                boolean verOpciones = false;
                boolean propuesto = false;
                PlanCalificacion planCalificacionSelected = null;
                node.put("sistemas", "");

                List<PlanCalificacionCurso> planesCalificacionesCursos = grupoSeccion.getCurso().getPlanesCalificacionCursos();

                StringBuilder strbSistemas = new StringBuilder();
                logger.debug("Curso {}, Cantidad Plan Cursos {}", grupoSeccion.getCurso().getId(), planesCalificacionesCursos.size());

                if (grupoSeccion.getPlanCalificacion() == null || grupoSeccion.isEstadoPropuesto()) {
                    logger.debug("El grupo no tiene plan calificacion o su estado es propuesto");
                    if (planesCalificacionesCursos.isEmpty()) {
                        logger.debug("sin planes asociados al curso");
                        node.put("estado", EstadoPlanCalificaEnum.PEND.name());
                        node.put("estadoEnum", EstadoPlanCalificaEnum.PEND.getValue());
                    } else {
                        logger.debug("con planes asociados al curso, quedara como propuesto");
                        for (PlanCalificacionCurso planesCalificacionesCurso : planesCalificacionesCursos) {
                            strbSistemas.append(planesCalificacionesCurso.getPlanCalificacion().getId());
                            strbSistemas.append(",");
                            strbSistemas.append(planesCalificacionesCurso.getPlanCalificacion().getCodigo());
                            strbSistemas.append("-");
                        }
                        if (strbSistemas.length() != 0) {
                            node.put("sistemas", strbSistemas.substring(0, strbSistemas.length() - 1));
                        }

                        node.put("estado", EstadoPlanCalificaEnum.PRO.name());
                        node.put("estadoEnum", EstadoPlanCalificaEnum.PRO.getValue());
                        propuesto = true;
                        verOpciones = true;
                    }

                } else {
                    verOpciones = true;
                    node.put("idSistemaCalificacion", grupoSeccion.getPlanCalificacion().getId().toString());
                    node.put("sistemaCalificacion", grupoSeccion.getPlanCalificacion().getCodigo());

                    node.put("estado", grupoSeccion.getEstadoPlan());
                    node.put("estadoEnum", grupoSeccion.getEstadoPlanEnum().getValue());

                    tienePlanCalificacion = true;
                    planCalificacionSelected = grupoSeccion.getPlanCalificacion();
                }
                node.put("tienePlanCalificacion", tienePlanCalificacion);

                node.put("verDetalleSistemaCal", false);
                node.put("verOpciones", verOpciones);
                if (grupoSeccion != null) {
                    if (grupoSeccion.isEstadoSolicitado()
                            || grupoSeccion.isEstadoExpandido()
                            || grupoSeccion.isEstadoExpandir()) {
                        node.put("verDetalleSistemaCal", true);
                    }
                }
                node.put("verAceptarSistemaCal", false);
                if (grupoSeccion != null) {
                    if (grupoSeccion.isEstadoPropuesto() || propuesto) {
                        node.put("verAceptarSistemaCal", true);
                    }
                }
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
