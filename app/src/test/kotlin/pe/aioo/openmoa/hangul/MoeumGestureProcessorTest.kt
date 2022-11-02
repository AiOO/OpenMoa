package pe.aioo.openmoa.hangul

import org.junit.Assert
import org.junit.Test

// FIXME: Add more tests
class MoeumGestureProcessorTest {

    @Test
    fun testYaGesture() {
        val processor = MoeumGestureProcessor()
        processor.appendMoeum("ㅏ")
        processor.appendMoeum("ㅓ")
        processor.appendMoeum("ㅏ")
        Assert.assertEquals("ㅑ", processor.resolveMoeumList())
    }

}