package pe.edu.lamolina.pivot.controller.oficinas.matricula.restriccionmatricula;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.DeudaAlumno;
import pe.edu.lamolina.model.academico.TipoDeudaAlumno;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface RestriccionMatriculaService {

    List<DeudaAlumno> allDeudaAlumno(DynatableFilter filter);

    void anularDeuda(DeudaAlumno deuda, DataSessionPivot ds);

    void levantarDeuda(DeudaAlumno deuda, DataSessionPivot ds);

    void guardarDeuda(DeudaAlumno deudaForm);

    List<TipoDeudaAlumno> allTipoDeudaAlumno();

    List<String> cargarDeudas(MultipartFile file, TipoDeudaAlumno tipo, DataSessionPivot ds);

}
