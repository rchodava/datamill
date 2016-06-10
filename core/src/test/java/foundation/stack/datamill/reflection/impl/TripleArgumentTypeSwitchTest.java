package foundation.stack.datamill.reflection.impl;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class TripleArgumentTypeSwitchTest {
    @Test
    public void doSwitchWithoutWrappers() {
        TripleArgumentTypeSwitch<String, String, String, String> s = 
                new TripleArgumentTypeSwitch<String, String, String, String>() {
            @Override
            protected String caseBoolean(String value1, String value2, String value3) {
                return "bool" + value1 + value2 + value3;
            }

            @Override
            protected String caseByte(String value1, String value2, String value3) {
                return "byte" + value1 + value2 + value3;
            }

            @Override
            protected String caseCharacter(String value1, String value2, String value3) {
                return "c" + value1 + value2 + value3;
            }

            @Override
            protected String caseShort(String value1, String value2, String value3) {
                return "s" + value1 + value2 + value3;
            }

            @Override
            protected String caseInteger(String value1, String value2, String value3) {
                return "i" + value1 + value2 + value3;
            }

            @Override
            protected String caseLong(String value1, String value2, String value3) {
                return "l" + value1 + value2 + value3;
            }

            @Override
            protected String caseFloat(String value1, String value2, String value3) {
                return "f" + value1 + value2 + value3;
            }

            @Override
            protected String caseDouble(String value1, String value2, String value3) {
                return "d" + value1 + value2 + value3;
            }

            @Override
            protected String caseLocalDateTime(String value1, String value2, String value3) {
                return "ldt" + value1 + value2 + value3;
            }

            @Override
            protected String caseByteArray(String value1, String value2, String value3) {
                return "ba" + value1 + value2 + value3;
            }

            @Override
            protected String defaultCase(String value1, String value2, String value3) {
                return "def" + value1 + value2 + value3;
            }
        };

        assertEquals("boolv1v2v3", s.doSwitch(boolean.class, "v1", "v2", "v3"));
        assertEquals("boolv1v2v3", s.doSwitch(Boolean.class, "v1", "v2", "v3"));
        assertEquals("bytev1v2v3", s.doSwitch(byte.class, "v1", "v2", "v3"));
        assertEquals("bytev1v2v3", s.doSwitch(Byte.class, "v1", "v2", "v3"));
        assertEquals("cv1v2v3", s.doSwitch(char.class, "v1", "v2", "v3"));
        assertEquals("cv1v2v3", s.doSwitch(Character.class, "v1", "v2", "v3"));
        assertEquals("sv1v2v3", s.doSwitch(short.class, "v1", "v2", "v3"));
        assertEquals("sv1v2v3", s.doSwitch(Short.class, "v1", "v2", "v3"));
        assertEquals("iv1v2v3", s.doSwitch(int.class, "v1", "v2", "v3"));
        assertEquals("iv1v2v3", s.doSwitch(Integer.class, "v1", "v2", "v3"));
        assertEquals("lv1v2v3", s.doSwitch(long.class, "v1", "v2", "v3"));
        assertEquals("lv1v2v3", s.doSwitch(Long.class, "v1", "v2", "v3"));
        assertEquals("fv1v2v3", s.doSwitch(float.class, "v1", "v2", "v3"));
        assertEquals("fv1v2v3", s.doSwitch(Float.class, "v1", "v2", "v3"));
        assertEquals("dv1v2v3", s.doSwitch(double.class, "v1", "v2", "v3"));
        assertEquals("dv1v2v3", s.doSwitch(Double.class, "v1", "v2", "v3"));
        assertEquals("ldtv1v2v3", s.doSwitch(LocalDateTime.class, "v1", "v2", "v3"));
        assertEquals("bav1v2v3", s.doSwitch(byte[].class, "v1", "v2", "v3"));
        assertEquals("defv1v2v3", s.doSwitch(String.class, "v1", "v2", "v3"));
    }

    @Test
    public void doSwitchWithWrappers() {
        TripleArgumentTypeSwitch<String, String, String, String> s =
                new TripleArgumentTypeSwitch<String, String, String, String>() {
            @Override
            protected String caseBoolean(String value1, String value2, String value3) {
                return "bool" + value1 + value2 + value3;
            }

            @Override
            protected String caseBooleanWrapper(String value1, String value2, String value3) {
                return "boolw" + value1 + value2 + value3;
            }

            @Override
            protected String caseByte(String value1, String value2, String value3) {
                return "byte" + value1 + value2 + value3;
            }

            @Override
            protected String caseByteWrapper(String value1, String value2, String value3) {
                return "bytew" + value1 + value2 + value3;
            }

            @Override
            protected String caseCharacter(String value1, String value2, String value3) {
                return "c" + value1 + value2 + value3;
            }

            @Override
            protected String caseCharacterWrapper(String value1, String value2, String value3) {
                return "cw" + value1 + value2 + value3;
            }

            @Override
            protected String caseShort(String value1, String value2, String value3) {
                return "s" + value1 + value2 + value3;
            }

            @Override
            protected String caseShortWrapper(String value1, String value2, String value3) {
                return "sw" + value1 + value2 + value3;
            }

            @Override
            protected String caseInteger(String value1, String value2, String value3) {
                return "i" + value1 + value2 + value3;
            }

            @Override
            protected String caseIntegerWrapper(String value1, String value2, String value3) {
                return "iw" + value1 + value2 + value3;
            }

            @Override
            protected String caseLong(String value1, String value2, String value3) {
                return "l" + value1 + value2 + value3;
            }

            @Override
            protected String caseLongWrapper(String value1, String value2, String value3) {
                return "lw" + value1 + value2 + value3;
            }

            @Override
            protected String caseFloat(String value1, String value2, String value3) {
                return "f" + value1 + value2 + value3;
            }

            @Override
            protected String caseFloatWrapper(String value1, String value2, String value3) {
                return "fw" + value1 + value2 + value3;
            }

            @Override
            protected String caseDouble(String value1, String value2, String value3) {
                return "d" + value1 + value2 + value3;
            }

            @Override
            protected String caseDoubleWrapper(String value1, String value2, String value3) {
                return "dw" + value1 + value2 + value3;
            }

            @Override
            protected String caseLocalDateTime(String value1, String value2, String value3) {
                return "ldt" + value1 + value2 + value3;
            }

            @Override
            protected String caseByteArray(String value1, String value2, String value3) {
                return "ba" + value1 + value2 + value3;
            }

            @Override
            protected String defaultCase(String value1, String value2, String value3) {
                return "def" + value1 + value2 + value3;
            }
        };

        assertEquals("boolv1v2v3", s.doSwitch(boolean.class, "v1", "v2", "v3"));
        assertEquals("boolwv1v2v3", s.doSwitch(Boolean.class, "v1", "v2", "v3"));
        assertEquals("bytev1v2v3", s.doSwitch(byte.class, "v1", "v2", "v3"));
        assertEquals("bytewv1v2v3", s.doSwitch(Byte.class, "v1", "v2", "v3"));
        assertEquals("cv1v2v3", s.doSwitch(char.class, "v1", "v2", "v3"));
        assertEquals("cwv1v2v3", s.doSwitch(Character.class, "v1", "v2", "v3"));
        assertEquals("sv1v2v3", s.doSwitch(short.class, "v1", "v2", "v3"));
        assertEquals("swv1v2v3", s.doSwitch(Short.class, "v1", "v2", "v3"));
        assertEquals("iv1v2v3", s.doSwitch(int.class, "v1", "v2", "v3"));
        assertEquals("iwv1v2v3", s.doSwitch(Integer.class, "v1", "v2", "v3"));
        assertEquals("lv1v2v3", s.doSwitch(long.class, "v1", "v2", "v3"));
        assertEquals("lwv1v2v3", s.doSwitch(Long.class, "v1", "v2", "v3"));
        assertEquals("fv1v2v3", s.doSwitch(float.class, "v1", "v2", "v3"));
        assertEquals("fwv1v2v3", s.doSwitch(Float.class, "v1", "v2", "v3"));
        assertEquals("dv1v2v3", s.doSwitch(double.class, "v1", "v2", "v3"));
        assertEquals("dwv1v2v3", s.doSwitch(Double.class, "v1", "v2", "v3"));
        assertEquals("ldtv1v2v3", s.doSwitch(LocalDateTime.class, "v1", "v2", "v3"));
        assertEquals("bav1v2v3", s.doSwitch(byte[].class, "v1", "v2", "v3"));
        assertEquals("defv1v2v3", s.doSwitch(String.class, "v1", "v2", "v3"));
    }
}
