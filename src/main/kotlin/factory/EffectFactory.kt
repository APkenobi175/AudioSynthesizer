package factory

import decorator.ADSEffectDecorator
import decorator.ClipDistortionEffectDecorator
import decorator.TanhDistortionEffectDecorator
import decorator.VolumeEffectDecorator
import strategy.WaveFormStrategy


class EffectFactory {

    fun create(segment: String, strategy: WaveFormStrategy, lineNumber: Int): WaveFormStrategy {
        val segments = segment.split("$")

        // Left side of $ is the effect, right side is the arguments
        val args = ArrayList<Double>()
        // Start from second segment as first argument, and add to args array
        for (i in 1 until segments.size){
            val v = segments[i].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid effect argument in line $lineNumber: ${segments[i]}")
            args.add(v)
        }

        val numArgs = args.size
        // Get the effect
        if (segments[0] == "vol"){
            // Volume has one argument
            if(numArgs != 1) {
                throw IllegalArgumentException("Expected 1 argument for vol effect, got $numArgs in line $lineNumber")
            }

            return VolumeEffectDecorator(strategy, args[0])
        } else if (segments[0] == "ads"){
            // ADS has 3 arguments
            if (numArgs != 3){
                throw IllegalArgumentException("Expected 3 arguments for ads effect, got $numArgs in line $lineNumber")
            }
            if (args[0] < 0) throw IllegalArgumentException("Line $lineNumber: ads attackEnd must be >= 0, got ${args[0]}")
            if (args[1] < args[0]) throw IllegalArgumentException("Line $lineNumber: ads decayEnd (${args[1]}) must be >= attackEnd (${args[0]})")

            return ADSEffectDecorator(strategy, args[0], args[1], args[2])

        } else if (segments[0] == "tanh"){
            // TANH has 1 argument
            if (numArgs != 1){
                throw IllegalArgumentException("Expected 1 argument for tanh effect, got $numArgs in line $lineNumber")
            }

            return TanhDistortionEffectDecorator(strategy, args[0])
        } else if (segments[0] == "clip"){
            // clip has 1 argument
            if (numArgs != 1){
                throw IllegalArgumentException("Expected 1 argument for clip effect, got $numArgs in line $lineNumber")
            }
            return ClipDistortionEffectDecorator(strategy, args[0])
        }else{
            throw IllegalArgumentException("Invalid effect argument in line $lineNumber: ${segments[0]}")
        }
    }
}
