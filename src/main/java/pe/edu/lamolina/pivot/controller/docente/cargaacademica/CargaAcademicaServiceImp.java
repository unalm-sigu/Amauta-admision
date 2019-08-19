package pe.edu.lamolina.pivot.controller.docente.cargaacademica;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;

@Service
@Transactional(readOnly = true)
public class CargaAcademicaServiceImp implements CargaAcademicaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;
    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;
    @Autowired
    SeccionDAO seccionDAO;

    @Override
    public List<GrupoSeccion> allGpoSecciones(Docente docente, CicloAcademico ciclo) {
        Map<Long, GrupoSeccion> mapGpoSecc = new LinkedHashMap();
        List<Seccion> secciones = new ArrayList();

        List<DocenteSeccion> profeSecciones = docenteSeccionDAO.allActivosByDocenteCiclo(docente, ciclo);
        for (DocenteSeccion profeSecc : profeSecciones) {
            Seccion secc = profeSecc.getSeccion();
            secc.setDocenteSeccion(new ArrayList());
            secc.getDocenteSeccion().add(profeSecc);
            GrupoSeccion gpoSeccBD = secc.getGrupoSeccion();
            GrupoSeccion gpoSecc = mapGpoSecc.get(gpoSeccBD.getId());
            if (gpoSecc == null) {
                mapGpoSecc.put(gpoSeccBD.getId(), gpoSeccBD);
                gpoSecc = gpoSeccBD;
                gpoSecc.setSecciones(new ArrayList());
            }
            gpoSecc.getSecciones().add(secc);
            secc.setGrupoSeccion(gpoSecc);
            secciones.add(secc);
        }

        List<HorarioSeccion> horarios = horarioSeccionDAO.allBySecciones(secciones);
        Map<Long, List<HorarioSeccion>> mapHorarios = TypesUtil.convertListToMapList("seccion.id", horarios);

        for (Seccion secc : secciones) {
            List<HorarioSeccion> horariosSeccion = TypesUtil.getListNotNull(mapHorarios.get(secc.getId()));
            secc.setHorarioSeccion(horariosSeccion);
        }

        return new ArrayList(mapGpoSecc.values());
    }

    @Override
    public Seccion findSeccion(Long idSeccion) {
        return seccionDAO.find(new Seccion(idSeccion));
    }

}
