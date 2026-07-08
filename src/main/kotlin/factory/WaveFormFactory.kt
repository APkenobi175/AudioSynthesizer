package factory

import strategy.SawtoothWaveStrategy
import strategy.SineWaveStrategy
import strategy.SquareWaveStrategy
import strategy.WaveFormStrategy
import strategy.WhiteNoiseStrategy

// Simple wave form factory
class WaveFormFactory {

    fun create(name: String, lineNumber: Int): WaveFormStrategy = when (name) {
        "sin" -> SineWaveStrategy()
        "square" -> SquareWaveStrategy()
        "saw" -> SawtoothWaveStrategy()
        "whitenoise" -> WhiteNoiseStrategy()
        else -> throw IllegalArgumentException("Line $lineNumber: Unknown wave form strategy $name")
    }
}
