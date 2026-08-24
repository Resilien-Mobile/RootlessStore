package com.baidaidai.illusioncube

import com.baidaidai.illusioncube.application.conf.ValidateConfUseCase
import com.baidaidai.illusioncube.application.json.ValidateJsonUseCase
import com.baidaidai.illusioncube.application.prop.EncodePropAsJsonUseCase
import com.baidaidai.illusioncube.application.prop.ValidatePropUseCase
import com.baidaidai.illusioncube.application.yaml.ValidateYamlUseCase
import com.baidaidai.illusioncube.domain.type.ConfigType

class IllusionCube (
    private val rawConfig: String,
){
    private var resolvedConfigType: ConfigType? = null
    val configType = resolvedConfigType

    fun resolveConfigType(): ConfigType {
        when{
            Json.validate(rawConfig) -> {
                resolvedConfigType = ConfigType.Json
                return ConfigType.Json
            }
            Prop.validate(rawConfig) -> {
                resolvedConfigType = ConfigType.Prop
                return ConfigType.Prop
            }
            else -> {
                resolvedConfigType = ConfigType.Error
                return ConfigType.Error
            }
        }
    }

    // Json
    class Json(
        private val rawConfig: String
    ){
        companion object {
            fun validate(rawConfig: String): Boolean{
                val validateJsonUseCase = ValidateJsonUseCase()
                return validateJsonUseCase(rawConfig)
            }
        }
    }

    // Prop
    class Prop(
        private val rawConfig: String
    ){
        fun encodeAsJson(): String{
            val encodePropAsJsonUseCase = EncodePropAsJsonUseCase()
            return encodePropAsJsonUseCase(rawConfig)
        }
        companion object {
            fun validate(rawConfig: String): Boolean{
                val validatePropUseCase = ValidatePropUseCase()
                return validatePropUseCase(rawConfig)
            }
        }

    }

    // Yaml
    class Yaml(){
        companion object {
            fun validate(): Boolean{
                val validateYamlUseCase = ValidateYamlUseCase()
                return validateYamlUseCase()
            }
        }

    }

    // Conf
    class Conf(){
        companion object {
            fun validate(): Boolean{
                val validateConfUseCase = ValidateConfUseCase()
                return validateConfUseCase()
            }
        }

    }

}
