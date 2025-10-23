package pe.edu.lamolina.amauta.controller.academico.profesor;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.academico.profesor.contratoprofesor.ContratoService;
import pe.edu.lamolina.amauta.controller.docente.cargaacademica.CargaAcademicaService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.rrhh.ContratoDocenteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.rrhh.ContratoDocente;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@RequestMapping("academico/docente/carga")
public class DocenteCargaAcademicaController {
    private final ProfesorService service;
    private final CargaAcademicaService cargaAcademicaService;
    private final VerificadorService verificadorService;

    private final ContratoDocenteDAO contratoDocenteDAO;

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
                .anyMatch(rol -> RolEnum.JEFE_DPTO_ACA == rol.getCodigoEnum() || "SOPORTE_TECNICO_DERA".equals(rol.getCodigo()));

        ArrayNode jFacultades = JaneHelper.from(facultades).array();
        ArrayNode jDepartamentos = JaneHelper.from(departamentos).join("facultad", "id").array();
        ArrayNode jCicloAcademicos = JaneHelper.from(ciclos).only("id,codigo,descripcion").array();

        model.addAttribute("puedeActivar", puedeActivar);
        model.addAttribute("jFacultades", jFacultades.toString());
        model.addAttribute("jDepartamentos", jDepartamentos.toString());
        model.addAttribute("jCicloAcademicos", jCicloAcademicos.toString());
        model.addAttribute("isRevisorDocente", isRevisorDocente);

        ArrayNode jCicloAcademicosNivelacion = JaneHelper.from(ciclosNivelacion).only("id,codigo,descripcion").array();
        model.addAttribute("jCicloAcademicosNivelacion", jCicloAcademicosNivelacion.toString());
        return "academico/profesor/cargaAcademica";
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

            String tipoPrograma = null;
            if (filter.getQueries() != null && filter.getQueries().get("tipoPrograma") != null) {
                tipoPrograma = (String) filter.getQueries().get("tipoPrograma");
            }

            docentes = service.allDocentesCargaByCiclo(filter, departamentos, ds.getCicloAcademico(),tipoPrograma);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (Docente docente : docentes) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                Persona persona = docente.getPersona();
                ContratoDocente contratoDocente = contratoDocenteDAO.find(docente.getId());
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
                node.put("situacion",  contratoDocente.getSituacion() != null ? contratoDocente.getSituacion().getNombre() : "" );
                node.put("categoria", contratoDocente.getCategoria() != null ? contratoDocente.getCategoria().getNombre() : "" );
                node.put("dedicacion", contratoDocente.getDedicacion() != null ? contratoDocente.getDedicacion().getNombre() : "" );

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
}
