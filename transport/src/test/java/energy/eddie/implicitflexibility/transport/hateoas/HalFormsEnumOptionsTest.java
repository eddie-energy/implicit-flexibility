package energy.eddie.implicitflexibility.transport.hateoas;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HalFormsEnumOptionsTest {

    @Mock
    private HalFormsConfiguration configuration;

    @BeforeEach
    void setUp() {
        doReturn(configuration)
                .when(configuration)
                .withOptions(any(), anyString(), any());
    }

    @Test
    void testRegisterRecord() {
        // Given / When
        HalFormsConfiguration result = HalFormsEnumOptions.register(configuration, TestRecord.class);

        // Then
        assertSame(configuration, result);
        verify(configuration).withOptions(
                eq(TestRecord.class),
                eq("test_enum"),
                any()
        );
    }

    @Test
    void testRegisterBean() {
        // Given / When
        HalFormsConfiguration result = HalFormsEnumOptions.register(configuration, TestBean.class);

        // Then
        assertSame(configuration, result);
        verify(configuration).withOptions(eq(TestBean.class), eq("testEnum"), any());
    }

    private record TestRecord(@JsonProperty("test_enum") TestEnum testEnum) {}

    private enum TestEnum {
        TEST_VAL1,
        TEST_VAL2
    }

    private static class TestBean {

        @Nullable
        private TestEnum testEnum;

        @SuppressWarnings({"EffectivelyPrivate", "UnusedMethod"})
        @Nullable
        public TestEnum getTestEnum() {
            return testEnum;
        }
    }
}
