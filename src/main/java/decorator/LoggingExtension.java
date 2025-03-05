package decorator;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LoggingExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        System.out.println("Запустился тест: " + context.getDisplayName());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        System.out.println("Завершился тест: " + context.getDisplayName());
    }

}
