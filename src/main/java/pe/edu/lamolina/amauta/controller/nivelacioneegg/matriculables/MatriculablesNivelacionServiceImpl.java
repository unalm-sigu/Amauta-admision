package pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.matriculables.dto.MatriculablesResumen;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoTemaExamenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.calificacion.TemaExamen;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.CER;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.AlumnoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoTemaExamen;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class MatriculablesNivelacionServiceImpl implements MatriculablesNivelacionService {

    private final AlumnoNivelacionDAO alumnoNivelacionDAO;
    private final CursoCicloAcademicoDAO cursoCicloAcademicoDAO;
    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final CursoTemaExamenDAO cursoTemaExamenDAO;
    private final GrupoHorasNivelacionDAO grupoHorasNivelacionDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;

    private final VerificadorService verificadorService;

    private void verificarPermiso(DataSessionPivot ds) {
        boolean esOperador = verificadorService.esOperadorEEGG(ds);
        Assert.isTrue(esOperador, "No tiene permiso para ejecutar esta operación");
    }

    @Override
    public List<GrupoHorasNivelacion> allGruposHoras() {
        return grupoHorasNivelacionDAO.all();
    }

    @Override
    public List<NotaAlumnoNivelacion> allMatriculablesByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        List<NotaAlumnoNivelacion> matriculables = notaAlumnoNivelacionDAO.allByDynatableCiclo(filter, ciclo);

        return matriculables;
    }

    @Override
    public MatriculablesResumen resumen(CicloAcademico ciclo, DataSessionPivot ds) {
        MatriculablesResumen resumen = notaAlumnoNivelacionDAO.findResumen(ciclo);
        if (resumen.getInscritos() == null) {
            resumen.setInscritos(0L);
        }
        if (resumen.getPendientes() == null) {
            resumen.setPendientes(0L);
        }
        return resumen;
    }

    @Override
    @Transactional
    public int generarMatriculables(CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        List<NotaAlumnoNivelacion> matbles = notaAlumnoNivelacionDAO.allActivosByCiclo(ciclo);
        Map<String, NotaAlumnoNivelacion> mapMatbles = new HashMap();
        for (NotaAlumnoNivelacion matble : matbles) {
            String key = matble.getAlumnoNivelacion().getAlumno().getId() + "-";
            key += matble.getCurso().getId();
            mapMatbles.put(key, matble);
        }

        List<NotaAlumnoNivelacion> nuevosMtbles = notaAlumnoNivelacionDAO.allSinCursoByCiclo(ciclo);
        if (nuevosMtbles.isEmpty()) {
            return nuevosMtbles.size();
        }

        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allActivosByCiclo(ciclo);
        if (cursosNiv.isEmpty()) {
            return cursosNiv.size();
        }

        Map<Long, List<CursoNivelacion>> mapCursoNiv = cursosNiv.stream()
                .collect(Collectors.groupingBy(cn -> cn.getCursoCiclo().getCurso().getId()));

        List<CursoTemaExamen> cursosTemas = cursoTemaExamenDAO.all();
        Map<Long, List<CursoTemaExamen>> mapCursoTema = cursosTemas.stream()
                .collect(Collectors.groupingBy(cte -> cte.getTemaExamen().getId()));

        int nuevos = 0;
        for (NotaAlumnoNivelacion mtble : nuevosMtbles) {
            Alumno alumno = mtble.getAlumnoNivelacion().getAlumno();
            if (mtble.getTemaAprobado()) {
                continue;
            }

            TemaExamen tema = mtble.getTemaExamen();
            List<CursoTemaExamen> cursosTema = mapCursoTema.get(tema.getId());
            if (cursosTema == null) {
                continue;
            }

            for (CursoTemaExamen cursoTema : cursosTema) {
                Curso curso = cursoTema.getCurso();
                String key = alumno.getId() + "-" + curso.getId();
                NotaAlumnoNivelacion existe = mapMatbles.get(key);
                if (existe != null) {
                    mtble.setEsMatriculable(Boolean.FALSE);
                    mtble.setCurso(curso);
                    notaAlumnoNivelacionDAO.update(mtble);
                    continue;
                }

                List<CursoNivelacion> cursosMtbles = mapCursoNiv.get(curso.getId());
                if (cursosMtbles == null) {
                    continue;
                }

                mtble.setEsMatriculable(Boolean.TRUE);
                mtble.setCurso(curso);
                notaAlumnoNivelacionDAO.update(mtble);
                mapMatbles.put(key, mtble);
                nuevos++;
            }
        }

        return nuevos;
    }

    @Override
    @Transactional
    public int matriculaMasivaTipo1(CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        List<NotaAlumnoNivelacion> nuevosMtbles = notaAlumnoNivelacionDAO.allConCursoByCiclo(ciclo);
        if (nuevosMtbles.isEmpty()) {
            return nuevosMtbles.size();
        }

        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allActivosByCiclo(ciclo);

        Map<Long, List<CursoNivelacion>> mapCursoNiv = cursosNiv.stream()
                .collect(Collectors.groupingBy(cn -> cn.getCursoCiclo().getCurso().getId()));

        int nuevos = 0;
        for (NotaAlumnoNivelacion mtble : nuevosMtbles) {
            if (mtble.getTemaAprobado()) {
                continue;
            }

            Curso curso = mtble.getCurso();
            List<CursoNivelacion> cursosMtbles = mapCursoNiv.get(curso.getId());
            if (cursosMtbles == null) {
                continue;
            }

            for (CursoNivelacion cursoNiv : cursosMtbles) {
                if (cursoNiv.getDisponibles() > 0) {
                    cursoNiv.setDisponibles(cursoNiv.getDisponibles() - 1);
                    cursoNiv.setMatriculados(cursoNiv.getMatriculados() + 1);
                    cursoNivelacionDAO.update(cursoNiv);

                    mtble.setCursoNivelacion(cursoNiv);
                    mtble.setEstadoEnum(MAT);
                    mtble.setUserModificacion(ds.getUsuario());
                    mtble.setFechaModificacion(new Date());
                    notaAlumnoNivelacionDAO.update(mtble);
                    nuevos++;

                    AlumnoNivelacion alumnoNiv = mtble.getAlumnoNivelacion();
                    if (alumnoNiv.getEstadoEnum() != MAT) {
                        alumnoNiv.setEstadoEnum(MAT);
                        alumnoNiv.setFechaModificacion(new Date());
                        alumnoNiv.setUserModificacion(ds.getUsuario());
                        alumnoNivelacionDAO.update(alumnoNiv);
                    }
                    break;
                }
            }

        }

        return nuevos;
    }

    @Override
    public NotaAlumnoNivelacion infoAlumno(NotaAlumnoNivelacion form, CicloAcademico ciclo, DataSessionPivot ds) {
        NotaAlumnoNivelacion info = notaAlumnoNivelacionDAO.find(form.getId());
        Assert.isNotNull(info, "No existe el registro que ha seleccionado");
        Assert.isNull(info.getCursoNivelacion(), "Ya se encuetra matriculado");
        Assert.isTrue(info.getEstadoEnum() == NMAT, "No se encuetra habilitado en este registro");

        CicloAcademico cicloBD = info.getAlumnoNivelacion().getCicloAcademico();
        Assert.isTrue(cicloBD.getId().equals(ciclo.getId()), "El registro no corresponde el ciclo actual");

        CursoNivelacion cursoNiv = new CursoNivelacion();

        List<NotaAlumnoNivelacion> notas = notaAlumnoNivelacionDAO.allByAlumnoNivelacion(info.getAlumnoNivelacion());
        Optional<NotaAlumnoNivelacion> notaOpt = notas.stream()
                .filter(nan -> nan.getEstadoEnum() == MAT)
                .filter(nan -> nan.getEsMatriculable())
                .filter(nan -> nan.getCursoNivelacion() != null)
                .findFirst();
        if (notaOpt.isPresent()) {
            GrupoHorasNivelacion grupoHoras = notaOpt.get().getCursoNivelacion().getGrupoHoras();
            cursoNiv.setGrupoHoras(grupoHoras);
        }

        info.setCursoNivelacion(cursoNiv);
        return info;
    }

    @Override
    public List<CursoNivelacion> allSecciones(CursoNivelacion form, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getGrupoHoras(), "No ha indicado el grupo horario");
        Assert.isNotNull(form.getGrupoHoras().getId(), "No ha indicado el grupo horario");

        Assert.isNotNull(form.getCursoCiclo(), "No ha indicado el curso");
        Assert.isNotNull(form.getCursoCiclo().getCurso(), "No ha indicado el curso");
        Assert.isNotNull(form.getCursoCiclo().getCurso().getId(), "No ha indicado el curso");

        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(form.getCursoCiclo().getCurso(), ciclo);
        Assert.isNotNull(cursoCiclo, "Este curso programado no está programado en este ciclo");

        return cursoNivelacionDAO.allByCursoCiclo(cursoCiclo, form.getGrupoHoras());
    }

    @Override
    @Transactional
    public synchronized void matricularCurso(NotaAlumnoNivelacion form, CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        Assert.isNotNull(form.getCursoNivelacion(), "No ha indicado la sección al cual matricularse");
        Assert.isNotNull(form.getCursoNivelacion().getId(), "No ha indicado la sección al cual matricularse");

        NotaAlumnoNivelacion mtble = notaAlumnoNivelacionDAO.find(form.getId());
        Assert.isNotNull(mtble, "No existe el registro que ha seleccionado");
        Assert.isNull(mtble.getCursoNivelacion(), "Ya se encuetra matriculado");
        Assert.isTrue(mtble.getEstadoEnum() == NMAT, "No se encuetra habilitado en este registro");

        CicloAcademico cicloBD = mtble.getAlumnoNivelacion().getCicloAcademico();
        Assert.isTrue(cicloBD.getId().equals(ciclo.getId()), "El registro no corresponde el ciclo actual");
        Assert.isTrue(!mtble.getTemaAprobado(), "Este tema ya aprobó");
        Assert.isTrue(mtble.getEsMatriculable(), "Este registro no es matriculable");
        Assert.isNotNull(mtble.getCurso(), "Este registro debe estar relacionado a algún curso");

        CursoNivelacion cursoNiv = cursoNivelacionDAO.find(form.getCursoNivelacion().getId());
        Assert.isNotNull(cursoNiv, "No existe la sección que ha seleccionado");
        Assert.isTrue(cursoNiv.getEstadoEnum() == SeccionEstadoEnum.ACT, "Esta sección no está habilitada para inscribirse");
        Assert.isTrue(cursoNiv.getDisponibles() > 0, "Ya no existe vacantes disponibles");
        Assert.isFalse(cursoNiv.getEstadoNotasEnum() == CER, "Esta sección ya cerró su acta de notas");

        cursoNiv.setDisponibles(cursoNiv.getDisponibles() - 1);
        cursoNiv.setMatriculados(cursoNiv.getMatriculados() + 1);
        cursoNivelacionDAO.update(cursoNiv);

        mtble.setCursoNivelacion(cursoNiv);
        mtble.setEstadoEnum(MAT);
        mtble.setUserModificacion(ds.getUsuario());
        mtble.setFechaModificacion(new Date());
        notaAlumnoNivelacionDAO.update(mtble);

        AlumnoNivelacion alumnoNiv = mtble.getAlumnoNivelacion();
        if (alumnoNiv.getEstadoEnum() != MAT) {
            alumnoNiv.setEstadoEnum(MAT);
            alumnoNiv.setFechaModificacion(new Date());
            alumnoNiv.setUserModificacion(ds.getUsuario());
            alumnoNivelacionDAO.update(alumnoNiv);
        }
    }

    @Override
    @Transactional
    public synchronized void retirarCurso(NotaAlumnoNivelacion form, CursoNivelacion seccion, CicloAcademico ciclo, DataSessionPivot ds) {
        this.verificarPermiso(ds);

        Assert.isNotNull(form.getCursoNivelacion(), "No ha indicado la sección de la cual retirar");
        Assert.isNotNull(form.getCursoNivelacion().getId(), "No ha indicado la sección de la cual retirar");

        NotaAlumnoNivelacion mtble = notaAlumnoNivelacionDAO.find(form.getId());
        Assert.isNotNull(mtble, "No existe el registro que ha seleccionado");
        Assert.isNotNull(mtble.getCursoNivelacion(), "Ya se encuetra matriculado");
        Assert.isFalse(mtble.getEstadoEnum() == NMAT, "No se encuetra inscrito en este registro");

        CicloAcademico cicloBD = mtble.getAlumnoNivelacion().getCicloAcademico();
        Assert.isTrue(cicloBD.getId().equals(ciclo.getId()), "El registro no corresponde el ciclo actual");

        CursoNivelacion seccionBD = seccion == null ? null : seccion;
        if (seccion == null) {
            seccionBD = cursoNivelacionDAO.find(form.getCursoNivelacion().getId());
        }

        Assert.isNotNull(seccionBD, "No existe la sección que ha seleccionado");
        Assert.isTrue(seccionBD.getId().equals(form.getCursoNivelacion().getId()), "La sección no corresponde al registro seleccionado");
        Assert.isFalse(seccionBD.getEstadoNotasEnum() == CER, "Esta sección ya cerró su acta de notas");

        seccionBD.setDisponibles(seccionBD.getDisponibles() + 1);
        seccionBD.setMatriculados(seccionBD.getMatriculados() - 1);
        cursoNivelacionDAO.update(seccionBD);

        mtble.setCursoNivelacion(null);
        mtble.setEstadoEnum(NMAT);
        mtble.setUserModificacion(ds.getUsuario());
        mtble.setFechaModificacion(new Date());
        notaAlumnoNivelacionDAO.update(mtble);

        AlumnoNivelacion alumnoNiv = mtble.getAlumnoNivelacion();
        List<NotaAlumnoNivelacion> notasAll = notaAlumnoNivelacionDAO.allByAlumnoNivelacion(alumnoNiv);
        List<NotaAlumnoNivelacion> matriculados = notasAll.stream()
                .filter(nan -> nan.getEstadoEnum() == MAT)
                .collect(Collectors.toList());

        if (matriculados.isEmpty() && alumnoNiv.getEstadoEnum() != NMAT) {
            alumnoNiv.setEstadoEnum(NMAT);
            alumnoNiv.setFechaModificacion(new Date());
            alumnoNiv.setUserModificacion(ds.getUsuario());
            alumnoNivelacionDAO.update(alumnoNiv);
        }
    }

}
