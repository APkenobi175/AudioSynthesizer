package song

import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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

    @Test
    @DisplayName("frequencyOrThrow returns the frequency for a real note")
    fun testFrequencyOrThrowOnNote() {
        assertEquals(440.0, Note(440.0, 1.0).frequencyOrThrow(), 1e-9)
    }

    @Test
    @DisplayName("frequencyOrThrow on a rest throws")
    fun testFrequencyOrThrowOnRest() {

        assertFailsWith<IllegalStateException> {
            Note(null, 1.0).frequencyOrThrow()
        }
    }
}