package pe.aioo.openmoa.hangul

import org.junit.Assert
import org.junit.Test

// FIXME: Add more tests
class HangulAssemblerTest {

    @Test
    fun testAssembleSimpleHangul() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㄴ"))
        Assert.assertEquals("ㄴ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅡ"))
        Assert.assertEquals("느", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("누", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("뉴", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅇ"))
        Assert.assertEquals("늉", assembler.getUnresolved())
        Assert.assertEquals("늉", assembler.appendJamo("ㄱ"))
        Assert.assertEquals("ㄱ", assembler.getUnresolved())
    }

    @Test
    fun testAssembleSimpleHangulWithAraea() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㄴ"))
        Assert.assertEquals("ㄴ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("ㄴㆍ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("ㄴᆢ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertEquals("녀", assembler.getUnresolved())
    }

    @Test
    fun testAssembleComplexHangul() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㅂ"))
        Assert.assertEquals("ㅂ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅜ"))
        Assert.assertEquals("부", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("뷰", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertEquals("붜", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertEquals("붸", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㄹ"))
        Assert.assertEquals("뷀", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㄱ"))
        Assert.assertEquals("뷁", assembler.getUnresolved())
        Assert.assertEquals("뷁", assembler.appendJamo("ㄱ"))
        Assert.assertEquals("ㄱ", assembler.getUnresolved())
    }

    @Test
    fun testDisassembleJongSeong() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㅂ"))
        Assert.assertNull(assembler.appendJamo("ㅜ"))
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertNull(assembler.appendJamo("ㄹ"))
        Assert.assertNull(assembler.appendJamo("ㄱ"))
        Assert.assertEquals("뷀", assembler.appendJamo("ㅣ"))
        Assert.assertEquals("기", assembler.getUnresolved())
    }

    @Test
    fun testDisassembleJongSeongWithAraea() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㅂ"))
        Assert.assertNull(assembler.appendJamo("ㅜ"))
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertNull(assembler.appendJamo("ㄹ"))
        Assert.assertNull(assembler.appendJamo("ㄱ"))
        Assert.assertEquals("뷀", assembler.appendJamo("ㆍ"))
        Assert.assertEquals("ㄱㆍ", assembler.getUnresolved())
    }

    @Test
    fun testRemoveLastJamo() {
        val assembler = HangulAssembler()
        assembler.appendJamo("ㄴ")
        assembler.appendJamo("ㅏ")
        Assert.assertEquals("나", assembler.getUnresolved())
        assembler.removeLastJamo()
        Assert.assertEquals("ㄴ", assembler.getUnresolved())
        assembler.removeLastJamo()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testRemoveLastJamoWithJongseong() {
        val assembler = HangulAssembler()
        assembler.appendJamo("ㄴ")
        assembler.appendJamo("ㅏ")
        assembler.appendJamo("ㄴ")
        Assert.assertEquals("난", assembler.getUnresolved())
        assembler.removeLastJamo()
        Assert.assertEquals("나", assembler.getUnresolved())
    }

    @Test
    fun testRemoveLastJamoWithCompoundJongseong() {
        // "닭": ㄷ+ㅏ+ㄹ+ㄱ → ㄺ 합자 → 백스페이스 → "달" (ㄹ 유지, ㄱ만 제거)
        val assembler = HangulAssembler()
        assembler.appendJamo("ㄷ")
        assembler.appendJamo("ㅏ")
        assembler.appendJamo("ㄹ")
        assembler.appendJamo("ㄱ")
        Assert.assertEquals("닭", assembler.getUnresolved())
        assembler.removeLastJamo()
        Assert.assertEquals("달", assembler.getUnresolved())
    }

}