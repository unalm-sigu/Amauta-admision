package pe.edu.lamolina.pivot.controller.reporte;

import com.itextpdf.text.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.pivot.controller.reporte.dto.HoraDTO;
import pe.edu.lamolina.pivot.controller.reporte.dto.HorarioDTO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;

@Service
@Transactional(readOnly = true)
public class ReporteServiceImp implements ReporteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoGrupoHorasDAO tipoGrupoHorasDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;

    @Autowired
    SeccionHorarioCachimbosDAO seccionHorarioCachimbosDAO;

    @Autowired
    HorarioCachimbosDAO horarioCachimbosDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Override
    public Map<Long, HorarioDTO> allHorariosCachimbo(CicloAcademico ciclo) {

        List<HorarioCachimbos> horariosCachimbo = horarioCachimbosDAO.allByCiclo(ciclo);

        List<SeccionHorarioCachimbos> seccionesCachimboGeneral = seccionHorarioCachimbosDAO.allByHorarios(horariosCachimbo);
        List<Hora> horas = this.allHorasEscuela();
        List<Dia> dias = this.allDiaForPrinter();

        Map<String, HorarioSeccion> mapHorariosSeccion = allHorarioSeccionBySecciones(seccionesCachimboGeneral);

        Map<Long, HorarioDTO> horariosDTO = new HashMap();

        for (HorarioCachimbos horarioCachimbo : horariosCachimbo) {

            HorarioDTO horarioDTO = new HorarioDTO(horarioCachimbo.getCodigo());

            horariosDTO.put(horarioCachimbo.getId(), horarioDTO);

            List<SeccionHorarioCachimbos> seccionesHorarioCachimbo = this.allSeccionHorarioCachimboByHorario(horarioCachimbo, seccionesCachimboGeneral);
            List filas = new ArrayList();
            horarioDTO.setHorarios(filas);

            filas.add(this.generateCabecera(dias));

            for (Hora hora : horas) {
                List<HoraDTO> columnas = new ArrayList();
                filas.add(columnas);

                HoraDTO hh = new HoraDTO();
                hh.setTipoCelda(HoraDTO.TipoCeldaDTO.BODY);
                hh.setAlineacion(Element.ALIGN_CENTER);
                hh.setVacio(false);
                hh.setContenido(hora.getDescripcion2());
                columnas.add(hh);

                for (Dia dia : dias) {

                    HoraDTO horaDTO = new HoraDTO();
                    horaDTO.setTipoCelda(HoraDTO.TipoCeldaDTO.BODY);
                    horaDTO.setAlineacion(Element.ALIGN_LEFT);
                    horaDTO.setVacio(true);

                    columnas.add(horaDTO);

                    HorarioSeccion horario = null;

                    for (SeccionHorarioCachimbos shc : seccionesHorarioCachimbo) {

                        String unique = String.format("%s-%s-%s", shc.getSeccion().getId(), hora.getId(), dia.getId());
                        horario = mapHorariosSeccion.get(unique);

                        if (horario != null) {
                            break;
                        }
                    }

                    String contenido = "";
                    if (horario != null) {
                        horaDTO.setVacio(false);

                        Docente docente = this.getDocentePrincipal(horario);

                        Curso curso = horario.getSeccion().getGrupoSeccion().getCurso();

                        String cur = curso.getCodigo() + " " + curso.getNombre();

                        String doc = "Prof. Desconocido";
                        if (docente != null) {
                            if (docente.getPersona() != null) {
                                doc = String.format("Prof: %s (%s)",
                                        docente.getPersona().getPaterno(),
                                        docente.getCodigo()
                                );
                            }
                        }

                        String clave = "Clave: " + horario.getSeccion().getCodigo2();

                        String aula = " ";
                        if (horario.getAula() != null) {
                            aula = String.format("Aula: %s (%s)",
                                    horario.getAula().getCodigo(),
                                    horario.getAula().getAulaSuperior().getNombre());
                        }
                        contenido = cur + "\n" + doc + "\n" + clave + "\n" + aula;
                    }
                    horaDTO.setContenido(contenido);

                }
            }
        }

        return horariosDTO;

    }

    public List<Hora> allHorasEscuela() {
        return horaDAO.allHorasByRango(8, 18);
    }

    public List<Dia> allDiaForPrinter() {
        return diaDAO.allDiaForPrinter();
    }

    @Override
    public List<AlumnoHorario> allAlumnoHorario(CicloAcademico ciclo) {
        return alumnoHorarioDAO.allByCicloAcademicoOrder(ciclo);
    }

    private Map<String, HorarioSeccion> allHorarioSeccionBySecciones(List<SeccionHorarioCachimbos> seccionesCachimbo) {

        List<Seccion> secciones = seccionesCachimbo.stream()
                .map(x -> x.getSeccion()).collect(Collectors.toList());

        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySecciones(secciones);

        Map<String, HorarioSeccion> mapHorariosSeccion = new HashMap();
        for (HorarioSeccion hs : horariosSeccion) {
            String unique = String.format("%s-%s-%s", hs.getSeccion().getId(), hs.getHora().getId(), hs.getDia().getId());
            mapHorariosSeccion.put(unique, hs);
        }

        return mapHorariosSeccion;

    }

    private List<SeccionHorarioCachimbos> allSeccionHorarioCachimboByHorario(HorarioCachimbos horarioCachimbo, List<SeccionHorarioCachimbos> seccionesCachimboGeneral) {

        return seccionesCachimboGeneral.stream()
                .filter(x -> x.getHorarioCachimbos().getId() == horarioCachimbo.getId())
                .collect(Collectors.toList());

    }

    public Map<Long, Oficina> allOficinaByConsejero() {

        List<Oficina> oficinas = oficinaDAO.allOficinaConsejero();
        return TypesUtil.convertListToMap("instanciaOficina", oficinas);
    }

    private Docente getDocentePrincipal(HorarioSeccion horario) {
        Docente docente = null;
        List<DocenteSeccion> docentes = horario.getSeccion().getDocenteSeccion();

        for (DocenteSeccion d : docentes) {
            if (d.getDocente().getEstadoEnum() == DocenteEstadoEnum.ACT
                    && d.getPrincipal() == 1) {
                docente = d.getDocente();
                break;

            }
        }
        return docente;
    }

    private Object generateCabecera(List<Dia> dias) {
        List<HoraDTO> cabecera = new ArrayList();

        HoraDTO hora = new HoraDTO();
        hora.setTipoCelda(HoraDTO.TipoCeldaDTO.HEADER);
        hora.setAlineacion(Element.ALIGN_CENTER);
        hora.setVacio(false);
        hora.setContenido("Hora");
        cabecera.add(hora);

        for (Dia dia : dias) {
            HoraDTO dd = new HoraDTO();
            dd.setTipoCelda(HoraDTO.TipoCeldaDTO.HEADER);
            dd.setAlineacion(Element.ALIGN_CENTER);
            dd.setVacio(false);
            dd.setContenido(dia.getNombre());
            cabecera.add(dd);
        }

        return cabecera;
    }

}
