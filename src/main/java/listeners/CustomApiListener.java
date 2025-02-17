package listeners;

import io.qameta.allure.restassured.AllureRestAssured;

// Кастомный класс для преобразования аллюр отчета в более читабельный вид
public class CustomApiListener {
    private static final AllureRestAssured FILTER = new AllureRestAssured();
    public static AllureRestAssured withCustomTemplates() {
        FILTER.setRequestTemplate("request.ftl");
        FILTER.setResponseTemplate("response.ftl");
        return FILTER;
    }
}
