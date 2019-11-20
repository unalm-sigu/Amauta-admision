package pe.edu.lamolina.pivot.controller.academico.preciocursoestructura;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.PrecioCursoEstructuraDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PrecioCursoEstructuraServiceImp implements PrecioCursoEstructuraService {

    @Autowired
    PrecioCursoEstructuraDAO precioCursoEstructuraDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;

    @Override
    public List<PrecioCursoEstructura> allByCicloAcademico(CicloAcademico ciclo) {
        return precioCursoEstructuraDAO.allByCiclo(ciclo);
    }

    @Override
    @Transactional
    public void saveAll(List<PrecioCursoEstructura> listaPrecios, CicloAcademico ciclo, DataSessionPivot ds) {
        Map<Long, PrecioCursoEstructura> mapPrecios = listaPrecios.stream().collect(Collectors.toMap(PrecioCursoEstructura::getId, x -> x));
        List<PrecioCursoEstructura> preciosBD = precioCursoEstructuraDAO.all(new ArrayList(mapPrecios.keySet()));

        List<CursoCicloAcademico> cursosCiclo = cursoCicloAcademicoDAO.allByCiclo(ciclo);
        Map<Long, CursoCicloAcademico> mapCursoCiclo = TypesUtil.convertListToMap("curso.id", cursosCiclo);
        Map<String, List<CursoCicloAcademico>> mapCursoCicloByTpc = TypesUtil.convertListToMapList("curso.tpc", cursosCiclo);

        List<Seccion> secciones = seccionDAO.allByCiclo(ciclo, SeccionEstadoEnum.ACT);
        Map<String, List<Seccion>> mapSeccionByTpc = TypesUtil.convertListToMapList("grupoSeccion.curso.tpc", secciones);
        List<Seccion> seccionesUps = new ArrayList();
        List<CursoCicloAcademico> cursosCicloUpd = new ArrayList();

        for (PrecioCursoEstructura item : preciosBD) {
            PrecioCursoEstructura precioTpc = mapPrecios.get(item.getId());
            item.setPrecio(precioTpc.getPrecio());

            item.setUserPrecio(ds.getUsuario());
            item.setFechaPrecio(new Date());

            precioCursoEstructuraDAO.update(item);

            List<CursoCicloAcademico> cursosCicloByTpc = TypesUtil.getListNotNull(mapCursoCicloByTpc.get(item.getTpc()));
            for (CursoCicloAcademico cc : cursosCicloByTpc) {
                CursoCicloAcademico ccUpd = new CursoCicloAcademico(cc.getId());
                ccUpd.setPrecio(item.getPrecio());
                cursosCicloUpd.add(ccUpd);
            }

            List<Seccion> seccionesByTpc = TypesUtil.getListNotNull(mapSeccionByTpc.get(item.getTpc()));
            for (Seccion seccion : seccionesByTpc) {
                if (seccion.getTipoSeccionEnum() != TipoSeccionEnum.TCUR) {
                    CursoCicloAcademico cursoCiclo = mapCursoCiclo.get(seccion.getGrupoSeccion().getCurso().getId());
                    BigDecimal cantidadMin = cursoCiclo.getMinimoAlumnos() == null ? BigDecimal.ZERO : cursoCiclo.getMinimoAlumnos();

                    Seccion seccionUpd = new Seccion(seccion.getId());
                    seccionUpd.setPrecio(item.getPrecio());
                    seccionUpd.setPrecioBase(cantidadMin.multiply(seccion.getPrecio()));
                    seccionUpd.setAbonoVerano(TypesUtil.ifNull(seccion.getAbonoVerano(), BigDecimal.ZERO));
                    seccionUpd.setDescuentoPrecio(TypesUtil.ifNull(seccion.getDescuentoPrecio(), BigDecimal.ZERO));
                    seccionesUps.add(seccionUpd);
                }
            }
        }

        cursoCicloAcademicoDAO.updateList(cursosCicloUpd, "precio");
        seccionDAO.updateList(seccionesUps, "precio", "precioBase", "abonoVerano", "descuentoPrecio");
    }

}
