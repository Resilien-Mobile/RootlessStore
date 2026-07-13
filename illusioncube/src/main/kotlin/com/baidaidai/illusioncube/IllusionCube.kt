package com.baidaidai.illusioncube

import com.baidaidai.illusioncube.application.conf.JudgeIfConfUseCase
import com.baidaidai.illusioncube.application.json.JudgeIfJsonUseCase
import com.baidaidai.illusioncube.application.prop.ConvertPropToJsonUseCase
import com.baidaidai.illusioncube.application.prop.JudgeIfPropUseCase
import com.baidaidai.illusioncube.application.yaml.JudgeIfYamlUseCase
import com.baidaidai.illusioncube.domain.type.ConfigType

class IllusionCube (
    private val rawConfig: String,
){
    private var _configType: ConfigType? = null
    val configType = _configType

    fun judge(): ConfigType {
        when{
            Json.judgeIfJson(rawConfig) -> {
                _configType = ConfigType.Json
                return ConfigType.Json
            }
            Prop.judgeIfProp(rawConfig) -> {
                _configType = ConfigType.Prop
                return ConfigType.Prop
            }
            else -> {
                _configType = ConfigType.Error
                return ConfigType.Error
            }
        }
    }

    // Json
    class Json(
        private val rawConfig: String
    ){
        companion object {
            fun judgeIfJson(rawConfig: String): Boolean{
                val judgeIfJsonUseCase = JudgeIfJsonUseCase()
                return judgeIfJsonUseCase(rawConfig)
            }
        }
    }

    // Prop
    class Prop(
        private val rawConfig: String
    ){
        fun convertPropToJson(): String{
            val convertPropToJsonUseCase = ConvertPropToJsonUseCase()
            return convertPropToJsonUseCase(rawConfig)
        }
        companion object {
            fun judgeIfProp(rawConfig: String): Boolean{
                val judgeIfPropUseCase = JudgeIfPropUseCase()
                return judgeIfPropUseCase(rawConfig)
            }
        }

    }

    // Yaml
    class Yaml(){
        companion object {
            fun judgeIfYaml(): Boolean{
                val judgeIfYamlUseCase = JudgeIfYamlUseCase()
                return judgeIfYamlUseCase()
            }
        }

    }

    // Conf
    class Conf(){
        companion object {
            fun judgeIfConf(): Boolean{
                val judgeIfConfUseCase = JudgeIfConfUseCase()
                return judgeIfConfUseCase()
            }
        }

    }

}
