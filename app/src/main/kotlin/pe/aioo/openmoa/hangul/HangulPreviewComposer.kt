package pe.aioo.openmoa.hangul

import com.github.kimkevin.hangulparser.HangulParser
import com.github.kimkevin.hangulparser.HangulParserException

object HangulPreviewComposer {

    fun compose(jaum: String, moeum: String?): String {
        moeum ?: return jaum
        return try {
            HangulParser.assemble(listOf(jaum, moeum))
        } catch (_: HangulParserException) {
            jaum
        }
    }

}
