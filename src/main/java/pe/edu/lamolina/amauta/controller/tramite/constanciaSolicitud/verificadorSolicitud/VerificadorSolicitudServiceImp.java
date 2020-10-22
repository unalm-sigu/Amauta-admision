package pe.edu.lamolina.amauta.controller.tramite.constanciaSolicitud.verificadorSolicitud;

import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.tramite.plantillaConstancia.PlantillaGenerica;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.PlantillaDocumentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.PlantillaIncrustacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDocumentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.VariablePlantillaDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariablePlantilla;

import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import static pe.edu.lamolina.model.constantines.AcademicoConstantine.CODIGO_ALIANZA_ESTRATEGICA;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.VARIABLE_TABLE;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.FECHAS_BACH;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.TipoConstanciaEnum;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.ObtencionGrado;

@Service
@Transactional(readOnly = true)
public class VerificadorSolicitudServiceImp implements VerificadorSolicitudService {

    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    TramiteDocumentoAcademicoDAO tramiteDocumentoAcademicoDAO;

    @Autowired
    PlantillaDocumentoAcademicoDAO plantillaDocumentoAcademicoDAO;

    @Autowired
    PlantillaIncrustacionDAO plantillaIncrustacionDAO;

    @Autowired
    VariablePlantillaDAO variablePlantillaDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    TramiteBachillerDAO tramiteBachillerDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    ObtencionGradoDAO obtencionGradoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Override
    public void verificarDocumentoAlumno(PlantillaDocumentoAcademico plantillaDocumentoAcademico, TramiteDocumentoAcademico tramiteDocumentoAcademico, Alumno alumno) {
        this.findPlantillaHtml(plantillaDocumentoAcademico, tramiteDocumentoAcademico, alumno);
    }

    private PlantillaGenerica findPlantillaHtml(PlantillaDocumentoAcademico plantillaDocumentoAcademico, TramiteDocumentoAcademico documentoAcademico, Alumno alumno) {
//        documentoAcademico = tramiteDocumentoAcademicoDAO.find(documentoAcademico.getId());
//        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());

        String htmlContent = plantillaDocumentoAcademico.getContenido();

        List<VariablePlantilla> variables = variablePlantillaDAO.allByPlantilla(plantillaDocumentoAcademico);
        alumno = alumnoDAO.findAllInfo(alumno.getId());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);

        Egresado egresado = egresadoDAO.findByAlumno(alumno);

        htmlContent = this.recorrerVariables(htmlContent, variables, alumno, egresado, alumnoCiclos, documentoAcademico);
        htmlContent = this.remplazarTablas(htmlContent, alumno, variables);

