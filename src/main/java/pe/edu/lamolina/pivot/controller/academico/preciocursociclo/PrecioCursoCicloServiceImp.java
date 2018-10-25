package pe.edu.lamolina.pivot.controller.academico.preciocursociclo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PrecioCursoCicloServiceImp implements PrecioCursoCicloService {

    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

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
        for (CursoCicloAcademico cursoCicloForm : cursosCicloForm) {
            CursoCicloAcademico cursoCicloBD = cursoCicloAcademicoDAO.find(cursoCicloForm.getId());

            if (cursoCicloBD.getPrecio().compareTo(cursoCicloForm.getPrecio()) == 0) {
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
                seccion.setPrecio(cursoCicloForm.getPrecio().add(cursoCicloForm.getPrecioAdicional()));
                seccionDAO.update(seccion);
            }

        }
    }

}
