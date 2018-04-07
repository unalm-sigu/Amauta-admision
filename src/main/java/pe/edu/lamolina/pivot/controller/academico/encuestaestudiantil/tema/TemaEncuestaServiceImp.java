package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.tema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.EstadoBloqueEnum;
import pe.edu.lamolina.model.enums.EstadoSubTituloEnum;
import pe.edu.lamolina.model.enums.EstadoTemaEnum;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;
import pe.edu.lamolina.pivot.dao.encuesta.BloquePreguntasDAO;
import pe.edu.lamolina.pivot.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.pivot.dao.encuesta.SubTituloExamenDAO;
import pe.edu.lamolina.pivot.dao.encuesta.TemaExamenVirtualDAO;

@Service
@Transactional(readOnly = true)
public class TemaEncuestaServiceImp implements TemaEncuestaService {

    @Autowired
    TemaExamenVirtualDAO temaEvaluacionVirtualDAO;

    @Autowired
    SubTituloExamenDAO subTituloEvaluacionVirtualDAO;

    @Autowired
    BloquePreguntasDAO bloqueEvaluacionVirtualDAO;

    @Autowired
    ExamenVirtualDAO evaluacionVirtualDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<TemaExamenVirtual> allTema(ExamenVirtual examenVirtual) {

        List<TemaExamenVirtual> temas = temaEvaluacionVirtualDAO.allByEvaluacion(examenVirtual);
        List<SubTituloExamen> subTitulos = subTituloEvaluacionVirtualDAO.allByTemas(temas);
        List<BloquePreguntas> bloques = bloqueEvaluacionVirtualDAO.allBysubtitulos(subTitulos);

        Map<Long, List<BloquePreguntas>> mapBloques = TypesUtil.convertListToMapList("subTituloExamen.id", bloques);

        subTitulos.forEach((subtitulo) -> {
            List<BloquePreguntas> bloquess = mapBloques.get(subtitulo.getId());
            if (bloquess == null) {
                bloquess = new ArrayList<>();
            }
            subtitulo.setBloquePreguntas(bloquess);
        });

        Map<Long, List<SubTituloExamen>> mapSubTitulos = TypesUtil.convertListToMapList("temaExamen.id", subTitulos);

        temas.forEach((tema) -> {
            List<SubTituloExamen> subtituloo = mapSubTitulos.get(tema.getId());
            if (subtituloo == null) {
                subtituloo = new ArrayList<>();
            }
            tema.setSubTituloEvaluacionVirtual(subtituloo);
        });

        return temas;

    }

    @Override
    @Transactional
    public void saveTema(TemaExamenVirtual tema) {

        tema.setEstado(EstadoTemaEnum.ACT.name());
        List<TemaExamenVirtual> temas = temaEvaluacionVirtualDAO.allByEvaluacion(tema.getExamenVirtual());
        Map<Integer, TemaExamenVirtual> mapTemas = TypesUtil.convertListToMap("orden", temas);

        Integer tamanoList = mapTemas.size();
        Integer ordenAsignado = 1;
        tema.setOrden(ordenAsignado);

        if (tamanoList < 1) {
            temaEvaluacionVirtualDAO.save(tema);
            return;
        }

        TemaExamenVirtual mapTemaUsado = mapTemas.get(ordenAsignado);

        while (mapTemaUsado != null) {
            if (mapTemaUsado.getEstado().equalsIgnoreCase(EstadoTemaEnum.INA.name())) {
                break;
            }
            ordenAsignado++;
            mapTemaUsado = mapTemas.get(ordenAsignado);
        }

        tema.setOrden(ordenAsignado);
        temaEvaluacionVirtualDAO.save(tema);

        while (mapTemaUsado != null) {
            ordenAsignado++;
            mapTemaUsado.setOrden(ordenAsignado);
            temaEvaluacionVirtualDAO.update(mapTemaUsado);
            mapTemaUsado = mapTemas.get(ordenAsignado);
        }
    }

