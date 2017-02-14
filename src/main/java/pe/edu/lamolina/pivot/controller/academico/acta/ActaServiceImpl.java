package pe.edu.lamolina.pivot.controller.academico.acta;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ActaServiceImpl implements ActaService{

}
