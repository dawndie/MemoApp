package memoapp.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorityTest {

    @Test
    void fromValue_WithValidValues_ShouldReturnCorrectPriority() {
        assertEquals(Priority.HIGH, Priority.fromValue("HIGH"));
        assertEquals(Priority.MEDIUM, Priority.fromValue("MEDIUM"));
        assertEquals(Priority.LOW, Priority.fromValue("LOW"));
        assertEquals(Priority.NONE, Priority.fromValue("NONE"));
    }

    @Test
    void fromValue_WithLowerCaseValues_ShouldReturnCorrectPriority() {
        assertEquals(Priority.HIGH, Priority.fromValue("high"));
        assertEquals(Priority.MEDIUM, Priority.fromValue("medium"));
        assertEquals(Priority.LOW, Priority.fromValue("low"));
        assertEquals(Priority.NONE, Priority.fromValue("none"));
    }

    @Test
    void fromValue_WithMixedCaseValues_ShouldReturnCorrectPriority() {
        assertEquals(Priority.HIGH, Priority.fromValue("High"));
        assertEquals(Priority.MEDIUM, Priority.fromValue("Medium"));
        assertEquals(Priority.LOW, Priority.fromValue("Low"));
        assertEquals(Priority.NONE, Priority.fromValue("None"));
    }

    @Test
    void fromValue_WithNullValue_ShouldReturnNone() {
        assertEquals(Priority.NONE, Priority.fromValue(null));
    }

    @Test
    void fromValue_WithInvalidValue_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
                Priority.fromValue("INVALID"));
        
        assertTrue(exception.getMessage().contains("Invalid priority value: INVALID"));
    }

    @Test
    void getValue_ShouldReturnCorrectStringValue() {
        assertEquals("HIGH", Priority.HIGH.getValue());
        assertEquals("MEDIUM", Priority.MEDIUM.getValue());
        assertEquals("LOW", Priority.LOW.getValue());
        assertEquals("NONE", Priority.NONE.getValue());
    }

    @Test
    void getOrder_ShouldReturnCorrectOrderValue() {
        assertEquals(3, Priority.HIGH.getOrder());
        assertEquals(2, Priority.MEDIUM.getOrder());
        assertEquals(1, Priority.LOW.getOrder());
        assertEquals(0, Priority.NONE.getOrder());
    }

    @Test
    void toString_ShouldReturnStringValue() {
        assertEquals("HIGH", Priority.HIGH.toString());
        assertEquals("MEDIUM", Priority.MEDIUM.toString());
        assertEquals("LOW", Priority.LOW.toString());
        assertEquals("NONE", Priority.NONE.toString());
    }

    @Test
    void orderComparison_ShouldBeCorrect() {
        assertTrue(Priority.HIGH.getOrder() > Priority.MEDIUM.getOrder());
        assertTrue(Priority.MEDIUM.getOrder() > Priority.LOW.getOrder());
        assertTrue(Priority.LOW.getOrder() > Priority.NONE.getOrder());
    }
}