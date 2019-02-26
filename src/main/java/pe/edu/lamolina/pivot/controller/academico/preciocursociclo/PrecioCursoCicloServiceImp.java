package pe.edu.lamolina.pivot.controller.academico.preciocursociclo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PrecioCursoEstructuraDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoCarpetaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PrecioCursoCicloServiceImp implements PrecioCursoCicloService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    PrecioCursoEstructuraDAO precioCursoEstructuraDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    TipoCarpetaDAO tipoCarpetaDAO;

    @Override
    public List<CursoCicloAcademico> allCursoCiclo(DynatableFilter filter, CicloAcademico ciclo) {
        List<CursoCicloAcademico> cursosCiclo = cursoCicloAcademicoDAO.allByDynatable(filter, ciclo);

        List<Curso> cursos = cursosCiclo.stream().map(x -> x.getCurso()).collect(Collectors.toList());

        List<CursoCicloAcademico> cursosCicloCount = cursoCicloAcademicoDAO.countGpoSeccByCursosCiclo(cursos, ciclo);
        Map<Long, CursoCicloAcademico> mapCursoCicloCount = TypesUtil.convertListToMap("id", cursosCicloCount);

        for (CursoCicloAcademico cursoCiclo : cursosCiclo) {
            CursoCicloAcademico cursoCicloCount = mapCursoCicloCount.get(cursoCiclo.getCurso().getId());
            if (cursoCicloCount == null) {
                cursoCiclo.setCantidadGpoSecc(0L);
            } else {
                cursoCiclo.setCantidadGpoSecc(cursoCicloCount.getCantidadGpoSecc());
            }
        }

        return cursosCiclo;
    }

    @Override
    @Transactional
    public void save(List<CursoCicloAcademico> cursosCicloForm, CicloAcademico ciclo, DataSessionPivot ds) {
        List<CursoCicloAcademico> cursosCicloBD = cursoCicloAcademicoDAO.allByLista(cursosCicloForm);
        Map<Long, CursoCicloAcademico> mapCursoCiclo = TypesUtil.convertListToMap("id", cursosCicloBD);

        Map<Long, Curso> mapCurso = TypesUtil.convertListToMap("curso.id", "curso", cursosCicloBD);

        List<String> tpcs = new ArrayList();
        List<Curso> cursosBD = new ArrayList(mapCurso.values());
        for (Curso curso : cursosBD) {
            tpcs.add(curso.getTpc());
        }

        List<PrecioCursoEstructura> preciosTpcCursos = precioCursoEstructuraDAO.allByEstructurasCiclo(tpcs, ciclo);
        Map<String, PrecioCursoEstructura> mapPrecioTPC = TypesUtil.convertListToMap("tpc", preciosTpcCursos);

        for (CursoCicloAcademico cursoCicloForm : cursosCicloForm) {
            CursoCicloAcademico cursoCicloBD = mapCursoCiclo.get(cursoCicloForm.getId());
            PrecioCursoEstructura precioTPC = mapPrecioTPC.get(cursoCicloBD.getCurso().getTpc());

            if (precioTPC.getPrecio().compareTo(cursoCicloForm.getPrecio()) == 0) {
                cursoCicloBD.setPrecioPersonalizado(Boolean.FALSE);
                cursoCicloBD.setUserPrecio(null);
                cursoCicloBD.setFechaPrecio(null);
            } else {
                if (!cursoCicloBD.getPrecioPersonalizado()) {
                    cursoCicloBD.setPrecioPersonalizado(Boolean.TRUE);
                    cursoCicloBD.setUserPrecio(ds.getUsuario());
                    cursoCicloBD.setFechaPrecio(new Date());
                }
            }

            cursoCicloBD.setPrecio(cursoCicloForm.getPrecio());
            cursoCicloBD.setPrecioAdicional(cursoCicloForm.getPrecioAdicional());
            cursoCicloBD.setMinimoAlumnos(cursoCicloForm.getMinimoAlumnos());
            cursoCicloAcademicoDAO.update(cursoCicloBD);

            Curso curso = cursoCicloBD.getCurso();

            List<GrupoSeccion> gpoSecciones = grupoSeccionDAO.allActivoByCursoCiclo(curso, ciclo);
            List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gpoSecciones);

            for (Seccion seccion : secciones) {
                if (seccion.isTipoSeccionTCUR()) {
                    continue;
                }

                if (seccion.getPrecioPersonalizado()) {
                    continue;
                }

                seccion.setPrecioPersonalizado(Boolean.FALSE);
                seccion.setUserPrecio(null);
                seccion.setFechaPrecio(null);
                seccion.setPrecio(cursoCicloForm.getPrecio().add(cursoCicloForm.getPrecioAdicional()));
                seccionDAO.updatePrecioBySeccion(seccion);
            }

        }
    }

    @Override
    @Transactional
    public void configurarcantidad(CantidadAlumno cantidad, CicloAcademico cicloAcademico) {
        if (cantidad.getCarrera() == null || cantidad.getGeneral() == null) {
            throw new PhobosException("Debe especificar una cantidad Mayor a cero");
        }
        boolean valid = cantidad.getCarrera() < 1 || cantidad.getGeneral() < 1;
        if (valid) {
            throw new PhobosException("Debe especificar una cantidad Mayor a cero");
        }
        List<CursoCicloAcademico> cursosCiclo = cursoCicloAcademicoDAO.allByCiclo(cicloAcademico);

        List<Curso> cursos = cursosCiclo.stream().map(x -> x.getCurso()).collect(Collectors.toList());
        List<Seccion> secciones = seccionDAO.allActivosByCursosCiclo(cursos, cicloAcademico);
        Map<Long, List<Seccion>> seccionesXcurso = TypesUtil.convertListToMapList("grupoSeccion.curso.id", secciones);

        TipoCursoCurricula tipocursogeneral = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);
        TipoCursoCurricula tipocursoobligatorio = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.OBL);

        for (CursoCicloAcademico cursoCicloBD : cursosCiclo) {
            boolean esGral = cursoCicloBD.getTipoCursoCurricula().getId() == tipocursogeneral.getId().longValue();
            boolean esCarr = cursoCicloBD.getTipoCursoCurricula().getId() == tipocursoobligatorio.getId().longValue();
            Long cantid = esGral ? cantidad.getGeneral() : (esCarr ? cantidad.getCarrera() : 0L);
            cursoCicloBD.setMinimoAlumnos(new BigDecimal(cantid));

            List<Seccion> seccioness = seccionesXcurso.get(cursoCicloBD.getCurso().getId());
            if (seccioness == null) {
                continue;
            }

            for (Seccion seccion : seccioness) {
                if (seccion.getTipoSeccionEnum() != TipoSeccionEnum.TCUR) {
                    seccion.setPrecioBase(cursoCicloBD.getMinimoAlumnos().multiply(seccion.getPrecio()));
                    seccionDAO.update(seccion);
                }
            }

            cursoCicloAcademicoDAO.update(cursoCicloBD);
        }
    }

    @Override
    public List<TipoCarpeta> allTipoCarpeta() {
        return tipoCarpetaDAO.all();
    }

    @Override
    @Transactional
    public void update(CursoCicloAcademico cursoCicloAcademicoForm, DataSessionPivot ds) {

        CursoCicloAcademico cursoCicloAcademicoBD = cursoCicloAcademicoDAO.find(cursoCicloAcademicoForm.getId());
        cursoCicloAcademicoBD.setTipoCarpetaTeoria(cursoCicloAcademicoForm.getTipoCarpetaTeoria());
        cursoCicloAcademicoBD.setTipoCarpetaPractica(cursoCicloAcademicoForm.getTipoCarpetaPractica());
        cursoCicloAcademicoDAO.update(cursoCicloAcademicoBD);
    }
}
