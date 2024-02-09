package br.com.raquelleao.certification_nlw.modules.questions.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.raquelleao.certification_nlw.modules.questions.QuestionEntity;

public interface QuestionRepository extends JpaRepository<QuestionEntity, UUID>{
    
    List<QuestionEntity> findByTechnology(String technology);
}
