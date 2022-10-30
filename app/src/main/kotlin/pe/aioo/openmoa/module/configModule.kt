package pe.aioo.openmoa.module

import org.koin.dsl.module
import pe.aioo.openmoa.config.Config

val configModule = module {
    single { Config() }
}