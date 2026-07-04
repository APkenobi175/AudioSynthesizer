package song

import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("Note")
class NoteTest {
    @Test
    @DisplayName("a note with a null frequency is a rest")
    fun testNullFrequency() {
        val rest = Note(null, 2.0)
        assertTrue(rest.isRest())
    }

    @Test
    @DisplayName("A note without a null frequency is not a rest")
    fun testFrequencyIsNotRest(){
        val rest = Note(392.0, 2.0)
        assertFalse(rest.isRest())
    }

    @Test
    @DisplayName("A frequency of 0.0 is NOT a rest")
    fun testZeroFrequency(){
        val rest = Note(0.0, 1.0)
        assertFalse(rest.isRest())
    }
}