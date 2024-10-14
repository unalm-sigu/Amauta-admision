package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacion;

import java.util.Date;
import java.util.List;
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
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

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

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter) {
        return cursoDAO.allByDynatableModalidad(filter, ModalidadEstudioEnum.NIV_ING);
    }

    @Override
    @Transactional
    public void save(Curso curso, DataSessionPivot ds) {

        Curso cursoNew = new Curso();
        cursoNew.setCodigo(this.getCodigo(curso));
        cursoNew.setNombre(curso.getNombre());
        cursoNew.setModalidadEstudio(new ModalidadEstudio(ID_NIVELACION_INGRESANTES));
        cursoNew.setDepartamentoAcademico(new DepartamentoAcademico(ID_DPTO_ESTUDIOS_GENERALES));
        cursoNew.setUserRegsitro(ds.getUsuario());
        cursoNew.setFechaRegistro(new Date());
        cursoNew.setEstadoEnum(EstadoEnum.ACT);
        cursoDAO.save(cursoNew);
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

}
