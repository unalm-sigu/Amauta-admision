package pe.edu.lamolina.pivot.controller.ingresante.hojarecorrido;

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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfigRecorridoIngresante;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.RecorridoIngresanteEstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.ActividadIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.ConfigRecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.RecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoActividadIngresanteDAO;

@Service
@Transactional(readOnly = true)
public class HojaRecorridoServiceImp implements HojaRecorridoService {

    @Autowired
    ActividadIngresanteDAO actividadIngresanteDAO;

    @Autowired
    RecorridoIngresanteDAO recorridoIngresanteDAO;

    @Autowired
    TipoActividadIngresanteDAO tipoActividadIngresanteDAO;

    @Autowired
    ConfigRecorridoIngresanteDAO configRecorridoIngresanteDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<RecorridoIngresante> allRecorridoIngresante(DynatableFilter filter, CicloAcademico ciclo) {
        
        logger.debug("CICLO {}",ciclo.getId());

        List<RecorridoIngresante> recorridoIngresantes = recorridoIngresanteDAO.allByDynatableCiclo(filter, ciclo);

        List<ConfigRecorridoIngresante> configRecorridoIngresantes = configRecorridoIngresanteDAO.allByCicloAcademico(ciclo);

        List<TipoActividadIngresante> tiposActividad = configRecorridoIngresantes.stream()
                .map(ConfigRecorridoIngresante::getTipoActividadIngresante)
                .collect(Collectors.toList());

        List<ActividadIngresante> actividades = actividadIngresanteDAO.allByRecorridoIngresantes(recorridoIngresantes);

        Map<Long, List<ActividadIngresante>> mapActividadByRecorrido = TypesUtil.convertListToMapList("recorridoIngresante.id", actividades);

        for (RecorridoIngresante recorridoIngresante : recorridoIngresantes) {

            List<ActividadIngresante> actividadesAlumnoAntes = mapActividadByRecorrido.get(recorridoIngresante.getId());
            actividadesAlumnoAntes = (actividadesAlumnoAntes == null) ? new ArrayList() : actividadesAlumnoAntes;
            Map<Long, ActividadIngresante> mapActividad = TypesUtil.convertListToMap("tipoActividadIngresante.id", actividadesAlumnoAntes);

            recorridoIngresante.setActividadIngresante(actividadesAlumnoAntes);
            List<ActividadIngresante> actividadesAlumno = new ArrayList();

            for (TipoActividadIngresante tipoActividad : tiposActividad) {
                ActividadIngresante actividad = mapActividad.get(tipoActividad.getId());

                if (actividad == null) {
                    actividad = new ActividadIngresante();
                    actividad.setTipoActividadIngresante(tipoActividad);
                    actividad.setRecorridoIngresante(recorridoIngresante);
                    actividad.setFechaEjecucion(new Date());
                    actividad.setFechaRegistro(new Date());
                    actividad.setEstadoEnum(RecorridoIngresanteEstadoEnum.PEND);
                }
                actividadesAlumno.add(actividad);
            }
            recorridoIngresante.setActividadIngresante(actividadesAlumno);
        }
        return recorridoIngresantes;
    }

    @Override
    public List<TipoActividadIngresante> allTipoActividadIngresante() {
         return tipoActividadIngresanteDAO.all();
    }

}
