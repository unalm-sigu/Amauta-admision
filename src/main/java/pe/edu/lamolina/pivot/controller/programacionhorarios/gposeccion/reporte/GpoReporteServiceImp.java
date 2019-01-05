package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static javax.management.Query.attr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;

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

    @Override
    public CicloAcademico findCiclo(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }

    @Override
    public GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo) {
        GpoSeccionResumen resumen = grupoSeccionDAO.resumenByCiclo(ciclo);
        resumen.setActividades(resumen.getActividades() == null ? 0 : resumen.getActividades());
        resumen.setDepartamentos(resumen.getDepartamentos() == null ? 0 : resumen.getDepartamentos());
        resumen.setIngresantes(resumen.getIngresantes() == null ? 0 : resumen.getIngresantes());
        resumen.setPostGrados(resumen.getPostGrados() == null ? 0 : resumen.getPostGrados());
        return resumen;
    }

    @Override
    public List<DepartamentoAcademico> allDepartamentoAcademico(CicloAcademico cicloAcademico) {

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);

        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allSinNNByCicloModalidad(cicloAcademico, modalidad);

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

    @Override
    public List<DepartamentoAcademico> allDepartamentoAcademicoXcurso(CicloAcademico cicloAcademico) {

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);

        List<DocenteSeccion> docenteSeccions = docenteSeccionDAO.allSinNNByCicloModalidad(cicloAcademico, modalidad);

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
        }

        return departamentosMap.values().stream().collect(Collectors.toList());

    }
}