    @Override
    @Transactional
    public void updateTema(TemaExamenVirtual tema) {
        TemaExamenVirtual temaEvaluacionVirtual = temaEvaluacionVirtualDAO.find(tema.getId());
        temaEvaluacionVirtual.setNombre(tema.getNombre());
        temaEvaluacionVirtual.setCodigo(tema.getCodigo());
        temaEvaluacionVirtual.setSubtitulosVisibles(tema.getSubtitulosVisibles());
        temaEvaluacionVirtual.setPreguntasVisibles(tema.getPreguntasVisibles());
        temaEvaluacionVirtualDAO.update(temaEvaluacionVirtual);
    }

    @Override
    @Transactional
    public void saveSubTitulo(SubTituloExamen subtitulo) {
        subtitulo.setEstado(EstadoSubTituloEnum.ACT.name());
        subtitulo.setOrden(0);
        subTituloEvaluacionVirtualDAO.save(subtitulo);
    }

    @Override
    @Transactional
    public void updateSubTitulo(SubTituloExamen subtitulo) {
        SubTituloExamen subTituloEvaluacionVirtual = subTituloEvaluacionVirtualDAO.find(subtitulo.getId());
        subTituloEvaluacionVirtual.setNombre(subtitulo.getNombre());
        subTituloEvaluacionVirtual.setBloquesVisibles(subtitulo.getBloquesVisibles());
        subTituloEvaluacionVirtual.setPreguntasVisibles(subtitulo.getPreguntasVisibles());
        subTituloEvaluacionVirtualDAO.update(subTituloEvaluacionVirtual);
    }

    @Override
    @Transactional
    public void updateBloque(BloquePreguntas bloque) {
        BloquePreguntas bloqueEvaluacionVirtual = bloqueEvaluacionVirtualDAO.find(bloque.getId());
        bloqueEvaluacionVirtual.setContenido(bloque.getContenido());
        bloqueEvaluacionVirtual.setNombre(bloque.getNombre());
        bloqueEvaluacionVirtual.setPreguntasVisibles(bloque.getPreguntasVisibles());
        bloqueEvaluacionVirtualDAO.update(bloqueEvaluacionVirtual);
    }

    @Override
    @Transactional
    public void saveBloque(BloquePreguntas bloque) {
        bloque.setEstado(EstadoBloqueEnum.ACT.name());
        bloqueEvaluacionVirtualDAO.save(bloque);
    }

    @Override
    @Transactional
    public void deleteSubTitulo(SubTituloExamen subtitulo) {
        subTituloEvaluacionVirtualDAO.delete(subtitulo);
    }

    @Override
    @Transactional
    public void deleteTema(TemaExamenVirtual tema) {
        temaEvaluacionVirtualDAO.delete(tema);
    }

    @Override
    @Transactional
    public void deleteBloque(BloquePreguntas bloque) {
        bloqueEvaluacionVirtualDAO.delete(bloque);
    }

    @Override
    public TemaExamenVirtual findTema(TemaExamenVirtual tema) {
        return temaEvaluacionVirtualDAO.find(tema.getId());
    }

    @Override
    public BloquePreguntas findBloque(BloquePreguntas bloque) {
        return bloqueEvaluacionVirtualDAO.find(bloque.getId());
    }

    @Override
    public SubTituloExamen findSubTitulo(SubTituloExamen subtitulo) {
        return subTituloEvaluacionVirtualDAO.find(subtitulo.getId());
    }

    @Override
    @Transactional
    public void itemSort(Integer itemSort, Long instancia, String tipo) {

        if ("TEMA".equalsIgnoreCase(tipo)) {
            this.itemSortTema(itemSort, instancia);
        }

        if ("SUBTITULO".equalsIgnoreCase(tipo)) {
            this.itemSortSubTitulo(itemSort, instancia);
        }
    }

    @Transactional
    private void itemSortTema(Integer itemSort, Long instancia) {

        TemaExamenVirtual temaEvaluacionVirtual = temaEvaluacionVirtualDAO.find(instancia);
        if (temaEvaluacionVirtual == null) {
            return;
        }

        Integer oldPlace = temaEvaluacionVirtual.getOrden();
        Integer newPlace = oldPlace + itemSort;

        if (newPlace < 1) {
            return;
        }

        TemaExamenVirtual temaEvaluacionVirtualUsurper = temaEvaluacionVirtualDAO.findByEvaluacionOrden(temaEvaluacionVirtual, newPlace);

        temaEvaluacionVirtual.setOrden(newPlace);
        temaEvaluacionVirtualDAO.update(temaEvaluacionVirtual);

        if (temaEvaluacionVirtualUsurper == null) {
            return;
        }

        temaEvaluacionVirtualUsurper.setOrden(oldPlace);
        temaEvaluacionVirtualDAO.update(temaEvaluacionVirtualUsurper);

    }