        Document html = Jsoup.parse(htmlContent);

        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        plantillaGene.setContenido(html.html());
        plantillaGene.setNombre(documentoAcademico.getTipoDocumentoAcademico().getNombre());
        return plantillaGene;
    }

    private String recorrerVariables(String htmlContent, List<VariablePlantilla> variables, Alumno alumno, Egresado egresado, List<AlumnoCiclo> alumnoCiclos, TramiteDocumentoAcademico documentoAcademico) {
        int idx = alumnoCiclos.size() - 1;
        OficinaEnum oficinaEnum = documentoAcademico.getTipoDocumentoAcademico().getTipoConstanciaEnum() == TipoConstanciaEnum.CONS ? OficinaEnum.UR : OficinaEnum.OERA;
        Oficina oficina = oficinaDAO.findByCode(oficinaEnum.name());
        ObtencionGrado obtencionGradoBachi = obtencionGradoDAO.findByAlumnoAndTipo(alumno, TipoGradoAcademicoEnum.BACH);
        ObtencionGrado obtencionGradoTitulo = obtencionGradoDAO.findByAlumnoAndTipo(alumno, TipoGradoAcademicoEnum.TIT);
        CicloAcademico cicloAcademicoAct = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);
        EventoCicloAcademico eventoAcademico = null;
        EventoCicloAcademico eventoFinAcademico = null;

        if (!alumnoCiclos.isEmpty()) {
            eventoAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(alumnoCiclos.get(0).getCicloAcademico(), FECHAS_BACH);
            eventoFinAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(alumnoCiclos.get(idx).getCicloAcademico(), FECHAS_BACH);
        }

        for (VariablePlantilla var : variables) {
            switch (var.getVariableGenerica().getCodigoVaribleEnum()) {

                case JEFE_OFICINA_OERA:
                case JEFE_URA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), oficina.getJefeEncargado() == null ? oficina.getPersonaJefe().getNombreCompleto() : oficina.getJefeEncargado().getNombreCompleto());
                    break;
                case CORRELATIVO_DOC:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "");
                    break;
                case SEX_IDENT:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getEstimado());
                    break;
                case SEX_ALUM:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getSexoEnum() == SexoEnum.F ? "a" : "o");
                    break;
                case TIPO_DOCUMENTO:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getTipoDocumento().getNombre());
                    break;
                case NUMERO_DOCUMENTO:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getNumeroDocIdentidad());
                    break;
                case MATRICULA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getCodigo());
                    break;
                case FACULTAD:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getCarrera().getFacultad().getNombre());
                    break;

                case ESPECIALIDAD:
                    if (!alumno.getCarrera().getFacultad().getCodigo().equals(alumno.getCarrera().getCodigo())) {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "Carrera de " + alumno.getCarrera().getNombre());
                    } else {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "");
                    }
                    break;
                case APELLIDO_PERSONA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getApellidosNombres());
                    break;
                case NOMBRE_PERSONA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getNombreCompleto());
                    break;

                case FECHA_CONSTANCIA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), TypesUtil.getStringDate(new Date(), "dd 'de' MMMM 'del' yyyy", "es"));
                    break;
                case APELLIDOS:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getApellidos());
                    break;
                case SENOR_A:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getSenior());
                    break;
                case ALUMNO_REGULAR:
                    MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademicoAct);
                    if (matriculaResumen != null && matriculaResumen.getCreditosMatriculados() >= 12) {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "regular");
                    } else {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "");
                    }

                    break;
                case PRIMER_CICLO_MATRICULADO:

                case CANTIDAD_CREDITOS_APROBADOS:
                    Assert.isNotNull(alumnoCiclos.get(0), "El alumno no tiene historial.");
                    break;
                case ULTIMO_CICLO_MATRICULADO:
                case CICLO_MATRICULA:
                case NIVEL_ACADEMICO:
                    idx = idx == -1 ? 0 : idx;
                    Assert.isNotNull(alumnoCiclos.get(idx), "El alumno no tiene historial.");
                    break;

                case CICLOS_CURSADOS:

                    String ciclos = alumnoCiclos.size() > 2 ? "los ciclos " : "el ciclo ";
                    int i = 1;
                    for (AlumnoCiclo ac : alumnoCiclos) {
                        if (i == alumnoCiclos.size()) {
                            ciclos = ciclos.concat("y " + ac.getCicloAcademico().getDescripcion());
                            continue;
                        }
                        ciclos = ciclos.concat(", " + ac.getCicloAcademico().getDescripcion());
                        i++;
                    }
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), ciclos);
                    break;
                case TITULO_PROFESIONAL:
                    Assert.isNotNull(egresado.getTitulo(), "El alumno no tiene titulo. Comuniquese con mesa de ayuda.");

                    break;
                case CICLO_PROMOCION:
                case CICLO_EGRESO:
                    Assert.isNotNull(egresado, "El alumno no es egresado. Comuniquese con mesa de ayuda.");
                    break;
                case FECHA_PRIMERA_MATRICULA:
                case FECHA_EGRESO:
                    Assert.isNotNull(eventoAcademico, "El alumno no es egresado. Comuniquese con mesa de ayuda.");
                    break;
                case FECHA_ULTIMA_MATRICULA:
                    Assert.isNotNull(eventoFinAcademico, "El alumno no es egresado. Comuniquese con mesa de ayuda.");
                    break;
                case PROGRAMA:
                    String programa = "";
                    if (alumno.getCarrera().getCodigo().equals(CODIGO_ALIANZA_ESTRATEGICA)) {
                        programa = programa.concat("por el Convenio de la " + alumno.getCarrera().getNombre());
                    } else {

                        programa = programa.concat("como " + alumno.getPersona().getGeneroAlumno("alter") + " " + alumno.getCarrera().getNombre());
                    }

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), programa);
                    break;

                case RESOL_EGRESO:
                case RESOL_FECHA:
                    Assert.isNotNull(obtencionGradoBachi, "El alumno no es bachiller. Comuniquese con mesa de ayuda.");
                    break;

                case RESOL_TITULO:
                case RESOL_TITULO_FECHA:

                    Assert.isNotNull(obtencionGradoTitulo, "El alumno no tiene titulo. Comuniquese con mesa de ayuda.");
                    break;

                case ORDEN_MERITO_EGRESADO:
                    Assert.isNotNull(egresado, "El alumno no es egresado. Comuniquese con mesa de ayuda.");
                    Assert.isNotNull(egresado.getOrdenMeritoFacultad(), "El alumno no tiene orden de merito facultad. Comuniquese con mesa de ayuda.");

                    break;
                case CANTIDAD_ALUMNOS:
                    Assert.isNotNull(egresado, "El alumno no es egresado. Comuniquese con mesa de ayuda.");
                    Assert.isNotNull(egresado.getControlMeritoFacultad(), "El alumno no tiene orden de control de merito facultad. Comuniquese con mesa de ayuda.");

                    break;
                case PROMEDIO_PONDERADO_GRADUACION:
                    Assert.isNotNull(egresado, "El alumno no es egresado. Comuniquese con mesa de ayuda.");
                    Assert.isNotNull(egresado.getPromedioGraduacion(), "El alumno no tiene promedio graduación. Comuniquese con mesa de ayuda.");

                    break;
                case MEJOR_PROMEDIO_PONDERADO_GRADUACION:
                    Assert.isNotNull(egresado, "El alumno no es egresado. Comuniquese con mesa de ayuda.");
                    Assert.isNotNull(egresado.getPromedioGraduacion(), "El alumno no tiene promedio graduación. Comuniquese con mesa de ayuda.");

                    break;

            }
        }
        return htmlContent;
    }

    private String remplazarTablas(String htmlContent, Alumno alumno, List<VariablePlantilla> variables) {
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        Document html = Jsoup.parse(htmlContent);
        if (!html.getElementsByClass(VARIABLE_TABLE).isEmpty()) {
            String tableOrigin = html.getElementsByClass(VARIABLE_TABLE).html();
            String tableClone = html.getElementsByClass(VARIABLE_TABLE).html();
            int idx = 1;
            int indexHtml = 1;
            for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
                for (VariablePlantilla var : variables) {
                    switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                        case TABLA_CODIGO_CURSO:
                            tableOrigin = tableOrigin.replace(var.getVariableGenerica().getCodigo(), alumnoCicloCurso.getCurso().getCodigo());
                            break;
                        case TABLA_CURSO:
                            tableOrigin = tableOrigin.replace(var.getVariableGenerica().getCodigo(), alumnoCicloCurso.getCurso().getNombre());
                            break;
                        case TABLA_CURSO_NOTA:
                            tableOrigin = tableOrigin.replace(var.getVariableGenerica().getCodigo(), alumnoCicloCurso.getNota());
                            break;
                        case TABLA_CURSO_CREDITO:
                            tableOrigin = tableOrigin.replace(var.getVariableGenerica().getCodigo(), alumnoCicloCurso.getCreditos().toString());
                            break;
                    }
                }

                if (indexHtml == idx) {
                    Element tr = html.select("tr").get(indexHtml);
                    tr.replaceWith(new Element("tr").append(tableOrigin));
                    indexHtml = 1;
                } else {
                    Element table = html.select("tbody").get(0);
                    Element trNew = new Element("tr");
                    trNew.append(tableOrigin);
                    table.insertChildren(indexHtml, trNew);
                    indexHtml++;
                }
                if (idx < alumnoCicloCursos.size()) {

                    tableOrigin = "";
                    tableOrigin = tableOrigin.concat(tableClone);
                    idx++;

                }
            }

            htmlContent = html.html();
        }
        return htmlContent;
    }

}
