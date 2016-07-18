package foundation.stack.datamill.reflection.impl;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class TypeSwitchTest {
    @Test
    public void doSwitchWithoutWrappers() {
        TypeSwitch<String, String, String> s = new TypeSwitch<String, String, String>() {
            @Override
            protected String caseBoolean(String value1, String value2) {
                return "bool" + value1 + value2;
            }

            @Override
            protected String caseByte(String value1, String value2) {
                return "byte" + value1 + value2;
            }

            @Override
            protected String caseCharacter(String value1, String value2) {
                return "c" + value1 + value2;
            }

            @Override
            protected String caseShort(String value1, String value2) {
                return "s" + value1 + value2;
            }

            @Override
            protected String caseInteger(String value1, String value2) {
                return "i" + value1 + value2;
            }

            @Override
            protected String caseLong(String value1, String value2) {
                return "l" + value1 + value2;
            }

            @Override
            protected String caseFloat(String value1, String value2) {
                return "f" + value1 + value2;
            }

            @Override
            protected String caseDouble(String value1, String value2) {
                return "d" + value1 + value2;
            }

            @Override
            protected String caseLocalDateTime(String value1, String value2) {
                return "ldt" + value1 + value2;
            }

            @Override
            protected String caseByteArray(String value1, String value2) {
                return "ba" + value1 + value2;
            }

            @Override
            protected String caseString(String value1, String value2) {
                return "str" + value1 + value2;
            }

            @Override
            protected String defaultCase(String value1, String value2) {
                return "def" + value1 + value2;
            }
        };

        assertEquals("boolv1v2", s.doSwitch(boolean.class, "v1", "v2"));
        assertEquals("boolv1v2", s.doSwitch(Boolean.class, "v1", "v2"));
        assertEquals("bytev1v2", s.doSwitch(byte.class, "v1", "v2"));
        assertEquals("bytev1v2", s.doSwitch(Byte.class, "v1", "v2"));
        assertEquals("cv1v2", s.doSwitch(char.class, "v1", "v2"));
        assertEquals("cv1v2", s.doSwitch(Character.class, "v1", "v2"));
        assertEquals("sv1v2", s.doSwitch(short.class, "v1", "v2"));
        assertEquals("sv1v2", s.doSwitch(Short.class, "v1", "v2"));
        assertEquals("iv1v2", s.doSwitch(int.class, "v1", "v2"));
        assertEquals("iv1v2", s.doSwitch(Integer.class, "v1", "v2"));
        assertEquals("lv1v2", s.doSwitch(long.class, "v1", "v2"));
        assertEquals("lv1v2", s.doSwitch(Long.class, "v1", "v2"));
        assertEquals("fv1v2", s.doSwitch(float.class, "v1", "v2"));
        assertEquals("fv1v2", s.doSwitch(Float.class, "v1", "v2"));
        assertEquals("dv1v2", s.doSwitch(double.class, "v1", "v2"));
        assertEquals("dv1v2", s.doSwitch(Double.class, "v1", "v2"));
        assertEquals("ldtv1v2", s.doSwitch(LocalDateTime.class, "v1", "v2"));
        assertEquals("bav1v2", s.doSwitch(byte[].class, "v1", "v2"));
        assertEquals("strv1v2", s.doSwitch(String.class, "v1", "v2"));
        assertEquals("defv1v2", s.doSwitch(Object.class, "v1", "v2"));
    }

    @Test
    public void doSwitchWithWrappers() {
        TypeSwitch<String, String, String> s = new TypeSwitch<String, String, String>() {
            @Override
            protected String caseBoolean(String value1, String value2) {
                return "bool" + value1 + value2;
            }

            @Override
            protected String caseBooleanWrapper(String value1, String value2) {
                return "boolw" + value1 + value2;
            }

            @Override
            protected String caseByte(String value1, String value2) {
                return "byte" + value1 + value2;
            }

            @Override
            protected String caseByteWrapper(String value1, String value2) {
                return "bytew" + value1 + value2;
            }

            @Override
            protected String caseCharacter(String value1, String value2) {
                return "c" + value1 + value2;
            }

            @Override
            protected String caseCharacterWrapper(String value1, String value2) {
                return "cw" + value1 + value2;
            }

            @Override
            protected String caseShort(String value1, String value2) {
                return "s" + value1 + value2;
            }

            @Override
            protected String caseShortWrapper(String value1, String value2) {
                return "sw" + value1 + value2;
            }

            @Override
            protected String caseInteger(String value1, String value2) {
                return "i" + value1 + value2;
            }

            @Override
            protected String caseIntegerWrapper(String value1, String value2) {
                return "iw" + value1 + value2;
            }

            @Override
            protected String caseLong(String value1, String value2) {
                return "l" + value1 + value2;
            }

            @Override
            protected String caseLongWrapper(String value1, String value2) {
                return "lw" + value1 + value2;
            }

            @Override
            protected String caseFloat(String value1, String value2) {
                return "f" + value1 + value2;
            }

            @Override
            protected String caseFloatWrapper(String value1, String value2) {
                return "fw" + value1 + value2;
            }

            @Override
            protected String caseDouble(String value1, String value2) {
                return "d" + value1 + value2;
            }

            @Override
            protected String caseDoubleWrapper(String value1, String value2) {
                return "dw" + value1 + value2;
            }

            @Override
            protected String caseLocalDateTime(String value1, String value2) {
                return "ldt" + value1 + value2;
            }

            @Override
            protected String caseByteArray(String value1, String value2) {
                return "ba" + value1 + value2;
            }

            @Override
            protected String caseString(String value1, String value2) {
                return "str" + value1 + value2;
            }

            @Override
            protected String defaultCase(String value1, String value2) {
                return "def" + value1 + value2;
            }
        };

        assertEquals("boolv1v2", s.doSwitch(boolean.class, "v1", "v2"));
        assertEquals("boolwv1v2", s.doSwitch(Boolean.class, "v1", "v2"));
        assertEquals("bytev1v2", s.doSwitch(byte.class, "v1", "v2"));
        assertEquals("bytewv1v2", s.doSwitch(Byte.class, "v1", "v2"));
        assertEquals("cv1v2", s.doSwitch(char.class, "v1", "v2"));
        assertEquals("cwv1v2", s.doSwitch(Character.class, "v1", "v2"));
        assertEquals("sv1v2", s.doSwitch(short.class, "v1", "v2"));
        assertEquals("swv1v2", s.doSwitch(Short.class, "v1", "v2"));
        assertEquals("iv1v2", s.doSwitch(int.class, "v1", "v2"));
        assertEquals("iwv1v2", s.doSwitch(Integer.class, "v1", "v2"));
        assertEquals("lv1v2", s.doSwitch(long.class, "v1", "v2"));
        assertEquals("lwv1v2", s.doSwitch(Long.class, "v1", "v2"));
        assertEquals("fv1v2", s.doSwitch(float.class, "v1", "v2"));
        assertEquals("fwv1v2", s.doSwitch(Float.class, "v1", "v2"));
        assertEquals("dv1v2", s.doSwitch(double.class, "v1", "v2"));
        assertEquals("dwv1v2", s.doSwitch(Double.class, "v1", "v2"));
        assertEquals("ldtv1v2", s.doSwitch(LocalDateTime.class, "v1", "v2"));
        assertEquals("bav1v2", s.doSwitch(byte[].class, "v1", "v2"));
        assertEquals("strv1v2", s.doSwitch(String.class, "v1", "v2"));
        assertEquals("defv1v2", s.doSwitch(Object.class, "v1", "v2"));
    }
}
