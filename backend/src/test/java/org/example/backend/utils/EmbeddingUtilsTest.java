package org.example.backend.utils;

import org.example.backend.util.EmbeddingUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class EmbeddingUtilsTest {

    private final String embStr1;
    private final String embStr2;
    private final boolean expected;

    public EmbeddingUtilsTest(String embStr1, String embStr2, boolean expected) {
        this.embStr1 = embStr1;
        this.embStr2 = embStr2;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"1.0,0.0,0.0", "1.0,0.0,0.0", true}, // Идентичные векторы
                {"1.0,0.0,0.0", "0.0,1.0,0.0", false}, // Ортогональные векторы
                {"1.0,0.0", "0.6,0.8", true}, // Ровно на пороге
                {"1.0,0.0", "0.7,0.714", true} // Чуть выше порога (~0.7)
        });
    }

    @Test
    public void testParseEmbeddingString_ValidInput() {
        String embStr = "1.0,2.5,3.7,0.1";

        float[] result = EmbeddingUtils.parseEmbeddingString(embStr);

        assertArrayEquals("Массивы должны совпадать", new float[]{1.0f, 2.5f, 3.7f, 0.1f}, result, 0.001f);
    }

    @Test
    public void testParseEmbeddingString_WithSpaces() {
        String embStr = " 1.0 , 2.5 , 3.7 , 0.1 ";

        float[] result = EmbeddingUtils.parseEmbeddingString(embStr);

        assertArrayEquals("Массивы должны совпадать после trim", new float[]{1.0f, 2.5f, 3.7f, 0.1f}, result, 0.001f);
    }

    @Test(expected = NumberFormatException.class)
    public void testParseEmbeddingString_EmptyString() {
        String embStr = "";

        EmbeddingUtils.parseEmbeddingString(embStr);
    }

    @Test(expected = NumberFormatException.class)
    public void testParseEmbeddingString_InvalidNumber() {
        String embStr = "1.0,invalid,3.7";

        EmbeddingUtils.parseEmbeddingString(embStr);
    }

    @Test
    public void testCosineSimilarity_IdenticalVectors() {
        float[] v1 = {1.0f, 2.0f, 3.0f};
        float[] v2 = {1.0f, 2.0f, 3.0f};

        double similarity = EmbeddingUtils.cosineSimilarity(v1, v2);

        assertEquals("Идентичные векторы должны иметь схожесть 1.0", 1.0, similarity, 0.001);
    }

    @Test
    public void testCosineSimilarity_OrthogonalVectors() {
        float[] v1 = {1.0f, 0.0f};
        float[] v2 = {0.0f, 1.0f};

        double similarity = EmbeddingUtils.cosineSimilarity(v1, v2);

        assertEquals("Ортогональные векторы должны иметь схожесть 0.0", 0.0, similarity, 0.001);
    }

    @Test
    public void testCosineSimilarity_OppositeVectors() {
        float[] v1 = {1.0f, 2.0f, 3.0f};
        float[] v2 = {-1.0f, -2.0f, -3.0f};

        double similarity = EmbeddingUtils.cosineSimilarity(v1, v2);

        assertEquals("Противоположные векторы должны иметь схожесть -1.0", -1.0, similarity, 0.001);
    }

    @Test
    public void testIsSimilar_AboveThreshold() {
        String embStr1 = "1.0,0.0,0.0";
        String embStr2 = "0.9,0.1,0.0"; // Должны быть похожи

        boolean result = EmbeddingUtils.isSimilar(embStr1, embStr2);

        assertTrue("Векторы выше порога должны быть похожи", result);
    }

    @Test
    public void testIsSimilar_BelowThreshold() {
        String embStr1 = "1.0,0.0,0.0";
        String embStr2 = "0.0,1.0,0.0"; // Ортогональные - не похожи

        boolean result = EmbeddingUtils.isSimilar(embStr1, embStr2);

        assertFalse("Векторы ниже порога не должны быть похожи", result);
    }

    @Test
    public void testIsSimilar_ExactlyAtThreshold() {
        String embStr1 = "1.0,0.0,0.0";
        // Создаем вектор с косинусной схожестью чуть меньше 0.6
        // Для вектора [1,0,0] и [a,b,c]: cos(θ) = a/√(a²+b²+c²)
        // Нужно a/√(a²+b²+c²) = 0.599999 (чуть меньше 0.6)
        String embStr2 = "0.6,0.8,0.0"; // cos(θ) = 0.6/√(0.6²+0.8²) = 0.6/1.0 = 0.6

        boolean result = EmbeddingUtils.isSimilar(embStr1, embStr2);

        assertTrue("Векторы с схожестью 0.6 должны давать true", result);
    }

    @Test
    public void testIsSimilar_JustBelowThreshold() {
        String embStr1 = "1.0,0.0,0.0";
        // Создаем вектор с косинусной схожестью чуть меньше 0.6
        // Для вектора [1,0,0] и [a,b,c]: cos(θ) = a/√(a²+b²+c²)
        // Нужно a/√(a²+b²+c²) ≈ 0.599999
        String embStr2 = "0.599,0.8,0.0"; // cos(θ) ≈ 0.599/√(0.599²+0.8²) ≈ 0.599/1.0 ≈ 0.599

        boolean result = EmbeddingUtils.isSimilar(embStr1, embStr2);

        assertFalse("Векторы с схожестью чуть ниже 0.6 должны давать false", result);
    }

    @Test
    public void testConvertEmbeddingToString() {
        float[] embedding = {1.0f, 2.5f, 3.7f, 0.1f};

        String result = EmbeddingUtils.convertEmbeddingToString(embedding);

        assertEquals("Строковое представление должно совпадать", "1.0,2.5,3.7,0.1", result);
    }

    @Test
    public void testConvertEmbeddingToString_EmptyArray() {
        float[] embedding = {};

        String result = EmbeddingUtils.convertEmbeddingToString(embedding);

        assertEquals("Пустой массив должен давать пустую строку", "", result);
    }

    @Test
    public void testConvertEmbeddingToString_SingleElement() {
        float[] embedding = {1.5f};

        String result = EmbeddingUtils.convertEmbeddingToString(embedding);

        assertEquals("Массив из одного элемента", "1.5", result);
    }

    @Test
    public void testIsSimilar_Parameterized() {
        boolean result = EmbeddingUtils.isSimilar(embStr1, embStr2);

        assertEquals("Параметризованный тест для isSimilar", expected, result);
    }

    @Test
    public void testIntegration_ParseConvertRoundTrip() {
        float[] original = {1.0f, 2.5f, 3.7f, 0.1f};

        String stringRepresentation = EmbeddingUtils.convertEmbeddingToString(original);
        float[] parsed = EmbeddingUtils.parseEmbeddingString(stringRepresentation);

        assertArrayEquals("Round-trip преобразование должно сохранять данные", original, parsed, 0.001f);
    }
}
