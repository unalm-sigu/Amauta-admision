package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.admision.TemaExamenDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoTemaExamenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.calificacion.TemaExamen;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.nivelacioneegg.CursoTemaExamen;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class CursoNivelacionServiceImpl implements CursoNivelacionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Long ID_DPTO_ESTUDIOS_GENERALES = 45L;
    private final Long ID_NIVELACION_INGRESANTES = 11L;
    private final String CODIGO_DPTO_ESTUDIOS_GENERALES = "EG0";
    private final CursoDAO cursoDAO;
    private final ModalidadEstudioDAO modalidadEstudioDAO;
    private final DepartamentoAcademicoDAO departamentoAcademicoDAO;
    private final TemaExamenDAO temaExamenDAO;
    private final CursoTemaExamenDAO cursoTemaExamenDAO;

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter) {
        List<Curso> cursosNivelacion = cursoDAO.allByDynatableModalidad(filter, ModalidadEstudioEnum.NIV_ING);
        List<CursoTemaExamen> cursosTemas = cursoTemaExamenDAO.allByCursos(cursosNivelacion);
        Map<Long, List<CursoTemaExamen>> mapCursoTemas = cursosTemas.stream()
                .filter(x -> x.getCurso() != null)
                .collect(Collectors.groupingBy(x -> x.getCurso().getId()));

        if (cursosNivelacion != null && !cursosNivelacion.isEmpty()) {
            cursosNivelacion.stream().forEach(cursoTemasBD -> {
                List<CursoTemaExamen> cursoTemas = mapCursoTemas.get(cursoTemasBD.getId());
                if (cursoTemas != null && !cursoTemas.isEmpty()) {
                    cursoTemasBD.setCursoTemasExamen(cursoTemas);
                } else {
                    cursoTemasBD.setCursoTemasExamen(new ArrayList<>());
                }
            });

        }

        return cursosNivelacion;
    }

    @Override
    @Transactional
    public void save(Curso curso, DataSessionPivot ds) {

        if (curso.getId() == null) {
            Curso cursoNew = new Curso();
            cursoNew.setCodigo(this.getCodigo(curso));
            cursoNew.setNombre(curso.getNombre());
            cursoNew.setModalidadEstudio(new ModalidadEstudio(ID_NIVELACION_INGRESANTES));
            cursoNew.setDepartamentoAcademico(new DepartamentoAcademico(ID_DPTO_ESTUDIOS_GENERALES));
            cursoNew.setUserRegsitro(ds.getUsuario());
            cursoNew.setFechaRegistro(new Date());
            cursoNew.setEstadoEnum(EstadoEnum.PEN);
            cursoDAO.save(cursoNew);
        } else {
            Curso cursoBD = cursoDAO.find(curso.getId());
            cursoBD.setNombre(curso.getNombre());
            cursoDAO.update(cursoBD);
        }
    }

    private String getCodigo(Curso curso) {
        String codDptoCursoNivelacion = CODIGO_DPTO_ESTUDIOS_GENERALES;
        Curso cursoBD = cursoDAO.findLastByCodigoFacultad(codDptoCursoNivelacion.concat("%"));

        String codCursoInicio = "";
        if (cursoBD == null) {
            codCursoInicio = this.getCodigoInicio(codDptoCursoNivelacion, 3);// cuando es nuevo 
        } else {
            codCursoInicio = cursoBD.getCodigo();
        }

        String codCurso = codCursoInicio;
        String numero = codCurso.substring(3);
        Integer correlativo = Integer.valueOf(numero) + 1;

        if (correlativo > 9) {
            codDptoCursoNivelacion += NumberFormat.codigo(correlativo, 3);
        } else {
            codDptoCursoNivelacion += NumberFormat.codigo(correlativo, 3);
        }
        return codDptoCursoNivelacion;
    }

    public String getCodigoInicio(String value, int ancho) {
        StringBuilder cod = new StringBuilder();
        for (int i = 0; i < ancho; i++) {
            cod.append("0");
        }
        return value.concat(cod.toString());
    }

    @Override
    @Transactional
    public void changeEstado(Curso curso, DataSessionPivot ds) {
        Curso cursoBD = cursoDAO.find(curso.getId());
        if (curso.getEstadoEnum() == EstadoEnum.PEN) {
            cursoBD.setEstadoEnum(EstadoEnum.ACT);
        } else {
            cursoBD.setEstadoEnum(EstadoEnum.ANU);
        }
        cursoDAO.update(cursoBD);
    }

    @Override
    @Transactional
    public void eliminar(Curso curso, DataSessionPivot ds) {
        cursoDAO.delete(curso);
    }

    @Override
    public List<TemaExamen> allTemas(DataSessionPivot ds) {
        return temaExamenDAO.all().stream().filter(x -> x.getCicloFin() == null).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int saveRelacion(CursoListTemas cursoListTemas, DataSessionPivot ds) {
        List<CursoTemaExamen> temasCursoBD = cursoTemaExamenDAO.allByCurso(cursoListTemas.getCurso());

        Map<String, CursoTemaExamen> mapCursoTemaBD = temasCursoBD.stream()
                .collect(Collectors.toMap(x -> x.getCurso().getId() + "-" + x.getTemaExamen().getId(), cursoTemaExamen -> cursoTemaExamen));

        List<CursoTemaExamen> cursosFinal = new ArrayList<>();

        cursoListTemas.getIds().stream().forEach(x -> {
            CursoTemaExamen cursoTemaExamenFinal = mapCursoTemaBD.get(cursoListTemas.getCurso().getId() + "-" + x);

            if (cursoTemaExamenFinal == null) {
                CursoTemaExamen cte = new CursoTemaExamen();
                cte.setCurso(cursoListTemas.getCurso());
                cte.setTemaExamen(new TemaExamen(x));
                cte.setUserRegistro(ds.getUsuario());
                cte.setFechaRegistro(new Date());
                cursoTemaExamenDAO.save(cte);
            }
            cursosFinal.add(cursoTemaExamenFinal);

        });

        Map<String, CursoTemaExamen> mapCursoTemaFinal = cursosFinal.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(x -> x.getCurso().getId() + "-" + x.getTemaExamen().getId(), cursoTemaExamen -> cursoTemaExamen));

        temasCursoBD.stream().forEach(x -> {

            CursoTemaExamen cursoTemaExamen = mapCursoTemaFinal.get(x.getCurso().getId() + "-" + x.getTemaExamen().getId());
            if (cursoTemaExamen == null) {
                cursoTemaExamenDAO.delete(x);
            }

        });

        return cursosFinal.size();
    }

    @Override
    public List<CursoTemaExamen> allByCurso(Curso curso) {
        return cursoTemaExamenDAO.allByCurso(curso);
    }

}
