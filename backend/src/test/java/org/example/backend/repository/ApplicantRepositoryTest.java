package org.example.backend.repository;

import org.example.backend.model.Applicant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ApplicantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Test
    public void findByApplicantId_ShouldReturnApplicant() {
        Applicant applicant = Applicant.builder()
                .applicantId(11167)
                .name("Артур")
                .surname("Давлетшин")
                .phoneNum("+79999999999")
                .school("Школа №1")
                .attempt(0)
                .status("NEW")
                .base64("base64string")
                .embedding("embedding_vector")
                .build();

        entityManager.persist(applicant);
        entityManager.flush();

        Applicant found = applicantRepository.findByApplicantId(11167).orElse(null);

        assertNotNull("Абитуриент должен быть найден", found);
        assertEquals("Имя должно совпадать", "Артур", found.getName());
        assertEquals("Фамилия должна совпадать", "Давлетшин", found.getSurname());
    }

    @Test
    public void existsByApplicantId_ShouldReturnTrueForExisting() {
        Applicant applicant = Applicant.builder()
                .applicantId(11168)
                .name("НеАртур")
                .surname("Давлетшин")
                .phoneNum("+7999999998")
                .school("Гимназия №2")
                .attempt(0)
                .status("NEW")
                .base64("base64string")
                .embedding("embedding_vector")
                .build();

        entityManager.persist(applicant);
        entityManager.flush();

        assertTrue("Абитуриент с applicantId=11168 должен существовать",
                applicantRepository.existsByApplicantId(11168));
    }

    @Test
    public void existsByApplicantId_ShouldReturnFalseForNonExisting() {
        assertFalse("Абитуриент с несуществующим ID не должен находиться",
                applicantRepository.existsByApplicantId(99999));
    }
}