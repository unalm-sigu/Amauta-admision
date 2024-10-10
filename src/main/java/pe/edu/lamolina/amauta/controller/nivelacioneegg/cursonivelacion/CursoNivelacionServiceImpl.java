package pe.edu.lamolina.amauta.controller.nivelacioneegg.cursonivelacion;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class CursoNivelacionServiceImpl implements CursoNivelacionService {

    private final CursoDAO cursoDAO;
    private final ModalidadEstudioDAO modalidadEstudioDAO;

    @Override
    public List<Curso> allByDynatable(DynatableFilter filter) {
        return cursoDAO.allByDynatableModalidad(filter, ModalidadEstudioEnum.NIV_ING);
    }

    @Override
    @Transactional
    public void save(Curso curso, DataSessionPivot ds) {
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.NIV_ING);

        Curso cursoNew = new Curso();
        cursoNew.setCodigo(this.getCodigo(curso));
        cursoNew.setNombre(curso.getNombre());
        cursoNew.setModalidadEstudio(modalidadEstudio);
        cursoNew.setUserRegsitro(ds.getUsuario());
        cursoNew.setFechaRegistro(new Date());
        cursoDAO.save(cursoNew);
    }

    private String getCodigo(Curso curso) {
//        DepartamentoAcademico dpto = departamentoAcademicoDAO.find(curso.getDepartamentoAcademico().getId());
//        String curCodFacultad = dpto.getFacultad().getCodigoCurso();
//
//        String codigo = curCodFacultad + curso.getNivel();
//        logger.debug("curCodFacultad curso.getNivel {} {}", curCodFacultad, curso.getNivel());
//        Curso cursoBD = cursoDAO.findLastByCodigoFacultad(codigo.concat("%"));
//
//        if (cursoBD == null) {
//            return this.getCodigoInicio(codigo, 3);// cuando es nuevo 
//        }
//
//        String codCurso = cursoBD.getCodigo();
//        String numero = codCurso.substring(3);
//        Integer correlativo = Integer.valueOf(numero) + 1;
//
//        if (correlativo > 999) {
//            codigo += NumberFormat.codigo(correlativo, 4);
//        } else {
//            codigo += NumberFormat.codigo(correlativo, 3);
//        }
//        return codigo;
        return "";
    }

}
