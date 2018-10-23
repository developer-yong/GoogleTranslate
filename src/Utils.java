/**
 * @author coderyong
 */
public class Utils {

    public static boolean isEmpty(String... strArr) {
        for (String s : strArr) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        } else {
            int len = str.length();
            if (len == 0) {
                return true;
            } else {
                int i = 0;

                while (i < len) {
                    switch (str.charAt(i)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ':
                            ++i;
                            break;
                        default:
                            return false;
                    }
                }

                return true;
            }
        }
    }
}