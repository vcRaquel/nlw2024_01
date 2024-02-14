package br.com.raquelleao.certification_nlw.modules.students.usecases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.raquelleao.certification_nlw.modules.questions.entities.QuestionEntity;
import br.com.raquelleao.certification_nlw.modules.questions.repositories.QuestionRepository;
import br.com.raquelleao.certification_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import br.com.raquelleao.certification_nlw.modules.students.dto.VerifyIfHasCertificationDTO;
import br.com.raquelleao.certification_nlw.modules.students.entities.AnswersCertificationsEntity;
import br.com.raquelleao.certification_nlw.modules.students.entities.CertificationStudentEntity;
import br.com.raquelleao.certification_nlw.modules.students.entities.StudentEntity;
import br.com.raquelleao.certification_nlw.modules.students.repositories.CertificationStudentRepository;
import br.com.raquelleao.certification_nlw.modules.students.repositories.StudentRepository;

@Service
public class StudentCertificationAnswersUseCase {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired CertificationStudentRepository certificationStudentRepository;

    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;
    
    public CertificationStudentEntity execute(StudentCertificationAnswerDTO studentCertificationAnswerDTO) throws Exception{

        var hasCertification = this.verifyIfHasCertificationUseCase.execute(new VerifyIfHasCertificationDTO(studentCertificationAnswerDTO.getEmail(),
            studentCertificationAnswerDTO.getTechnology()));

        if (hasCertification) {
            throw new Exception("Você já tirou sua certificação!");
        }
        // buscar as alternativas das perguntas
            // correct or incorrect
        List<QuestionEntity> questionsEntity = questionRepository.findByTechnology(studentCertificationAnswerDTO.getTechnology());
        List<AnswersCertificationsEntity> answersCertifications = new ArrayList<>();
        
        AtomicInteger correctAnswers = new AtomicInteger(0);

        studentCertificationAnswerDTO.getQuestionAnswers()
            .stream().forEach(questionAnswer ->{
                var question = questionsEntity.stream()
                .filter(questionFilter -> questionFilter.getId().equals(questionAnswer.getQuestionID()))
                .findFirst().get();
            
                var findCorrectAlternative = question.getAlternatives().stream()
                .filter(alternative -> alternative.isCorrect()).findFirst().get();

                if (findCorrectAlternative.getId().equals(questionAnswer.getAlternativeID())) {
                    questionAnswer.setCorrect(true);
                    correctAnswers.incrementAndGet();
                }else{
                    questionAnswer.setCorrect(false);
                }

                var answersCertificationsEntity = AnswersCertificationsEntity.builder()
                    .answerID(questionAnswer.getAlternativeID())
                    .questionID(questionAnswer.getQuestionID())
                    .isCorrect(questionAnswer.isCorrect()).build();
                answersCertifications.add(null);
            });
        
        //verificar se o estudante existe pelo email
        var student = studentRepository.findByEmail(studentCertificationAnswerDTO.getEmail());
        UUID studentID; 
        if(student.isEmpty()){
            var studentCreated = StudentEntity.builder().email(studentCertificationAnswerDTO.getEmail()).build();
            studentCreated = studentRepository.save(studentCreated);
            studentID = studentCreated.getId();
        }else{
            studentID = student.get().getId();
        }

        CertificationStudentEntity certificationStudentEntity = CertificationStudentEntity.builder()
            .technology(studentCertificationAnswerDTO.getTechnology())
            .studentID(studentID)
            .grade(correctAnswers.get())
            .build();

        var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

        answersCertifications.stream().forEach(answersCertification ->{
            answersCertification.setCertificationID(certificationStudentEntity.getId());
            answersCertification.setCertificationStudentEntity(certificationStudentEntity);
        });

        certificationStudentEntity.setAnswersCertificationsEntities(answersCertifications);

        certificationStudentRepository.save(certificationStudentEntity);

        return certificationStudentCreated;
        //salvar as informações da certificação

    }
}
