package pe.aioo.openmoa.hangul

import org.junit.Assert
import org.junit.Test

class MoeumGestureProcessorTest {

    @Test
    fun testYaGesture() {
        val processor = MoeumGestureProcessor()
        processor.appendMoeum("ㅏ")
        processor.appendMoeum("ㅓ")
        processor.appendMoeum("ㅏ")
        Assert.assertEquals("ㅑ", processor.resolveMoeumList())
    }

    @Test
    fun testSingleMoeum() {
        Assert.assertEquals("ㅏ", MoeumGestureProcessor().apply { appendMoeum("ㅏ") }.resolveMoeumList())
        Assert.assertEquals("ㅓ", MoeumGestureProcessor().apply { appendMoeum("ㅓ") }.resolveMoeumList())
        Assert.assertEquals("ㅗ", MoeumGestureProcessor().apply { appendMoeum("ㅗ") }.resolveMoeumList())
        Assert.assertEquals("ㅜ", MoeumGestureProcessor().apply { appendMoeum("ㅜ") }.resolveMoeumList())
    }

    @Test
    fun testWaGesture() {
        val processor = MoeumGestureProcessor()
        processor.appendMoeum("ㅗ")
        processor.appendMoeum("ㅏ")
        Assert.assertEquals("ㅘ", processor.resolveMoeumList())
    }

    @Test
    fun testYoGesture() {
        // ㅗ → ㅚ(+ㅜ) → ㅛ(+ㅗ)
        val processor = MoeumGestureProcessor()
        processor.appendMoeum("ㅗ")
        processor.appendMoeum("ㅜ")
        processor.appendMoeum("ㅗ")
        Assert.assertEquals("ㅛ", processor.resolveMoeumList())
    }

    @Test
    fun testEuiGesture() {
        // ㅡL → ㅢ(+ㅣL)
        val processor = MoeumGestureProcessor()
        processor.appendMoeum("ㅡL")
        processor.appendMoeum("ㅣL")
        Assert.assertEquals("ㅢ", processor.resolveMoeumList())
    }

    @Test
    fun testEmptyReturnsNull() {
        Assert.assertNull(MoeumGestureProcessor().resolveMoeumList())
    }

    @Test
    fun testClearsAfterResolve() {
        val processor = MoeumGestureProcessor()
        processor.appendMoeum("ㅏ")
        Assert.assertEquals("ㅏ", processor.resolveMoeumList())
        Assert.assertNull(processor.resolveMoeumList())
    }

}