    @Transactional
    private void itemSortSubTitulo(Integer itemSort, Long instancia) {

        SubTituloExamen subTituloEvaluacionVirtual = subTituloEvaluacionVirtualDAO.find(instancia);
        if (subTituloEvaluacionVirtual == null) {
            return;
        }

        Integer oldPlace = subTituloEvaluacionVirtual.getOrden();
        Integer newPlace = oldPlace + itemSort;

        if (newPlace < 1) {
            return;
        }

        SubTituloExamen subTituloEvaluacionVirtualUsurper = subTituloEvaluacionVirtualDAO.findByTemaOrden(subTituloEvaluacionVirtual, newPlace);

        subTituloEvaluacionVirtual.setOrden(newPlace);
        subTituloEvaluacionVirtualDAO.update(subTituloEvaluacionVirtual);

        if (subTituloEvaluacionVirtualUsurper == null) {
            return;
        }

        subTituloEvaluacionVirtualUsurper.setOrden(oldPlace);
        subTituloEvaluacionVirtualDAO.update(subTituloEvaluacionVirtualUsurper);

    }

    @Override
    @Transactional
    public void estado(Long instancia, String tipo) {
        if ("TEMA".equalsIgnoreCase(tipo)) {
            this.changeEstadoTema(instancia);
        }
        if ("SUBTITULO".equalsIgnoreCase(tipo)) {
            this.changeEstadoSortSubTitulo(instancia);
        }
        if ("BLOQUE".equalsIgnoreCase(tipo)) {
            this.changeEstadoSortBloque(instancia);
        }
    }

    @Transactional
    private void changeEstadoTema(Long instancia) {

        TemaExamenVirtual temaEvaluacionVirtual = temaEvaluacionVirtualDAO.findTemaExamenVirtual(new TemaExamenVirtual(instancia));

        if (temaEvaluacionVirtual.getEstado() == null) {
            temaEvaluacionVirtual.setEstado(EstadoTemaEnum.INA.name());
        }

        boolean activo = EstadoTemaEnum.ACT.name().equalsIgnoreCase(temaEvaluacionVirtual.getEstado());

        if (!activo) {
            TemaExamenVirtual lastActivoTema = temaEvaluacionVirtualDAO.findLastActivo(temaEvaluacionVirtual.getExamenVirtual());
            Integer ordenActual = 1;
            if (lastActivoTema != null) {
                ordenActual = lastActivoTema.getOrden();
                ordenActual++;
            }
            temaEvaluacionVirtual.setOrden(ordenActual);
            temaEvaluacionVirtual.setEstado(EstadoTemaEnum.ACT.name());
            temaEvaluacionVirtualDAO.update(temaEvaluacionVirtual);
            return;
        }

        Integer ordenRemovido = temaEvaluacionVirtual.getOrden();

        TemaExamenVirtual lastInactivoTema = temaEvaluacionVirtualDAO.findLastInactivo(temaEvaluacionVirtual.getExamenVirtual());
        Integer lastOrden = 1000;

        if (lastInactivoTema != null) {
            lastOrden = lastInactivoTema.getOrden();
            lastOrden++;
        }

        temaEvaluacionVirtual.setOrden(lastOrden);
        temaEvaluacionVirtual.setEstado(EstadoTemaEnum.INA.name());
        temaEvaluacionVirtualDAO.update(temaEvaluacionVirtual);

        List<TemaExamenVirtual> temasActivos = temaEvaluacionVirtualDAO.allActivoByEvaluacion(temaEvaluacionVirtual.getExamenVirtual());
        Map<Integer, TemaExamenVirtual> mapTemasActivos = TypesUtil.convertListToMap("orden", temasActivos);

        ordenRemovido++;
        TemaExamenVirtual temaaa = mapTemasActivos.get(ordenRemovido);

        while (temaaa != null) {
            Integer ordenActual = temaaa.getOrden();
            ordenActual--;
            temaaa.setOrden(ordenActual);
            temaEvaluacionVirtualDAO.update(temaaa);
            ordenRemovido++;
            temaaa = mapTemasActivos.get(ordenRemovido);
        }

    }

