package br.com.raquelleao.certification_nlw.modules.students.usecases;

import org.springframework.stereotype.Service;

import br.com.raquelleao.certification_nlw.modules.students.dto.VerifyIfHasCertificationDTO;

@Service
public class VerifyIfHasCertificationUseCase {
    
    public boolean execute(VerifyIfHasCertificationDTO dto) {
        if (dto.getEmail().equals("danieleleaoe@gmail.com") && dto.getTechnology().equals("JAVA")) {
            return true;
        }
        return false;
    }
}
