package pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AmbitoReporteEnum;
import pe.edu.lamolina.model.enums.CodigoAnexoBoletinEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.ReporteOficina;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.aula.SeccionDTO;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.reporte.dto.CantidadMatriculadosDTO;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.general.ReporteOficinaDAO;
import pe.edu.lamolina.amauta.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.general.Persona;

@Service
@Transactional(readOnly = true)
public class GpoReporteServiceImp implements GpoReporteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    ReporteOficinaDAO reporteOficinaDAO;

    @Autowired
    VerificadorService verificadorService;

    @Override
    public CicloAcademico findCiclo(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }

    @Override
    public GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo) {
        List<AnexoBoletin> anexos = anexoBoletinDAO.allAnexosHijos();
        GpoSeccionResumen resumen = grupoSeccionDAO.resumenByCiclo(ciclo, anexos);
        resumen.setActividades(resumen.getActividades() == null ? 0 : resumen.getActividades());
        resumen.setDepartamentos(resumen.getDepartamentos() == null ? 0 : resumen.getDepartamentos());
        resumen.setIngresantes(resumen.getIngresantes() == null ? 0 : resumen.getIngresantes());
        resumen.setPostGrados(resumen.getPostGrados() == null ? 0 : resumen.getPostGrados());
        return resumen;
    }

    @Override
    public List<DepartamentoAcademico> allDepartamentoAcademico(CicloAcademico cicloAcademico) {

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);

        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allSinNNByCicloModalidadReporte(cicloAcademico, modalidad);

        List<Docente> docentes = docenteSeccions
                .stream()
                .filter(docSec -> docSec.getDocente() != null)
                .map(docSec -> docSec.getDocente())
                .collect(Collectors.toList());

        List<DepartamentoAcademico> departamentos = docentes
                .stream()
                .filter(doc -> doc.getDepartamentoAcademico() != null)
                .map(doc -> doc.getDepartamentoAcademico())
                .collect(Collectors.toList());

        Map<Long, List<DocenteSeccion>> docenteSeccionXdocente = TypesUtil.convertListToMapList("docente.id", docenteSeccions);
        Map<Long, Docente> docentesMap = TypesUtil.convertListToMap("id", docentes);

        Map<Long, List<DocenteSeccion>> docenteSeccionXcurso = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.id", docenteSeccions);
        Map<Long, List<Curso>> departamentoXcursos = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.departamentoAcademico.id", "seccion.grupoSeccion.curso", docenteSeccions);

        for (Docente docente : docentesMap.values()) {
            docente.setDocenteSeccion(null);
            List<DocenteSeccion> misdocenteseccion = docenteSeccionXdocente.get(docente.getId());
            docente.setDocenteSeccion(misdocenteseccion);
            docente.setMontoTotalVerano(this.calMontoTotalVerano(docente));
        }

        List<Docente> docentess = docentesMap.values().stream().collect(Collectors.toList());

        Map<Long, List<Docente>> docenteXdepartamento = TypesUtil.convertListToMapList("departamentoAcademico.id", docentess);
        Map<Long, DepartamentoAcademico> departamentosMap = TypesUtil.convertListToMap("id", departamentos);

        for (DepartamentoAcademico departamento : departamentosMap.values()) {
            departamento.setDocente(null);
            List<Docente> misdocentes = docenteXdepartamento.get(departamento.getId());
            departamento.setDocente(misdocentes);
            departamento.setMontoTotalVerano(this.calcMontoTotalVerano(departamento));

            departamento.setCurso(null);
            List<Curso> miscursos = departamentoXcursos.get(departamento.getId());
            this.fillMontoCurso(miscursos, docenteSeccionXcurso);
            List<Curso> miscursosfinal = this.simplificarCurso(miscursos);
            departamento.setCurso(miscursosfinal);
            departamento.setMatriculados(this.calcMatriculados(miscursosfinal));
        }

        return departamentosMap.values().stream().collect(Collectors.toList());
    }

    private BigDecimal calMontoTotalVerano(Docente docente) {
        BigDecimal total = BigDecimal.ZERO;
        List<DocenteSeccion> docenteSeccions = docente.getDocenteSeccion();
        if (docenteSeccions != null) {
            for (DocenteSeccion docenteSeccion : docenteSeccions) {
                if (docenteSeccion.getPagoVerano() != null) {
                    total = total.add(docenteSeccion.getPagoVerano());
                } else {
                    docenteSeccion.setPagoVerano(new BigDecimal("0.00"));
                }
            }
        }
        return total;
    }

    private BigDecimal calcMontoTotalVerano(DepartamentoAcademico departamento) {
        BigDecimal total = BigDecimal.ZERO;
        List<Docente> docentes = departamento.getDocente();
        if (docentes != null) {
            for (Docente docente : docentes) {
                if (docente.getMontoTotalVerano() != null) {
                    total = total.add(docente.getMontoTotalVerano());
                }
            }
        }
        return total;
    }

    private void fillMontoCurso(List<Curso> miscursos, Map<Long, List<DocenteSeccion>> docenteSeccionXcurso) {
        miscursos.forEach((micurso) -> {
            BigDecimal total = BigDecimal.ZERO;
            Integer matriculados = 0;
            List<DocenteSeccion> midocenteSeccion = docenteSeccionXcurso.get(micurso.getId());
            if (midocenteSeccion != null) {
                for (DocenteSeccion docente : midocenteSeccion) {
                    if (docente.getPagoVerano() != null) {
                        total = total.add(docente.getPagoVerano());
                        Integer mismatriculados = (Integer) ObjectUtil.getParentTree(docente, "seccion.matriculados");
                        mismatriculados = mismatriculados != null ? mismatriculados : 0;
                        matriculados = matriculados + mismatriculados;
                    }
                }
            }
            micurso.setMontoVerano(total);
            micurso.setMatriculados(matriculados);
        });
    }

    private List<Curso> simplificarCurso(List<Curso> miscursos) {
        Map<Long, Curso> cursosMap = new LinkedHashMap();
        miscursos.forEach(curso -> {
            Curso cursoo = cursosMap.get(curso.getId());
            if (cursoo == null) {
                cursosMap.put(curso.getId(), curso);
            } else {
                BigDecimal total = BigDecimal.ZERO;
                Integer matriculados = 0;
                if (cursoo.getMontoVerano() != null) {
                    total = total.add(cursoo.getMontoVerano());
                    matriculados = matriculados + cursoo.getMatriculados();
                }
                cursoo.setMontoVerano(total);
                cursoo.setMatriculados(matriculados);
            }
        });
        return cursosMap.values().stream().collect(Collectors.toList());
    }

    private Integer calcMatriculados(List<Curso> miscursosfinal) {
        Integer total = 0;
        if (miscursosfinal != null) {
            for (Curso curso : miscursosfinal) {
                if (curso.getMatriculados() != null) {
                    total = total + curso.getMatriculados();
                }
            }
        }
        return total;
    }

    @Override
    public List<Facultad> allDepartamentoAcademicoXfacultad(CicloAcademico cicloAcademico) {

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);

        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allSinNNByCicloModalidadReporte(cicloAcademico, modalidad);

        List<Docente> docentes = docenteSeccions
                .stream()
                .filter(docSec -> docSec.getDocente() != null)
                .map(docSec -> docSec.getDocente())
                .collect(Collectors.toList());

        List<DepartamentoAcademico> departamentos = docentes
                .stream()
                .filter(doc -> doc.getDepartamentoAcademico() != null)
                .map(doc -> doc.getDepartamentoAcademico())
                .collect(Collectors.toList());

        List<Facultad> facultades = departamentos
                .stream()
                .filter(depa -> depa.getFacultad() != null)
                .map(depa -> depa.getFacultad())
                .collect(Collectors.toList());

        Map<Long, List<DocenteSeccion>> docenteSeccionXdocente = TypesUtil.convertListToMapList("docente.id", docenteSeccions);
        Map<Long, Docente> docentesMap = TypesUtil.convertListToMap("id", docentes);

        Map<Long, List<DocenteSeccion>> docenteSeccionXcurso = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.id", docenteSeccions);
        Map<Long, List<Curso>> departamentoXcursos = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.departamentoAcademico.id", "seccion.grupoSeccion.curso", docenteSeccions);

        for (Docente docente : docentesMap.values()) {
            docente.setDocenteSeccion(null);
            List<DocenteSeccion> misdocenteseccion = docenteSeccionXdocente.get(docente.getId());
            docente.setDocenteSeccion(misdocenteseccion);
            docente.setMontoTotalVerano(this.calMontoTotalVerano(docente));
        }

        List<Docente> docentess = docentesMap.values().stream().collect(Collectors.toList());

        Map<Long, List<Docente>> docenteXdepartamento = TypesUtil.convertListToMapList("departamentoAcademico.id", docentess);
        Map<Long, DepartamentoAcademico> departamentosMap = TypesUtil.convertListToMap("id", departamentos);

        for (DepartamentoAcademico departamento : departamentosMap.values()) {
            departamento.setDocente(null);
            List<Docente> misdocentes = docenteXdepartamento.get(departamento.getId());
            departamento.setDocente(misdocentes);
            departamento.setMontoTotalVerano(this.calcMontoTotalVerano(departamento));

            departamento.setCurso(null);
            List<Curso> miscursos = departamentoXcursos.get(departamento.getId());
            this.fillMontoCurso(miscursos, docenteSeccionXcurso);
            List<Curso> miscursosfinal = this.simplificarCurso(miscursos);
            departamento.setCurso(miscursosfinal);
            departamento.setMatriculados(this.calcMatriculados(miscursosfinal));
        }

        List<DepartamentoAcademico> departamentoss = departamentosMap.values().stream().collect(Collectors.toList());
        Map<Long, List<DepartamentoAcademico>> facultaXdepartamento = TypesUtil.convertListToMapList("facultad.id", departamentoss);
        Map<Long, Facultad> facultadesMap = new LinkedHashMap();
        for (Facultad facultade : facultades) {
            Facultad faculty = facultadesMap.get(facultade.getId());
            if (faculty == null) {
                List<DepartamentoAcademico> depas = facultaXdepartamento.get(facultade.getId());
                facultade.setDepartamentoAcademico(depas);
                facultadesMap.put(facultade.getId(), facultade);
                facultade.setMontoTotalVerano(this.calMontoTotalVerano(facultade));
            }
        }

        return facultadesMap.values().stream().collect(Collectors.toList());
    }

    private BigDecimal calMontoTotalVerano(Facultad facultade) {
        BigDecimal total = BigDecimal.ZERO;
        List<DepartamentoAcademico> depas = facultade.getDepartamentoAcademico();
        if (depas != null) {
            for (DepartamentoAcademico depa : depas) {
                if (depa.getMontoTotalVerano() != null) {
                    total = total.add(depa.getMontoTotalVerano());
                }
            }
        }
        return total;
    }

    @Override
    public List<AnexoBoletin> getAnexosForBoletin(CicloAcademico ciclo, DataSessionPivot ds) {

        List<AnexoBoletin> anexosAll = anexoBoletinDAO.all();
        List<AnexoBoletin> anexosSuperiores = verificadorService.anexosSuperioresByOficina(ds);
        List<AnexoBoletin> anexosInferiores = verificadorService.anexosInferioresByOficina(ds, anexosAll);

        List<AnexoBoletin> anexos = anexoBoletinDAO.allTodosByCiclo(ciclo, anexosSuperiores, anexosInferiores);
        Collections.sort(anexos, (a1, a2) -> a1.getOrden().compareTo(a2.getOrden()));

        for (AnexoBoletin anexo : anexos) {
            anexo.setGruposSecciones(new ArrayList());
        }
        Map<Long, AnexoBoletin> mapAnexoSuper = TypesUtil.convertListToMap("anexoSuperior.id", "anexoSuperior", anexos);
        Map<Long, AnexoBoletin> mapAnexos = TypesUtil.convertListToMap("id", anexos);

        List<AnexoBoletin> anexosSuper = new ArrayList(mapAnexoSuper.values());
        for (AnexoBoletin anexo : anexosSuper) {
            anexo.setAnexosBoletinHijos(new ArrayList());
        }
        for (AnexoBoletin anexo : anexos) {
            AnexoBoletin anexoPadre = mapAnexoSuper.get(anexo.getAnexoSuperior().getId());
            anexoPadre.getAnexosBoletinHijos().add(anexo);
            anexo.setAnexoSuperior(anexoPadre);
        }

        List<Seccion> secciones = seccionDAO.allForBoletinByCiclo(ciclo);
        List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allBySecciones(secciones);
        Map<Long, List<HorarioSeccion>> mapHorarios = TypesUtil.convertListToMapList("seccion.id", horariosSecciones);
        Map<Long, Seccion> mapSeccion = TypesUtil.convertListToMap("id", secciones);
        for (Seccion secc : secciones) {
            if (secc.getGrupoSeccion().getCurso().getCodigo().equals("CC5002")) {
                System.out.println("codigo2 " + secc.getCodigo2() + " - estado " + secc.getEstado());
            }
            List<HorarioSeccion> horarioSecc = TypesUtil.getListNotNull(mapHorarios.get(secc.getId()));
            secc.setHorarioSeccion(horarioSecc);
            secc.setDocenteSeccion(new ArrayList());
        }

        Map<Long, GrupoSeccion> mapGpoSeccion = TypesUtil.convertListToMap("grupoSeccion.id", "grupoSeccion", secciones);
        List<GrupoSeccion> gpoSecciones = new ArrayList(mapGpoSeccion.values());

        Map<String, Curso> mapCursos = new LinkedHashMap();
        Map<Long, Curso> mapCursosTmp = TypesUtil.convertListToMap("curso.id", "curso", gpoSecciones);
        List<Curso> cursosTmp = new ArrayList(mapCursosTmp.values());
        for (Curso cursoTmp : cursosTmp) {
            for (AnexoBoletin anexo : anexos) {
                Curso curso = cursoTmp.clone();
                curso.setGrupoSeccion(new ArrayList());
                mapCursos.put(anexo.getId() + "-" + curso.getId(), curso);
            }
        }

        for (GrupoSeccion gpoSecc : gpoSecciones) {
            AnexoBoletin anexo = mapAnexos.get(gpoSecc.getAnexoBoletin().getId());
            if (anexo == null) { //CHECK THIS
                continue;
            }
            anexo.getGruposSecciones().add(gpoSecc);

            Curso cursoTmp = gpoSecc.getCurso();
            Curso curso = mapCursos.get(anexo.getId() + "-" + cursoTmp.getId());
            curso.getGrupoSeccion().add(gpoSecc);

            gpoSecc.setSecciones(new ArrayList());
            gpoSecc.setAnexoBoletin(anexo);
            gpoSecc.setCurso(curso);
        }

        for (Seccion secc : secciones) {
            GrupoSeccion gpoSecc = mapGpoSeccion.get(secc.getGrupoSeccion().getId());
            gpoSecc.getSecciones().add(secc);
            secc.setGrupoSeccion(gpoSecc);
        }

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allActivosBySecciones(secciones);
        for (DocenteSeccion profeSecc : docentesSecciones) {
            Seccion secc = mapSeccion.get(profeSecc.getSeccion().getId());
            secc.getDocenteSeccion().add(profeSecc);
            profeSecc.setSeccion(secc);
        }

        Collections.sort(anexosSuper, (a1, a2) -> a1.getOrden().compareTo(a2.getOrden()));

        return anexosSuper;
    }

    @Override
    public List<AnexoBoletin> getAnexosInferiores(CicloAcademico ciclo, DataSessionPivot ds) {
        List<AnexoBoletin> anexosAll = anexoBoletinDAO.all();
        List<AnexoBoletin> anexosSuperiores = verificadorService.anexosSuperioresByOficina(ds);
        List<AnexoBoletin> anexosInferiores = verificadorService.anexosInferioresByOficina(ds, anexosAll);

        List<AnexoBoletin> anexos = anexoBoletinDAO.allTodosByCiclo(ciclo, anexosSuperiores, anexosInferiores);
        Collections.sort(anexos, (a1, a2) -> a1.getOrden().compareTo(a2.getOrden()));

        System.out.println("retornando " + anexos.size() + " anexos-inf para reportes");
        return anexos;
    }

    @Override
    public List<Seccion> allSeccionesConCruce(CicloAcademico cicloAcademico) {
        List<Seccion> secciones = seccionDAO.allConCruceHorario(cicloAcademico);
        if (secciones == null || secciones.isEmpty()) {
            return new ArrayList<>();
        }
        List<GrupoHoras> gruposHoras = secciones.stream().map(x -> x.getGrupoHoras()).distinct().collect(Collectors.toList());
        List<DiaHoraGrupo> diasHorasGrupos = diaHoraGrupoDAO.allByGruposCiclo(gruposHoras, cicloAcademico);
        for (GrupoHoras gruposHora : gruposHoras) {
            List<DiaHoraGrupo> diasHorasGruposByGpoHoras = diasHorasGrupos.stream()
                    .filter(x -> x.getGrupoHorario().equals(gruposHora))
                    .collect(Collectors.toList());
            gruposHora.setDiaHoraGrupo(diasHorasGruposByGpoHoras);
        }

        for (Seccion seccion : secciones) {
            GrupoHoras grupoHorasBySeccion = gruposHoras.stream().filter(x -> x.equals(seccion.getGrupoHoras())).findFirst().orElse(null);
            seccion.setGrupoHoras(grupoHorasBySeccion);
        }
        this.fillSecciones(secciones, cicloAcademico);
        return secciones;
    }

    void fillSecciones(List<Seccion> secciones, CicloAcademico cicloAcademico) {
        List<HorarioSeccion> horariosSeccion = horarioSeccionDAO.allBySeccionesSortByDiaHora(secciones);
        horariosSeccion = horariosSeccion.stream().filter(x -> x.isEstadoActivo()).collect(Collectors.toList());

        List<HorarioAula> horarioAulas = horarioAulaDAO.allBySeccionesSortByDiaHora(secciones, cicloAcademico);
        horarioAulas = horarioAulas.stream().filter(x -> x.isEstadoActivo()).collect(Collectors.toList());

        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allPrincipalesBySecciones(secciones);

        for (Seccion seccion : secciones) {
            Aula aula = seccion.getAula();
            List<HorarioSeccion> horarioSeccionBySeccion = horariosSeccion.stream().filter(x -> x.getSeccion().equals(seccion)).collect(Collectors.toList());
            List<HorarioAula> horariosAulasBySeccion = new ArrayList<>();
            if (aula != null) {
                horariosAulasBySeccion = horarioAulas.stream()
                        .filter(x -> x.getSeccion().equals(seccion)).collect(Collectors.toList());
            }
            List<DocenteSeccion> docentesSeccionBySeccion = docenteSeccions.stream().filter(x -> x.getSeccion().equals(seccion)).collect(Collectors.toList());
            if (!docentesSeccionBySeccion.isEmpty() && docentesSeccionBySeccion.size() == 1) {
                seccion.setDocentePrincipal(docentesSeccionBySeccion.get(0).getDocente());
            }
            seccion.setHorarioSeccion(horarioSeccionBySeccion);
            seccion.setHorariosAula(horariosAulasBySeccion);
            seccion.setDocenteSeccion(docentesSeccionBySeccion);
        }
    }

    @Override
    public List<Seccion> allSeccionesByFilter(CicloAcademico cicloAcademico, SeccionDTO seccionDTO) {
        List<Seccion> secciones = seccionDAO.allByCicloAndFilter(cicloAcademico, ModalidadEstudioEnum.PRE, seccionDTO, SeccionEstadoEnum.ACT);
        this.fillSecciones(secciones, cicloAcademico);

        return secciones;
    }

    @Override
    public List<MatriculaSeccion> allMatriculadosBySeccion(SeccionDTO seccionDTO) {
        List<DocenteSeccion> docentesSecciones;
        if (seccionDTO.getSecciones() != null && !seccionDTO.getSecciones().isEmpty()) {
            docentesSecciones = docenteSeccionDAO.allBySecciones(seccionDTO.getSecciones());
        } else {
            docentesSecciones = docenteSeccionDAO.allByCiclo(seccionDTO.getCicloAcademico());
        }

        List<MatriculaSeccion> matriculados = matriculaSeccionDAO.matriculadosPorSeccion(seccionDTO);
        for (MatriculaSeccion matriculado : matriculados) {
            Seccion seccion = matriculado.getSeccion();
            DocenteSeccion docenteSeccion = docentesSecciones.stream()
                    .filter(x -> x.getSeccion().equals(seccion))
                    .filter(x -> x.getEstadoEnum() == SeccionEstadoEnum.ACT)
                    .filter(x -> x.getPrincipal() == 1)
                    .findFirst().orElse(null);
            seccion.setDocentePrincipal(docenteSeccion.getDocente());
        }
        return matriculados;
    }

    @Override
    public List<CantidadMatriculadosDTO> allCantidadMatriculados(SeccionDTO seccionDTO) {
        List<CantidadMatriculadosDTO> cantidades = new ArrayList();
        List<Seccion> secciones = seccionDAO.allActivosBySeccionDTO(seccionDTO);
        List<DocenteSeccion> docenteSecciones = docenteSeccionDAO.allPrincipalesBySecciones(secciones);
        Map<Long, DocenteSeccion> mapDocenteSeccion = TypesUtil.convertListToMap("seccion.id", docenteSecciones);

        for (Seccion seccion : secciones) {
            GrupoSeccion gpoSeccion = seccion.getGrupoSeccion();
            CicloAcademico ciclo = gpoSeccion.getCicloAcademico();
            AnexoBoletin anexo = gpoSeccion.getAnexoBoletin();
            AnexoBoletin anexoSup = anexo.getAnexoSuperior();
            Curso curso = gpoSeccion.getCurso();
            DepartamentoAcademico departamento = curso.getDepartamentoAcademico();
            DocenteSeccion profeSeccion = mapDocenteSeccion.get(seccion.getId());
            Docente docente = profeSeccion.getDocente();
            Persona persona = docente.getPersona();

            CantidadMatriculadosDTO cantidad = new CantidadMatriculadosDTO(ciclo.getDescripcion(),
                    anexoSup.getNombre(), anexo.getNombre(),
                    departamento.getNombre(), curso.getCodigo(), curso.getNombre(),
                    docente.getCodigo(), persona == null ? "Desconocido" : persona.getApellidosNombres(),
                    seccion.getCodigo2(),
                    seccion.getMatriculados().longValue());

            cantidades.add(cantidad);
        }
        return cantidades;
        //return matriculaSeccionDAO.cantidadMatriculados(seccionDTO);
    }

    @Override
    public List<ReporteOficina> allReportesProgramacion(CicloAcademico ciclo, DataSessionPivot ds) {
        List<ReporteOficina> reportes = new ArrayList();
        Oficina oficina = new Oficina(OficinaEnum.OERA);

        List<ReporteOficina> reportesAll = reporteOficinaDAO.allByOficinaAmbito(oficina, AmbitoReporteEnum.PROGRAMACION_OERA);
        List<AnexoBoletin> anexosSuper = verificadorService.anexosSuperioresByOficina(ds);
        Map<String, AnexoBoletin> mapAnexoSuper = TypesUtil.convertListToMap("codigo", anexosSuper);

        for (ReporteOficina reporte : reportesAll) {
            if (reporte.getTipoCiclo() != null) {
                if (ciclo.getTipoEnum() == reporte.getTipoCicloEnum()) {
                } else {
                    continue;
                }
            }
            if (reporte.getTipoModalidad() != null) {
                if (reporte.getTipoModalidadEnum() == ModalidadEstudioEnum.PRE) {
                    AnexoBoletin anexo = mapAnexoSuper.get(CodigoAnexoBoletinEnum.G01.name());
                    if (anexo != null) {
                        reportes.add(reporte);
                        continue;
                    }
                    anexo = mapAnexoSuper.get(CodigoAnexoBoletinEnum.G02.name());
                    if (anexo != null) {
                        reportes.add(reporte);
                        continue;
                    }
                    anexo = mapAnexoSuper.get(CodigoAnexoBoletinEnum.G03.name());
                    if (anexo != null) {
                        reportes.add(reporte);
                        continue;
                    }

                } else if (reporte.getTipoModalidadEnum() == ModalidadEstudioEnum.EPG) {
                    AnexoBoletin anexoEpg = mapAnexoSuper.get(CodigoAnexoBoletinEnum.G04.name());
                    if (anexoEpg != null) {
                        reportes.add(reporte);
                        continue;
                    }
                }
                continue;

            }
            reportes.add(reporte);
        }

        return reportes;
    }

}
