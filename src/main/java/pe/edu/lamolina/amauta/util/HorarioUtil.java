package pe.edu.lamolina.amauta.util;

public class HorarioUtil {
    public HorarioUtil() {
    }

    public static boolean eq(String a, String b) {
        return (a == null ? "" : a).equals(b == null ? "" : b);
    }

    public static int parseNum(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return -1; }
    }

    public static int parseHHmm(String s) {
        String[] p = s.trim().split(":");
        return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
    }

    public static String fmt(int x) {
        return String.format("%02d:%02d", (x/60)%24, x%60);
    }

    public static String fmtAmPm(int x) {
        int totalMin = x % (24*60);
        int h = totalMin / 60, m = totalMin % 60;
        String ap = h < 12 ? "am" : "pm";
        int h12 = h % 12; if (h12 == 0) h12 = 12;
        return String.format("%02d:%02d %s", h12, m, ap);
    }
}
