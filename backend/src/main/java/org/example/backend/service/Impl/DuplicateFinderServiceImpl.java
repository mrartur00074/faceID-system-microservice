package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.Applicant;
import org.example.backend.model.BlackList;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.BlackListRepository;
import org.example.backend.service.DuplicateFinderService;
import org.example.backend.util.EmbeddingUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DuplicateFinderServiceImpl implements DuplicateFinderService {
    private final ApplicantRepository applicantRepository;
    private final BlackListRepository blackListRepository;

    /**
     * Сервис для поиска дубликатов абитуриентов на основе векторных представлений (embeddings) лиц.
     * Выполняет поиск похожих записей в базе данных абитуриентов и черном списке.
     *
     * Алгоритм работы:
     *  Итерируется по всем абитуриентам в базе данных
     *  Для каждого абитуриента с ненулевым embedding вычисляет степень сходства
     *       с новым embedding с помощью {@link EmbeddingUtils#isSimilar}
     *  При обнаружении сходства выше порогового значения немедленно выбрасывает
     *       исключение с детальной информацией о найденном дубликате
     *  Повторяет аналогичную проверку для всех записей в черном списке
     *
     * @param newEmbeddingStr строковое представление embedding для проверки
     * @throws RuntimeException если найден похожий абитуриент в основной базе или черном списке
     * @see EmbeddingUtils#isSimilar(String, String) для деталей алгоритма сравнения
     * @see Applicant#getEmbedding() модель хранения embedding абитуриента
     * @see BlackList#getEmbedding() модель хранения embedding в черном списке
     *
     */
    @Override
    public void checkForDuplicates(String newEmbeddingStr) {
        for (Applicant existing : applicantRepository.findAll()) {
            if (existing.getEmbedding() != null && EmbeddingUtils.isSimilar(existing.getEmbedding(), newEmbeddingStr)) {
                throw new RuntimeException("❌ Найден похожий абитуриент в базе: ID = " + existing.getApplicantId() +
                        ", имя: " + existing.getName() + " " + existing.getSurname());
            }
        }

        for (BlackList bl : blackListRepository.findAll()) {
            if (bl.getEmbedding() != null && EmbeddingUtils.isSimilar(bl.getEmbedding(), newEmbeddingStr)) {
                throw new RuntimeException("❌ Найден похожий абитуриент в черном списке: ID = " + bl.getApplicantId() +
                        ", имя: " + bl.getName() + " " + bl.getSurname());
            }
        }
    }
}
