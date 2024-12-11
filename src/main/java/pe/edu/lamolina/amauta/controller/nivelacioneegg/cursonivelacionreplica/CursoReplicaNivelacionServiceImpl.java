package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacionreplica;

import java.util.ArrayList;
import java.util.Date;
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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacionreplica.dto.CursoReplicaDTO;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoReplicaNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
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

    private final VerificadorService verificadorService;

    private void verificarPermiso(DataSessionPivot ds) {
        boolean esOperador = verificadorService.esOperadorEEGG(ds);
        Assert.isTrue(esOperador, "No tiene permiso para ejecutar esta operación");
    }

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

    @Override
    @Transactional
    public int saveRelacionRegular(CursoReplicaDTO cursoReplicaDTO, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        List<CursoReplicaNivelacion> relacionadosBD = cursoReplicaNivelacionDAO.allByCursoNivelacion(cursoReplicaDTO.getCurso());
        List<CursoReplicaNivelacion> relacionadosFinal = new ArrayList<>();

        Map<String, CursoReplicaNivelacion> mapRelacionados = relacionadosBD.stream()
                .collect(Collectors.toMap(x -> x.getCursoNivelacion().getId() + "-" + x.getCursoRegular().getId(), x -> x));

        cursoReplicaDTO.getCursosRegulares().stream().forEach(x -> {

            CursoReplicaNivelacion cursoReplicaNivelacion = mapRelacionados.get(cursoReplicaDTO.getCurso().getId() + "-" + x.getId());
            if (cursoReplicaNivelacion == null) {
                CursoReplicaNivelacion curso = new CursoReplicaNivelacion();
                curso.setCursoNivelacion(cursoReplicaDTO.getCurso());
                curso.setCursoRegular(x);
                curso.setUserRegistro(ds.getUsuario());
                curso.setFechaRegistro(new Date());
                cursoReplicaNivelacionDAO.save(curso);
            }
            relacionadosFinal.add(cursoReplicaNivelacion);

        });

        Map<String, CursoReplicaNivelacion> mapRelacionadosFinal = relacionadosFinal.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(x -> x.getCursoNivelacion().getId() + "-" + x.getCursoRegular().getId(), x -> x));

        relacionadosBD.stream().forEach(x -> {
            CursoReplicaNivelacion cursoReplicaNivelacion = mapRelacionadosFinal.get(x.getCursoNivelacion().getId() + "-" + x.getCursoRegular().getId());
            if (cursoReplicaNivelacion == null) {
                cursoReplicaNivelacionDAO.delete(x);
            }
        });

        return relacionadosFinal.size();

    }

}
