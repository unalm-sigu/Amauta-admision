package pe.edu.lamolina.amauta.controller.academico.profesor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.docentemodalidad.FiltroEncuestaCargaAcademicaDTO;
import pe.edu.lamolina.amauta.controller.academico.profesor.view.FiltroHistoricoCargaAcademicaDTO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.misc.FotoHelper;
import pe.edu.lamolina.amauta.controller.academico.profesor.view.ProfesoresPDF;
import pe.edu.lamolina.amauta.controller.academico.profesor.view.ReporteCargaAcademicaPDF;
import pe.edu.lamolina.amauta.controller.academico.profesor.view.ReporteDocenteCargaCicloView;
import pe.edu.lamolina.amauta.controller.academico.profesor.view.ReporteDocenteCicloView;
import pe.edu.lamolina.amauta.controller.academico.profesor.view.ReporteHistoricoCargaAcademicoView;
import pe.edu.lamolina.amauta.controller.academico.visitante.AlumnoHelper;
import pe.edu.lamolina.amauta.controller.docente.cargaacademica.CargaAcademicaService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.horario.HorarioSeccion;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("academico/profesor")
public class ProfesorController {

    private final ProfesorService service;
    private final CargaAcademicaService cargaAcademicaService;
    private final VerificadorService verificadorService;

