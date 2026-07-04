package song

import org.junit.jupiter.api.DisplayName
import strategy.ConstantStrategy
import kotlin.test.Test
import kotlin.test.assertEquals


@DisplayName("Channel")
class ChannelTest {

    @Test
    @DisplayName("Notes and rests occupy the right regions of the array")
    fun testNoteAndRestLayout(){
        // sample rate of 100, seconds per beat of 1.0 is 100 samples exactly
        // layout should be note(1 beat), rest 1 beat, note 0.5 beat.
        val channel = Channel(ConstantStrategy(1.0), listOf(Measure(listOf(Note(440.0, 1.0), Note(null, 1.0), Note(440.0, 0.5)))))

        val samples = channel.generateSamples(100, 1.0)

        assertEquals(250, samples.size)

        for(i in 0..99){
            assertEquals(1.0, samples[i])
        }

        for(i in 100..199){
            assertEquals(0.0, samples[i])
        }

        for(i in 200..249){
            assertEquals(1.0, samples[i])
        }
    }


    @Test
    @DisplayName("Notes flow across measure boundaries")
    fun testMultipleMeasures(){
        // Two measures of one note each.
        val channel = Channel(
            ConstantStrategy(1.0),
            listOf(
                Measure(listOf(Note(440.0, 1.0))),
                Measure(listOf(Note(440.0, 1.0)))
            )
        )
        val samples = channel.generateSamples(100, 1.0)
        assertEquals(200, samples.size)
        for (i in samples.indices){
            assertEquals(1.0, samples[i])
        }
    }

    @Test
    @DisplayName("All rest channels produce pure silence of correct length")
    fun testAllRests(){
        val channel = Channel(
            ConstantStrategy(1.0),
            listOf(Measure(
                listOf(
                    Note(null, 2.0),
                    Note(null, 1.0)
                )
            )
            )
        )
        val samples = channel.generateSamples(100, 1.0)
        // Make sure there are still 300 samples of 0.0
        assertEquals(300, samples.size)
        for (i in samples.indices){
            assertEquals(0.0, samples[i])
        }
    }


}