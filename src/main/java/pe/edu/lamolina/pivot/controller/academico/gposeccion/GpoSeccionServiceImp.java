package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

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

    @Override
    public List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<GrupoSeccion> gsecciones = grupoSeccionDAO.allByDynatable(filter, cicloAcademico);
        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gsecciones);

        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);
        for (GrupoSeccion gseccion : gsecciones) {
            gseccion.setSecciones(mapSecciones.get(gseccion.getId()));
        }
        
        List<DocenteSeccion> docenteSeccion = docenteSeccionDAO.allActivosBySecciones(secciones);

        Map<Long, List<DocenteSeccion>> mapDocSeccion = TypesUtil.convertListToMapList("seccion.id", docenteSeccion);

        for (Seccion seccion : secciones) {
            seccion.setDocenteSeccion(mapDocSeccion.get(seccion.getId()));
        }

        return gsecciones;
    }

    @Override
    public GpoSeccionResumen resumen() {
        return grupoSeccionDAO.resumen();
    }

}