    private final DespliegueConfig despliegueConfig;
    private final ProfesoresPDF profesoresPDF;
    private final ReporteCargaAcademicaPDF reporte;
    private final ReporteDocenteCicloView reporteDocenteCicloView;
    private final ReporteDocenteCargaCicloView reportedocentecargacicloView;
    private final ReporteHistoricoCargaAcademicoView reporteHistoricoCargaAcademicoViewPDF;

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
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        String codeRequest = verificadorService.generateCodeRequest();

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds, codeRequest);
        List<Facultad> facultades = departamentos.stream().map(x -> x.getFacultad()).distinct().collect(Collectors.toList());
        List<CicloAcademico> ciclos = service.allCicloAcademico();
        List<CicloAcademico> ciclosNivelacion = service.allCicloAcademicoNivel();
        boolean puedeActivar = verificadorService.isTrabajadorOera(ds);
        boolean isRevisorDocente = ds.getRoles().stream()
                .anyMatch(rol -> RolEnum.JEFE_DPTO_ACA == rol.getCodigoEnum());

        boolean rolDocente = ds.getRoles().stream().anyMatch(rol -> RolEnum.LOGUEO_DOCENTE == rol.getCodigoEnum());

        boolean isLoginDocente = !despliegueConfig.isProduccion() || rolDocente;
        boolean isProduction = despliegueConfig.isProduccion();

        ArrayNode jFacultades = JaneHelper.from(facultades).array();
        ArrayNode jDepartamentos = JaneHelper.from(departamentos).join("facultad", "id").array();
        ArrayNode jCicloAcademicos = JaneHelper.from(ciclos).only("id,codigo,descripcion").array();

        model.addAttribute("puedeActivar", puedeActivar);
        model.addAttribute("jFacultades", jFacultades.toString());
        model.addAttribute("jDepartamentos", jDepartamentos.toString());
        model.addAttribute("jCicloAcademicos", jCicloAcademicos.toString());
        model.addAttribute("loginDocente", isLoginDocente);
        model.addAttribute("isRevisorDocente", isRevisorDocente);
        model.addAttribute("isProduction", isProduction);

        ArrayNode jCicloAcademicosNivelacion = JaneHelper.from(ciclosNivelacion).only("id,codigo,descripcion").array();
        model.addAttribute("jCicloAcademicosNivelacion", jCicloAcademicosNivelacion.toString());
        return "academico/profesor/profesor";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse allDocente(DynatableFilter filter, HttpSession session, HttpServletRequest request) {

        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        boolean isRevisorDocente = ds.getRoles().stream()
                .anyMatch(rol -> RolEnum.JEFE_DPTO_ACA == rol.getCodigoEnum());
        String activo = isRevisorDocente ? "activos" : "";
        String codeRequest = verificadorService.generateCodeRequest();

        try {

            List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds, codeRequest);

            List<Docente> docentes;

            if (filter.getQueries() != null && filter.getQueries().get("departamento") != null) {
                String dep = (String) filter.getQueries().get("departamento");
                Long departamentoId = TypesUtil.getLong(dep);
                departamentos = Arrays.asList(new DepartamentoAcademico(departamentoId));
            }
            docentes = service.allByDepartamentoDynatable(filter, departamentos, ds.getCicloAcademico(), activo);

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
                node.put("foto", persona.getFoto());
                node.put("sexo", persona.getSexo());
                node.put("rutaFoto", persona.getRutaFoto());
                node.put("tipoFoto", persona.getTipoFoto());

                node.put("facultad", fa.getNombre());
                node.put("departamentoAcademico", da.getNombre());
                node.put("situacion", "Contratado");
                node.put("cantSeccionesPos", docente.getCantSeccionesPos());
                node.put("cantSeccionesPre", docente.getCantSeccionesPre());

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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            Boolean success = true;
            if (docente.getId() == null) {
                service.save(docente, ds);
                response.setMessage("Docente creado satisfactoriamente");

            } else {
                Persona personaDuplicada = service.update(docente, ds);
                if (personaDuplicada == null) {
                    response.setMessage("Docente modificado satisfactoriamente");
                } else {
                    ObjectNode objectNode = JsonHelper.createJson(personaDuplicada, JsonNodeFactory.instance, new String[]{
                        "*",
                        "tipoDocumento.*"
                    });
                    node.put("personaDuplicado", objectNode);
                    response.setMessage("DNI duplicado");
                    success = false;
                }
            }

            response.setSuccess(success);
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
            response.setMessage("El estado del docente fue actualizado satisfactoriamente");
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
        try {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            Persona persona = service.findPersonaByDocIdentidad(docente.getPersona());

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
                node.put("conDiscapacidad", personaBD.getConDiscapacidad());

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
            String absoluteName = GlobalConstantine.TMP_DIR + fileName;
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
            String foto = GlobalConstantine.TMP_DIR + file;
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

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        Docente docente = service.find(new Docente(idDocente));

        model.addAttribute("docente", docente);
        model.addAttribute("cicloAcademico", ds.getCicloAcademico());

        //    cargaAcademicaService.createEvaluacionSeccionPorDocente(ds.getDocente(), ds);
        model.addAttribute("dptoAcad", docente.getDepartamentoAcademico());
        return "academico/profesor/cargaAcademicaProfesor";
    }

    @ResponseBody
    @RequestMapping("{idDocente}/cargaAcademicaSeparada")
    public JsonResponse cargaAcademicaSeparada(@PathVariable("idDocente") Long idDocente, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            ArrayNode arrayPregrado = new ArrayNode(JsonNodeFactory.instance);
            ArrayNode arrayPosgrado = new ArrayNode(JsonNodeFactory.instance);
            ObjectNode data = new ObjectNode(JsonNodeFactory.instance);

            CicloAcademico ciclo = ds.getCicloAcademico();
            Docente docente = service.find(new Docente(idDocente));
            List<GrupoSeccion> gruposSeccion = cargaAcademicaService.allGpoSecciones(docente, ciclo);

            BigDecimal creditosPregrado = BigDecimal.ZERO;
            BigDecimal creditosPosgrado = BigDecimal.ZERO;

            for (GrupoSeccion grupoSeccion : gruposSeccion) {
                AnexoBoletin anexoSup = grupoSeccion.getAnexoBoletin().getAnexoSuperior();

                List<Seccion> secciones = grupoSeccion.getSecciones();
                for (Seccion seccion : secciones) {
                    List<DocenteSeccion> profesSeccion = seccion.getDocenteSeccion();
                    for (DocenteSeccion profeSecc : profesSeccion) {
                        if (profeSecc.getCreditosCarga() == null) {
                            continue;
                        }
                        if (anexoSup.isAnexoCursosPostgrado()) {
                            creditosPosgrado = creditosPosgrado.add(profeSecc.getCreditosCarga());
                        } else {
                            creditosPregrado = creditosPregrado.add(profeSecc.getCreditosCarga());
                        }
                    }
                }

                ObjectNode node = JaneHelper
                        .from(grupoSeccion)
                        .only("id, estadoEnum, estadoGrupoEnum, cursoDirigido")
                        .join("cicloAcademico", "tipoEnum")
                        .join("curso", "codigo, nombre, tpc")
                        .join("curso.modalidadEstudio", "id, nombre, estado, codigo")
                        .join("planCalificacion", "id")
                        .json();

                List<Seccion> seccionesByGpoSecc = grupoSeccion.getSecciones();
                ArrayNode seccionesArray = new ArrayNode(JsonNodeFactory.instance);
                for (Seccion seccion : seccionesByGpoSecc) {
                    ObjectNode nodeSeccion = JaneHelper
                            .from(seccion)
                            .only("id,tipoSeccionEnum,codigo2,matriculados,verInformacion,horarioTexto,linkZoom")
                            .join("aula", "codigo,nombre,usuarioZoom,passZoom")
                            .join("grupoHoras", "codigo")
                            .json();

                    List<DocenteSeccion> docentesSecc = seccion.getDocenteSeccion();
                    ArrayNode docSeccArray = JaneHelper
                            .from(docentesSecc)
                            .only("id,estado,porcentajeCarga,fechaInicio,fechaFin,creditosCarga")
                            .array();

                    nodeSeccion.set("docenteSeccion", docSeccArray);
                    seccionesArray.add(nodeSeccion);
                }

                node.set("secciones", seccionesArray);

                if (anexoSup.isAnexoCursosPostgrado()) {
                    arrayPosgrado.add(node);
                } else {
                    arrayPregrado.add(node);
                }
            }

            data.set("cargaPregrado", arrayPregrado);
            data.set("cargaPosgrado", arrayPosgrado);
            data.put("creditosPregrado", creditosPregrado);
            data.put("creditosPosgrado", creditosPosgrado);

            response.setData(data);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("reporteEntregaMateriales")
    public ModelAndView reporteEntregaMateriales(@RequestParam(value = "facultad", required = false) Long facultadId,
            Model model, HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {
        log.debug("facultad " + facultadId);
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        String codeRequest = verificadorService.generateCodeRequest();

        List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds, codeRequest);

        model.addAttribute("cicloAcademico", ds.getCicloAcademico());
        model.addAttribute("facultad", new Facultad(facultadId));
        model.addAttribute("departamentos", departamentos);
        return new ModelAndView(profesoresPDF);
    }

    @RequestMapping("reporteCargaAcademica")
    public ModelAndView reporteCargaAcademica(@RequestBody FiltroEncuestaCargaAcademicaDTO filtro,
            Model model, HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {

        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        String codeRequest = verificadorService.generateCodeRequest();

        List<DepartamentoAcademico> departamentos = verificadorService.allInstanciasByMenuRol(TipoOficinaEnum.DPTO, request, ds, codeRequest);

        if (filtro.getFacultad() != null) {

            List<DepartamentoAcademico> departamentosXfacutad = departamentos
                    .stream()
                    .filter(x -> x.getFacultad().getId() == filtro.getFacultad())
                    .collect(Collectors.toList());

            if (!departamentosXfacutad.isEmpty()) {
                departamentos = departamentosXfacutad;
            }

        }

        if (filtro.getDepartamento() != null) {
            departamentos.removeIf(x -> !x.equals(new DepartamentoAcademico(filtro.getDepartamento())));
        }

        List<Docente> docentes = new ArrayList<>();

        if (filtro.getDocente() != null) {
            docentes.add(service.find(new Docente(filtro.getDocente())));
        } else {
            docentes = service.allDocenteByDepartamentosAcademicoEstado(departamentos, EnteAcademicoEstadoEnum.ACT);
        }

        List<DocenteSeccion> docentesSecciones = service.allDocenteSeccionActivosByDocentesCiclos(docentes, filtro.getCicloAcademicos());

        List<Seccion> secciones = docentesSecciones.stream().map(x -> x.getSeccion())
                .distinct().collect(Collectors.toList());

        List<HorarioSeccion> horarioSecciones = service.allHorarioSeccionBySecciones(secciones);

        model.addAttribute("cicloAcademicos", filtro.getCicloAcademicos());
        model.addAttribute("tipoGrado", filtro.getTipoGrado());

        model.addAttribute("docentes", docentes);

        model.addAttribute("docentesSecciones", docentesSecciones);
        model.addAttribute("horarioSecciones", horarioSecciones);

        return new ModelAndView(reporte);
    }

    @RequestMapping("reporteDocenteCicloAcademico")
    public ModelAndView reporteDocenteCicloAcademico(@RequestBody FiltroEncuestaCargaAcademicaDTO filtro,
            Model model, HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {

        if (filtro.getCicloAcademicos() != null) {
            List<DocenteCicloBean> listdocenteCicloBean = service.allDocentecicloAcademico(filtro.getCicloAcademicos());
            model.addAttribute("listdocenteCicloBean", listdocenteCicloBean);
            model.addAttribute("listaDocente", "ListaDocente");
            return new ModelAndView(reporteDocenteCicloView);
        } else {
            List<DocenteCicloCargaBean> listdocenteCicloCargaBean = service.allDocenteCargacicloAcademico(filtro.getDocente());
            model.addAttribute("listdocenteCicloCargaBean", listdocenteCicloCargaBean);
            model.addAttribute("listaDocente", "ListaDocente");
            return new ModelAndView(reportedocentecargacicloView);
        }

    }

    @ResponseBody
    @RequestMapping("searchDocente")
    public ArrayNode searchDocente(@RequestParam("nombre") String nombre) {

        List<Docente> docentes = service.allByNombre(nombre);

        return JaneHelper.from(docentes).
                only("id").
                join("persona", "id,nombreCompleto").array();

    }

    @RequestMapping("reporteHistoricoCargaAcademica")
    public ModelAndView reporteHistoricoCargaAcademica(@RequestBody FiltroHistoricoCargaAcademicaDTO filtro,
            Model model, HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        List<HistoricoCargaAcademicoBean> historicos = service.allHistoricoCargaAcademico(filtro, ds);

        model.addAttribute("historicos", historicos);
        return new ModelAndView(reporteHistoricoCargaAcademicoViewPDF);
    }

}
