package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.tema;

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
import pe.edu.lamolina.amauta.dao.encuesta.BloquePreguntasDAO;
import pe.edu.lamolina.amauta.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.amauta.dao.encuesta.SubTituloExamenDAO;
import pe.edu.lamolina.amauta.dao.encuesta.TemaExamenVirtualDAO;

@Service
@Transactional(readOnly = true)
public class TemaEncuestaServiceImp implements TemaEncuestaService {

    @Autowired
    TemaExamenVirtualDAO temaExamenVirtualDAO;

    @Autowired
    SubTituloExamenDAO subTituloExamenDAO;

    @Autowired
    BloquePreguntasDAO bloquePreguntasDAO;

    @Autowired
    ExamenVirtualDAO examenVirtualDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<TemaExamenVirtual> allTema(ExamenVirtual examenVirtual) {

        List<TemaExamenVirtual> temas = temaExamenVirtualDAO.allByEvaluacion(examenVirtual);
        List<SubTituloExamen> subTitulos = subTituloExamenDAO.allByTemas(temas);
        List<BloquePreguntas> bloques = bloquePreguntasDAO.allBysubtitulos(subTitulos);

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
            tema.setSubtitulos(subtituloo);
        });

        return temas;

    }

    @Override
    @Transactional
    public void saveTema(TemaExamenVirtual tema) {

        tema.setEstado(EstadoTemaEnum.ACT.name());
        List<TemaExamenVirtual> temas = temaExamenVirtualDAO.allByEvaluacion(tema.getExamenVirtual());
        Map<Integer, TemaExamenVirtual> mapTemas = TypesUtil.convertListToMap("orden", temas);

        Integer tamanoList = mapTemas.size();
        Integer ordenAsignado = 1;
        tema.setOrden(ordenAsignado);

        if (tamanoList < 1) {
            temaExamenVirtualDAO.save(tema);
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
        temaExamenVirtualDAO.save(tema);

        while (mapTemaUsado != null) {
            ordenAsignado++;
            mapTemaUsado.setOrden(ordenAsignado);
            temaExamenVirtualDAO.update(mapTemaUsado);
            mapTemaUsado = mapTemas.get(ordenAsignado);
        }
    }

    @Override
    @Transactional
    public void updateTema(TemaExamenVirtual tema) {
        TemaExamenVirtual temaEvaluacionVirtual = temaExamenVirtualDAO.find(tema.getId());
        temaEvaluacionVirtual.setNombre(tema.getNombre());
        temaEvaluacionVirtual.setCodigo(tema.getCodigo());
        temaEvaluacionVirtual.setSubtitulosVisibles(tema.getSubtitulosVisibles());
        temaEvaluacionVirtual.setPreguntasVisibles(tema.getPreguntasVisibles());
        temaEvaluacionVirtual.setPesoCategoria(tema.getPesoCategoria());
        temaExamenVirtualDAO.update(temaEvaluacionVirtual);
    }

    @Override
    @Transactional
    public void saveSubTitulo(SubTituloExamen subtitulo) {
        subtitulo.setEstado(EstadoSubTituloEnum.ACT.name());
        subtitulo.setOrden(0);
        subTituloExamenDAO.save(subtitulo);
    }

    @Override
    @Transactional
    public void updateSubTitulo(SubTituloExamen subtitulo) {
        SubTituloExamen subTituloExamen = subTituloExamenDAO.find(subtitulo.getId());
        subTituloExamen.setNombre(subtitulo.getNombre());
        subTituloExamen.setBloquesVisibles(subtitulo.getBloquesVisibles());
        subTituloExamen.setPreguntasVisibles(subtitulo.getPreguntasVisibles());
        subTituloExamenDAO.update(subTituloExamen);
    }

    @Override
    @Transactional
    public void updateBloque(BloquePreguntas bloque) {
        BloquePreguntas bloqueEvaluacionVirtual = bloquePreguntasDAO.find(bloque.getId());
        bloqueEvaluacionVirtual.setContenido(bloque.getContenido());
        bloqueEvaluacionVirtual.setNombre(bloque.getNombre());
        bloqueEvaluacionVirtual.setPreguntasVisibles(bloque.getPreguntasVisibles());
        bloquePreguntasDAO.update(bloqueEvaluacionVirtual);
    }

    @Override
    @Transactional
    public void saveBloque(BloquePreguntas bloque) {
        bloque.setEstado(EstadoBloqueEnum.ACT.name());
        bloquePreguntasDAO.save(bloque);
    }

    @Override
    @Transactional
    public void deleteSubTitulo(SubTituloExamen subtitulo) {
        subTituloExamenDAO.delete(subtitulo);
    }

    @Override
    @Transactional
    public void deleteTema(TemaExamenVirtual tema) {
        temaExamenVirtualDAO.delete(tema);
    }

    @Override
    @Transactional
    public void deleteBloque(BloquePreguntas bloque) {
        bloquePreguntasDAO.delete(bloque);
    }

    @Override
    public TemaExamenVirtual findTema(TemaExamenVirtual tema) {
        return temaExamenVirtualDAO.find(tema.getId());
    }

    @Override
    public BloquePreguntas findBloque(BloquePreguntas bloque) {
        return bloquePreguntasDAO.find(bloque.getId());
    }

    @Override
    public SubTituloExamen findSubTitulo(SubTituloExamen subtitulo) {
        return subTituloExamenDAO.find(subtitulo.getId());
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

        TemaExamenVirtual temaEvaluacionVirtual = temaExamenVirtualDAO.find(instancia);
        if (temaEvaluacionVirtual == null) {
            return;
        }

        Integer oldPlace = temaEvaluacionVirtual.getOrden();
        Integer newPlace = oldPlace + itemSort;

        if (newPlace < 1) {
            return;
        }

        TemaExamenVirtual temaEvaluacionVirtualUsurper = temaExamenVirtualDAO.findByEvaluacionOrden(temaEvaluacionVirtual, newPlace);

        temaEvaluacionVirtual.setOrden(newPlace);
        temaExamenVirtualDAO.update(temaEvaluacionVirtual);

        if (temaEvaluacionVirtualUsurper == null) {
            return;
        }

        temaEvaluacionVirtualUsurper.setOrden(oldPlace);
        temaExamenVirtualDAO.update(temaEvaluacionVirtualUsurper);

    }

    @Transactional
    private void itemSortSubTitulo(Integer itemSort, Long instancia) {

        SubTituloExamen subTituloExamen = subTituloExamenDAO.find(instancia);
        if (subTituloExamen == null) {
            return;
        }

        Integer oldPlace = subTituloExamen.getOrden();
        Integer newPlace = oldPlace + itemSort;

        if (newPlace < 1) {
            return;
        }

        SubTituloExamen subTituloExamenSuper = subTituloExamenDAO.findByTemaOrden(subTituloExamen, newPlace);

        subTituloExamen.setOrden(newPlace);
        subTituloExamenDAO.update(subTituloExamen);

        if (subTituloExamenSuper == null) {
            return;
        }

        subTituloExamenSuper.setOrden(oldPlace);
        subTituloExamenDAO.update(subTituloExamenSuper);

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

        TemaExamenVirtual temaEvaluacionVirtual = temaExamenVirtualDAO.findTemaExamenVirtual(new TemaExamenVirtual(instancia));

        if (temaEvaluacionVirtual.getEstado() == null) {
            temaEvaluacionVirtual.setEstado(EstadoTemaEnum.INA.name());
        }

        boolean activo = EstadoTemaEnum.ACT.name().equalsIgnoreCase(temaEvaluacionVirtual.getEstado());

        if (!activo) {
            TemaExamenVirtual lastActivoTema = temaExamenVirtualDAO.findLastActivo(temaEvaluacionVirtual.getExamenVirtual());
            Integer ordenActual = 1;
            if (lastActivoTema != null) {
                ordenActual = lastActivoTema.getOrden();
                ordenActual++;
            }
            temaEvaluacionVirtual.setOrden(ordenActual);
            temaEvaluacionVirtual.setEstado(EstadoTemaEnum.ACT.name());
            temaExamenVirtualDAO.update(temaEvaluacionVirtual);
            return;
        }

        Integer ordenRemovido = temaEvaluacionVirtual.getOrden();

        TemaExamenVirtual lastInactivoTema = temaExamenVirtualDAO.findLastInactivo(temaEvaluacionVirtual.getExamenVirtual());
        Integer lastOrden = 1000;

        if (lastInactivoTema != null) {
            lastOrden = lastInactivoTema.getOrden();
            lastOrden++;
        }

        temaEvaluacionVirtual.setOrden(lastOrden);
        temaEvaluacionVirtual.setEstado(EstadoTemaEnum.INA.name());
        temaExamenVirtualDAO.update(temaEvaluacionVirtual);

        List<TemaExamenVirtual> temasActivos = temaExamenVirtualDAO.allActivoByEvaluacion(temaEvaluacionVirtual.getExamenVirtual());
        Map<Integer, TemaExamenVirtual> mapTemasActivos = TypesUtil.convertListToMap("orden", temasActivos);

        ordenRemovido++;
        TemaExamenVirtual temaaa = mapTemasActivos.get(ordenRemovido);

        while (temaaa != null) {
            Integer ordenActual = temaaa.getOrden();
            ordenActual--;
            temaaa.setOrden(ordenActual);
            temaExamenVirtualDAO.update(temaaa);
            ordenRemovido++;
            temaaa = mapTemasActivos.get(ordenRemovido);
        }

    }

    @Transactional
    private void changeEstadoSortSubTitulo(Long instancia) {

        SubTituloExamen subTituloExamen = subTituloExamenDAO.findSubTituloExamen(instancia);
        TemaExamenVirtual temaExamenVirtual = subTituloExamen.getTemaExamen();

        if (subTituloExamen.getEstado() == null) {
            subTituloExamen.setEstado(EstadoSubTituloEnum.INA.name());
        }

        boolean activo = EstadoSubTituloEnum.ACT.name().equalsIgnoreCase(subTituloExamen.getEstado());

        if (!activo) {
            SubTituloExamen ultimoSubtitulo = subTituloExamenDAO.findLastActivo(temaExamenVirtual);
            Integer ordenActual = 1;
            if (ultimoSubtitulo != null) {
                ordenActual = ultimoSubtitulo.getOrden();
                ordenActual++;
            }
            subTituloExamen.setOrden(ordenActual);
            subTituloExamen.setEstado(EstadoSubTituloEnum.ACT.name());
            subTituloExamenDAO.update(subTituloExamen);
            return;
        }

        Integer ordenRemovido = subTituloExamen.getOrden();

        SubTituloExamen lastInactivoSubtitulo = subTituloExamenDAO.findLastInactivo(temaExamenVirtual);
        Integer lastOrden = 1000;

        if (lastInactivoSubtitulo != null) {
            lastOrden = lastInactivoSubtitulo.getOrden();
            lastOrden++;
        }

        subTituloExamen.setOrden(lastOrden);
        subTituloExamen.setEstado(EstadoSubTituloEnum.INA.name());
        subTituloExamenDAO.update(subTituloExamen);

        List<SubTituloExamen> subtitulosActivos = subTituloExamenDAO.allActivoByTema(temaExamenVirtual);
        Map<Integer, SubTituloExamen> mapSubtitulosActivo = TypesUtil.convertListToMap("orden", subtitulosActivos);

        ordenRemovido++;
        SubTituloExamen subtituloo = mapSubtitulosActivo.get(ordenRemovido);

        while (subtituloo != null) {
            Integer ordenActual = subtituloo.getOrden();
            ordenActual--;
            subtituloo.setOrden(ordenActual);
            subTituloExamenDAO.update(subtituloo);
            ordenRemovido++;
            subtituloo = mapSubtitulosActivo.get(ordenRemovido);
        }

    }

    @Transactional
    private void changeEstadoSortBloque(Long instancia) {

        BloquePreguntas bloqueEvaluacionVirtual = bloquePreguntasDAO.findBloqueEvaluacionVirtual(instancia);

        if (bloqueEvaluacionVirtual.getEstado() == null) {
            bloqueEvaluacionVirtual.setEstado(EstadoBloqueEnum.INA.name());
        }

        boolean activo = EstadoBloqueEnum.ACT.name().equalsIgnoreCase(bloqueEvaluacionVirtual.getEstado());

        if (!activo) {
            bloqueEvaluacionVirtual.setEstado(EstadoBloqueEnum.ACT.name());
            bloquePreguntasDAO.update(bloqueEvaluacionVirtual);
            return;
        }

        bloqueEvaluacionVirtual.setEstado(EstadoBloqueEnum.INA.name());
        bloquePreguntasDAO.update(bloqueEvaluacionVirtual);

    }

    @Override
    public ExamenVirtual findEncuesta(Long idEncuesta) {
        return examenVirtualDAO.find(idEncuesta);
    }

}
