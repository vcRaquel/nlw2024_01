package br.com.raquelleao.certification_nlw.modules.questions.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.raquelleao.certification_nlw.modules.questions.QuestionEntity;
import br.com.raquelleao.certification_nlw.modules.questions.dto.AlternativesResultDTO;
import br.com.raquelleao.certification_nlw.modules.questions.dto.QuestionResultDTO;
import br.com.raquelleao.certification_nlw.modules.questions.entities.AlternativesEntity;
import br.com.raquelleao.certification_nlw.modules.questions.repositories.QuestionRepository;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/tecnology/{tecnology}")
    public List<QuestionResultDTO> findByTechnology(@PathVariable String tecnology){
        var result = this.questionRepository.findByTechnology(tecnology);

        var toMap = result.stream().map(question -> mapQuestionToDTO(question))
        .collect(Collectors.toList());
        return toMap;
    }

    static QuestionResultDTO mapQuestionToDTO(QuestionEntity question){
        var questionResultDTO = QuestionResultDTO.builder()
            .id(question.getId())
            .description(question.getDescription()).build();

        List<AlternativesResultDTO> alternativesResultDTOs = question.getAlternatives()
            .stream().map(alternative -> mapAlternativeDTO(alternative))
            .collect(Collectors.toList());

        questionResultDTO.setAlternatives(alternativesResultDTOs);
        return questionResultDTO;
    }
    
    static AlternativesResultDTO mapAlternativeDTO(AlternativesEntity alternativesEntity){
        return AlternativesResultDTO.builder()
        .id(alternativesEntity.getId())
        .description(alternativesEntity.getDescription()).build();
    }
}