    @Transactional
    private void changeEstadoSortSubTitulo(Long instancia) {

        SubTituloExamen subTituloEvaluacionVirtual = subTituloEvaluacionVirtualDAO.findSubTituloEvaluacionVirtual(instancia);
        TemaExamenVirtual temaEvaluacionVirtual = subTituloEvaluacionVirtual.getTemaExamen();

        if (subTituloEvaluacionVirtual.getEstado() == null) {
            subTituloEvaluacionVirtual.setEstado(EstadoSubTituloEnum.INA.name());
        }

        boolean activo = EstadoSubTituloEnum.ACT.name().equalsIgnoreCase(subTituloEvaluacionVirtual.getEstado());

        if (!activo) {
            SubTituloExamen lastActivoSubtitulo = subTituloEvaluacionVirtualDAO.findLastActivo(temaEvaluacionVirtual);
            Integer ordenActual = 1;
            if (lastActivoSubtitulo != null) {
                ordenActual = lastActivoSubtitulo.getOrden();
                ordenActual++;
            }
            subTituloEvaluacionVirtual.setOrden(ordenActual);
            subTituloEvaluacionVirtual.setEstado(EstadoSubTituloEnum.ACT.name());
            subTituloEvaluacionVirtualDAO.update(subTituloEvaluacionVirtual);
            return;
        }

        Integer ordenRemovido = subTituloEvaluacionVirtual.getOrden();

        SubTituloExamen lastInactivoSubtitulo = subTituloEvaluacionVirtualDAO.findLastInactivo(temaEvaluacionVirtual);
        Integer lastOrden = 1000;

        if (lastInactivoSubtitulo != null) {
            lastOrden = lastInactivoSubtitulo.getOrden();
            lastOrden++;
        }

        subTituloEvaluacionVirtual.setOrden(lastOrden);
        subTituloEvaluacionVirtual.setEstado(EstadoSubTituloEnum.INA.name());
        subTituloEvaluacionVirtualDAO.update(subTituloEvaluacionVirtual);

        List<SubTituloExamen> subtitulosActivo = subTituloEvaluacionVirtualDAO.allActivoByTema(temaEvaluacionVirtual);
        Map<Integer, SubTituloExamen> mapSubtitulosActivo = TypesUtil.convertListToMap("orden", subtitulosActivo);

        ordenRemovido++;
        SubTituloExamen subtituloo = mapSubtitulosActivo.get(ordenRemovido);

        while (subtituloo != null) {
            Integer ordenActual = subtituloo.getOrden();
            ordenActual--;
            subtituloo.setOrden(ordenActual);
            subTituloEvaluacionVirtualDAO.update(subtituloo);
            ordenRemovido++;
            subtituloo = mapSubtitulosActivo.get(ordenRemovido);
        }

    }

    @Transactional
    private void changeEstadoSortBloque(Long instancia) {

        BloquePreguntas bloqueEvaluacionVirtual = bloqueEvaluacionVirtualDAO.findBloqueEvaluacionVirtual(instancia);

        if (bloqueEvaluacionVirtual.getEstado() == null) {
            bloqueEvaluacionVirtual.setEstado(EstadoBloqueEnum.INA.name());
        }

        boolean activo = EstadoBloqueEnum.ACT.name().equalsIgnoreCase(bloqueEvaluacionVirtual.getEstado());

        if (!activo) {
            bloqueEvaluacionVirtual.setEstado(EstadoBloqueEnum.ACT.name());
            bloqueEvaluacionVirtualDAO.update(bloqueEvaluacionVirtual);
            return;
        }

        bloqueEvaluacionVirtual.setEstado(EstadoBloqueEnum.INA.name());
        bloqueEvaluacionVirtualDAO.update(bloqueEvaluacionVirtual);

    }

    @Override
    public ExamenVirtual findEncuesta(Long idEncuesta) {
        return evaluacionVirtualDAO.find(idEncuesta);
    }

}
