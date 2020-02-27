package pe.edu.lamolina.pivot.controller.migraciones.histomigra;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.croacia.HistoGradMy;
import pe.edu.lamolina.model.croacia.HistoMy;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RET;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoMigracionEnum;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.migraciones.HistoGradMyDAO;
import pe.edu.lamolina.pivot.dao.migraciones.HistoMyDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class HistoMigraServiceImp implements HistoMigraService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    CursoDAO cursoDAO;
    @Autowired
    HistoMyDAO histoMyDAO;
    @Autowired
    HistoGradMyDAO histoGradMyDAO;

    @Autowired
    PromedioService promedioService;

    @Override
    public Alumno findAlumno(Alumno alumno) {
        return alumnoDAO.find(alumno);
    }

    @Override
    public List<HistoMy> allHistoByAlumno(Alumno alumno) {
        Alumno alumnoBD = alumnoDAO.find(alumno);
        return histoMyDAO.allByMatricula(alumnoBD.getCodigo());
    }

    @Override
    public List<HistoGradMy> allHistoGradByAlumno(Alumno alumno) {
        Alumno alumnoBD = alumnoDAO.find(alumno);
        return histoGradMyDAO.allByMatricula(alumnoBD.getCodigo());
    }

    @Override
    public List<Curso> allCursosByHisto(List<HistoMy> historias) {
        List<String> codigosAntiguos = historias.stream().map(x -> x.getHistoPK().getCurCodigo()).collect(Collectors.toList());
        return cursoDAO.allByCodigosAntiguos(codigosAntiguos);
    }

    @Override
    public List<Curso> allCursosByHistoGrad(List<HistoGradMy> historiasGrad) {
        List<String> codigosAntiguos = historiasGrad.stream().map(x -> x.getCurCodigo()).collect(Collectors.toList());
        return cursoDAO.allByCodigosAntiguos(codigosAntiguos);
    }

    @Override
    public List<AlumnoCicloCurso> allAlumnoCursoByAlumno(Alumno alumno) {
        return alumnoCicloCursoDAO.allByAlumno(alumno);
    }

    @Override
    @Transactional
    public void migrarCurso(HistoGradMy histo, DataSessionPivot ds) {
        HistoMy histoPregrado = null;
        HistoGradMy histoPosgrado = null;
        if (histo.getTipoRegistroOracle().equals("histo")) {
            histoPregrado = histoMyDAO.findByHisto(histo);
        } else if (histo.getTipoRegistroOracle().equals("histo_grad")) {
            histoPosgrado = histoGradMyDAO.findByHisto(histo);
        }

        Alumno alumno = alumnoDAO.findByCodigo(histo.getMatricula());
        ModalidadEstudioEnum modalidaEnum = alumno.getModalidadEstudio().getOperativeModalidadEnum();
        List<AlumnoCicloCurso> alumnoCursos = alumnoCicloCursoDAO.allByAlumno(alumno);
        Curso curso = cursoDAO.findByCodigoAntiguo(histo.getCurCodigo());
        CicloAcademico ciclo = cicloAcademicoDAO.findByCodigoAnteriorModalidadEnum(histo.getCiclo(), modalidaEnum);
        AlumnoCicloCurso acc = getAlumnoCursoByHisto(histo, alumnoCursos, curso);

        AlumnoCiclo aluCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, ciclo);
        if (aluCiclo == null) {
            aluCiclo = new AlumnoCiclo();
            aluCiclo.defaultValuesToCreate(alumno, ciclo, ds.getUsuario());
            aluCiclo.setSituacionInicio(new SituacionAcademica(SituacionAcademicaEnum.S_N));
            aluCiclo.setSituacionFinal(new SituacionAcademica(SituacionAcademicaEnum.S_N));
            aluCiclo.setEstadoEnum(MAT);
            aluCiclo.setTipoMigracion(TipoMigracionEnum.NREG);
            alumnoCicloDAO.save(aluCiclo);
        }

        if (acc == null) {
            acc = new AlumnoCicloCurso();
            acc.setAlumnoCiclo(aluCiclo);
            acc.setCurso(curso);
            acc.setCreditos(modalidaEnum == EPG ? histoPosgrado.getCurCredit() : histoPregrado.getCurCredit());
            acc.setNota(modalidaEnum == EPG ? histoPosgrado.getNota() : histoPregrado.getNota());
            acc.setEstaAprobado(promedioService.evaluateEstaAprobado(acc, alumno));
            acc.setRegistroActivo(1);

            if (Arrays.asList("1", "3").contains(histo.getMov())) {
                acc.setEstado(MAT);
            } else {
                acc.setEstado(RET);
            }

            acc.setFechaMigracion(new Date());
            acc.setUserMigracion(modalidaEnum == EPG ? histoPosgrado.getUsuario() : histoPregrado.getUsuario());
            acc.setFechaRegistro(modalidaEnum == EPG ? histoPosgrado.getFechaMov() : histoPregrado.getFechaMov());
            acc.setOrigenData(OrigenDataSituacionAcademicaEnum.MIGRA);
            acc.setUsuarioRegistro(ds.getUsuario());
            acc.setVecesCursado(1);
            alumnoCicloCursoDAO.save(acc);

        } else {
            acc.setCurso(curso);
            acc.setCreditos(modalidaEnum == EPG ? histoPosgrado.getCurCredit() : histoPregrado.getCurCredit());
            acc.setNota(modalidaEnum == EPG ? histoPosgrado.getNota() : histoPregrado.getNota());
            if (Arrays.asList("1", "3").contains(histo.getMov())) {
                acc.setEstado(MAT);
            } else {
                acc.setEstado(RET);
            }
            acc.setEstaAprobado(promedioService.evaluateEstaAprobado(acc, alumno));

            alumnoCicloCursoDAO.update(acc);
        }

    }

    private AlumnoCicloCurso getAlumnoCursoByHisto(HistoGradMy histo, List<AlumnoCicloCurso> alumnoCursosTodos, Curso curso) {
        if (curso == null) {
            return null;
        }

        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCurso = TypesUtil.convertListToMapList("curso.id", alumnoCursosTodos);
        List<AlumnoCicloCurso> aluCicloCursoByCurso = TypesUtil.getListNotNull(mapAlumnoCurso.get(curso.getId()));
        if (aluCicloCursoByCurso.isEmpty()) {
            return null;
        }

        String ciclo = histo.getCiclo();
        String cicloMy = ciclo.substring(0, 4) + (ciclo.endsWith("N") ? "15" : (ciclo.substring(4, 5) + "0"));

        Map<String, List<AlumnoCicloCurso>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumnoCiclo.cicloAcademico.codigo", aluCicloCursoByCurso);
        List<AlumnoCicloCurso> aluCicloCursoByCiclo = TypesUtil.getListNotNull(mapAlumnoCiclo.get(cicloMy));
        if (aluCicloCursoByCiclo.isEmpty()) {
            return null;
        }

        String mov = histo.getMov();
        Boolean registroOk = Arrays.asList("1", "3").contains(mov);
        for (AlumnoCicloCurso acc : aluCicloCursoByCiclo) {
            if (acc.getEstadoEnum() == MAT && registroOk) {
                return acc;
            }
            if (acc.getEstadoEnum() != MAT && !registroOk) {
                return acc;
            }
        }
        return aluCicloCursoByCiclo.get(0);

    }

    @Override
    public Alumno findAlumnoByCodigo(String matricula) {
        return alumnoDAO.findByCodigo(matricula);
    }

}
