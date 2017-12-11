package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoPlanCalificaEnum;
import pe.edu.lamolina.pivot.zelper.enums.GrupoAnexoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;

@Service
@Transactional(readOnly = true)
public class GpoSeccionServiceImp implements GpoSeccionService {

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Override
    public List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<GrupoSeccion> gsecciones = grupoSeccionDAO.allByDynatable(filter, ciclo);
        List<Seccion> secciones = seccionDAO.allByGposSeccion(gsecciones);

        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);
        for (GrupoSeccion gseccion : gsecciones) {
            gseccion.setSecciones(mapSecciones.get(gseccion.getId()));
        }

        List<DocenteSeccion> docenteSeccion = docenteSeccionDAO.allBySecciones(secciones);

        Map<Long, List<DocenteSeccion>> mapDocSeccion = TypesUtil.convertListToMapList("seccion.id", docenteSeccion);

        for (Seccion seccion : secciones) {
            seccion.setDocenteSeccion(mapDocSeccion.get(seccion.getId()));
        }

        return gsecciones;
    }

    @Override
    @Transactional(readOnly = false)
    public GrupoSeccion saveGpoSeccionHeader(GrupoSeccion grupoSeccion, CicloAcademico cicloAcademico) {
        GrupoSeccion lastGrupoSeccion = grupoSeccionDAO.findLast();
        String codigo = generateCodigo(lastGrupoSeccion.getCodigo());
        grupoSeccion.setCodigo(codigo);
        grupoSeccion.setVersion(BigDecimal.ONE.toString());
        grupoSeccion.setEstadoGrupoEnum(EstadoGrupoSeccionEnum.ABI);
        grupoSeccion.setEstadoPlanEnum(EstadoPlanCalificaEnum.PEND);
        grupoSeccion.setCicloAcademico(cicloAcademico);
        Curso curso = cursoDAO.find(grupoSeccion.getCurso().getId());

        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);

        DateTime today = new DateTime();

        grupoSeccion.setSecciones(new ArrayList<Seccion>());
        if (curso.isTipoCursoTEO()) {
            Seccion seccionTEO = new Seccion();
            seccionTEO.setGrupoSeccion(grupoSeccion);
            seccionTEO.setCodigo(codigo + "0");
            seccionTEO.setCodigo2(seccionTEO.getCodigo());
            seccionTEO.setEstadoEnum(EstadoEnum.CRE);
            seccionTEO.setTipoSeccionEnum(TipoSeccionEnum.TEO);
            seccionTEO.setHorasPractica(curso.getHorasPractica());
            seccionTEO.setHorasTeoria(curso.getHorasTeoria());

            seccionTEO.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion = new DocenteSeccion();
            docenteSeccion.setDocente(docenteDefault);
            docenteSeccion.setCodigoSeccion(seccionTEO.getCodigo());
            docenteSeccion.setEstado(EstadoEnum.ACT.name());
            docenteSeccion.setFechaInicio(cicloAcademico.getFechaRegistro());
            docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
            docenteSeccion.setSeccion(seccionTEO);
            docenteSeccion.setPorcentajeCarga(BigDecimal.valueOf(100));
            seccionTEO.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionTEO);
        }
        if (curso.isTipoCursoPRA()) {
            Seccion seccionPRA = new Seccion();
            seccionPRA.setGrupoSeccion(grupoSeccion);
            seccionPRA.setCodigo(codigo + "1");
            seccionPRA.setCodigo2(seccionPRA.getCodigo());
            seccionPRA.setEstadoEnum(EstadoEnum.CRE);
            seccionPRA.setTipoSeccionEnum(TipoSeccionEnum.PRA);
            seccionPRA.setHorasPractica(curso.getHorasPractica());
            seccionPRA.setHorasTeoria(curso.getHorasTeoria());

            seccionPRA.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion = new DocenteSeccion();
            docenteSeccion.setDocente(docenteDefault);
            docenteSeccion.setCodigoSeccion(seccionPRA.getCodigo());
            docenteSeccion.setEstado(EstadoEnum.ACT.name());
            docenteSeccion.setFechaInicio(cicloAcademico.getFechaRegistro());
            docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
            docenteSeccion.setSeccion(seccionPRA);
            docenteSeccion.setPorcentajeCarga(BigDecimal.valueOf(100));
            seccionPRA.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionPRA);
        }
        if (curso.isTipoCursoTEOPRA()) {
            Seccion seccionTCUR = new Seccion();
            seccionTCUR.setGrupoSeccion(grupoSeccion);
            seccionTCUR.setCodigo(codigo + "0");
            seccionTCUR.setCodigo2(seccionTCUR.getCodigo());
            seccionTCUR.setEstadoEnum(EstadoEnum.CRE);
            seccionTCUR.setTipoSeccionEnum(TipoSeccionEnum.TCUR);
            seccionTCUR.setHorasPractica(curso.getHorasPractica());
            seccionTCUR.setHorasTeoria(curso.getHorasTeoria());

            seccionTCUR.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion = new DocenteSeccion();
            docenteSeccion.setDocente(docenteDefault);
            docenteSeccion.setCodigoSeccion(seccionTCUR.getCodigo());
            docenteSeccion.setEstado(EstadoEnum.ACT.name());
            docenteSeccion.setFechaInicio(cicloAcademico.getFechaRegistro());
            docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
            docenteSeccion.setSeccion(seccionTCUR);
            docenteSeccion.setPorcentajeCarga(BigDecimal.valueOf(50));
            seccionTCUR.getDocenteSeccion().add(docenteSeccion);

            grupoSeccion.getSecciones().add(seccionTCUR);

            Seccion seccionPCUR = new Seccion();
            seccionPCUR.setGrupoSeccion(grupoSeccion);
            seccionPCUR.setCodigo(codigo + "1");
            seccionPCUR.setCodigo2(seccionPCUR.getCodigo());
            seccionPCUR.setEstadoEnum(EstadoEnum.CRE);
            seccionPCUR.setTipoSeccionEnum(TipoSeccionEnum.PCUR);
            seccionPCUR.setHorasPractica(curso.getHorasPractica());
            seccionPCUR.setHorasTeoria(curso.getHorasTeoria());

            seccionPCUR.setDocenteSeccion(new ArrayList<>());
            DocenteSeccion docenteSeccion2 = new DocenteSeccion();
            docenteSeccion2.setDocente(docenteDefault);
            docenteSeccion2.setCodigoSeccion(seccionPCUR.getCodigo());
            docenteSeccion2.setEstado(EstadoEnum.ACT.name());
            docenteSeccion2.setFechaInicio(cicloAcademico.getFechaRegistro());
            docenteSeccion2.setPrincipal(BigDecimal.ONE.intValue());
            docenteSeccion2.setSeccion(seccionPCUR);
            docenteSeccion2.setPorcentajeCarga(BigDecimal.valueOf(50));
            seccionPCUR.getDocenteSeccion().add(docenteSeccion2);

            grupoSeccion.getSecciones().add(seccionPCUR);
        }
        grupoSeccionDAO.save(grupoSeccion);
        return grupoSeccion;
    }

    @Override
    @Transactional(readOnly = false)
    public void addSeccion(GrupoSeccion grupoSeccion) {
        grupoSeccion = grupoSeccionDAO.find(grupoSeccion.getId());
        Curso curso = grupoSeccion.getCurso();
        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        List<Seccion> secciones = seccionDAO.allByGposSeccion(grupoSeccion);
        DateTime today = new DateTime();

        Seccion seccionPCUR = new Seccion();
        seccionPCUR.setGrupoSeccion(grupoSeccion);
        seccionPCUR.setCodigo(grupoSeccion.getCodigo() + (secciones.size() + 1));
        seccionPCUR.setCodigo2(seccionPCUR.getCodigo());
        seccionPCUR.setEstadoEnum(EstadoEnum.CRE);
        seccionPCUR.setTipoSeccionEnum(TipoSeccionEnum.PCUR);
        seccionPCUR.setHorasPractica(curso.getHorasPractica());
        seccionPCUR.setHorasTeoria(curso.getHorasTeoria());

        seccionPCUR.setDocenteSeccion(new ArrayList<>());
        DocenteSeccion docenteSeccion2 = new DocenteSeccion();
        docenteSeccion2.setDocente(docenteDefault);
        docenteSeccion2.setCodigoSeccion(seccionPCUR.getCodigo());
        docenteSeccion2.setEstado(EstadoEnum.ACT.name());
        docenteSeccion2.setFechaInicio(today.toDate());
        docenteSeccion2.setPrincipal(BigDecimal.ONE.intValue());
        docenteSeccion2.setSeccion(seccionPCUR);
        seccionPCUR.getDocenteSeccion().add(docenteSeccion2);

        seccionDAO.save(seccionPCUR);

    }

    @Override
    @Transactional(readOnly = false)
    public void addDocenteSeccion(Seccion seccion) {
        seccion = seccionDAO.find(seccion.getId());

        Docente docenteDefault = docenteDAO.findByCode(Constantine.DOCENTE_INDETERMINADO);
        DateTime today = new DateTime();

        DocenteSeccion docenteSeccion = new DocenteSeccion();
        docenteSeccion.setDocente(docenteDefault);
        docenteSeccion.setCodigoSeccion(seccion.getCodigo());
        docenteSeccion.setEstado(EstadoEnum.ACT.name());
        docenteSeccion.setFechaInicio(today.toDate());
        docenteSeccion.setPrincipal(BigDecimal.ZERO.intValue());
        docenteSeccion.setSeccion(seccion);
        docenteSeccionDAO.save(docenteSeccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSeccion(Seccion seccion) {
        seccion = seccionDAO.find(seccion.getId());
        List<Seccion> secciones = seccionDAO.allByGposSeccion(seccion.getGrupoSeccion());
        if (secciones.size() == 1) {
            throw new PhobosException("No se pueden eliminar todas las secciones del grupo");
        }
        //docenteSeccionDAO.deleteDocenteSeccionBySeccion(seccion);
        List<DocenteSeccion> docentesSec = docenteSeccionDAO.allBySeccion(seccion);
        for (DocenteSeccion docenteSeccion : docentesSec) {
            docenteSeccionDAO.delete(docenteSeccion);
        }
        seccionDAO.delete(seccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDocSeccion(DocenteSeccion docenteSeccion) {
        docenteSeccion = docenteSeccionDAO.find(docenteSeccion.getId());
        docenteSeccionDAO.delete(docenteSeccion);
    }

    @Override
    public List<DocenteSeccion> allDocentesSeccionBySeccion(Seccion seccion) {
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(seccion);
        return docentesSeccion;
    }

    public static String generateCodigo(String codigo) {
        if (StringUtils.isBlank(codigo)) {
            return "001";
        }
        String letterPart = codigo.substring(0, 1);
        Integer numericPart = Integer.parseInt(codigo.substring(1, 3));

        if (numericPart == 99) {
            if (StringUtils.isNumeric(letterPart)) {
                Integer letterPartInt = Integer.parseInt(letterPart);
                if (letterPartInt < 9) {
                    letterPartInt++;
                    letterPart = letterPartInt + "";
                } else {
                    letterPart = "A";
                }
            } else {
                int charValue = letterPart.charAt(0);
                letterPart = String.valueOf((char) (charValue + 1));
            }
            numericPart = 0;
        }
        numericPart++;
        return letterPart + String.format("%02d", numericPart);
    }

    @Override
    public List<AnexoBoletin> allAnexosSuperiores() {
        return anexoBoletinDAO.allAnexosSuperiores();
    }

    @Override
    public GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo) {
        return grupoSeccionDAO.resumenByCiclo(ciclo);
    }

    @Override
    public List<Curso> allCursosForProgramacion(String nomString) {
        return cursoDAO.allForProgramacion(nomString);
    }

    @Override
    public List<AnexoBoletin> allAnexoBoletionHijos() {
        return anexoBoletinDAO.allAnexosHijos();
    }

    @Override
    public AnexoBoletin findAnexoBoletin(Long idAnexoBoletin) {
        return anexoBoletinDAO.find(idAnexoBoletin);
    }

    @Override
    public Curso findCurso(Long id) {
        return cursoDAO.find(id);
    }

    @Override
    public GrupoSeccion findGpoSeccion(Long id) {
        return grupoSeccionDAO.find(id);
    }

    @Override
    public List<Seccion> allSeccionesByGrupo(GrupoSeccion grupoSeccion) {
        List<Seccion> secciones = seccionDAO.allByGposSeccion(grupoSeccion);
        for (Seccion seccion : secciones) {
            seccion.getDocenteSeccion().size();
        }
        return secciones;
    }

    @Override
    public List<Docente> allDocenterByNombre(String nombre) {
        return docenteDAO.allByNombreFilter(nombre, 10);
    }

    @Override
    @Transactional(readOnly = false)
    public void cambiarDocentePrincipal(DocenteSeccion docenteSeccion) {
        docenteSeccion = docenteSeccionDAO.find(docenteSeccion.getId());
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allBySeccion(docenteSeccion.getSeccion());
        for (DocenteSeccion docenteSeccionEach : docentesSeccion) {
            docenteSeccionEach.setPrincipal(BigDecimal.ZERO.intValue());
            docenteSeccionDAO.updatePrincipal(docenteSeccionEach);
        }
        docenteSeccion.setPrincipal(BigDecimal.ONE.intValue());
        docenteSeccionDAO.updatePrincipal(docenteSeccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void actualizarDocente(Long docenteSeccionId, Long docenteId) {
        DocenteSeccion docenteSeccion = new DocenteSeccion(docenteSeccionId);
        docenteSeccion.setDocente(new Docente(docenteId));
        docenteSeccionDAO.updateDocente(docenteSeccion);
    }

    @Override
    @Transactional(readOnly = false)
    public void updatePorcentajeAvance(DocenteSeccion docenteSeccion) {
        docenteSeccionDAO.updatePorcentajeAvance(docenteSeccion);
    }

    @Override
    public List<AnexoBoletin> allAnexosBySuperiorCiclo(String anexoSuperior, CicloAcademico ciclo) {
        GrupoAnexoEnum gpoAnexoE = GrupoAnexoEnum.get2(anexoSuperior);
        System.out.println(gpoAnexoE.name());
        System.out.println(gpoAnexoE.getValue());

        return anexoBoletinDAO.allBySuperiorCiclo(new AnexoBoletin(gpoAnexoE.getValue()), ciclo);
    }

}
