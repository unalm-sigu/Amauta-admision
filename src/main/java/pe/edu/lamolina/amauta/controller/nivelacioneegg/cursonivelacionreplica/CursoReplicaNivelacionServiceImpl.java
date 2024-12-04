package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacionreplica;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoReplicaNivelacionDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.nivelacioneegg.CursoReplicaNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class CursoReplicaNivelacionServiceImpl implements CursoReplicaNivelacionService {

    public final CursoDAO cursoDAO;
    public final CursoReplicaNivelacionDAO cursoReplicaNivelacionDAO;

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter) {
        List<Curso> cursosNivelacion = cursoDAO.allByDynatableModalidad(filter, ModalidadEstudioEnum.NIV_ING);
        List<CursoReplicaNivelacion> cursosReplicasBD = cursoReplicaNivelacionDAO.allByParents();

        Map<Long, List<CursoReplicaNivelacion>> mapCursoNivelacion = cursosReplicasBD.stream()
                .collect(Collectors.groupingBy(x -> x.getCursoNivelacion().getId()));

        if (cursosNivelacion != null && !cursosNivelacion.isEmpty()) {
            cursosNivelacion.stream().filter(Objects::nonNull).forEach(x -> {
                List<CursoReplicaNivelacion> cursosReplicas = mapCursoNivelacion.get(x.getId());

                if (cursosReplicas != null && !cursosReplicas.isEmpty()) {
                    x.setCursosReplica(cursosReplicas);
                }
            });
        }

        return cursosNivelacion;
    }

    @Override
    public List<Curso> allCursos(String nombre) {
        return cursoDAO.allByModalidadEstudioNombre(ModalidadEstudioEnum.PRE, nombre);
    }

}
