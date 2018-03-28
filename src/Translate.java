import com.jfinal.kit.StrKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author coderyong
 * @date 2018/3/28
 */
public class Translate {
    private static Map<String, String> translationLib = new HashMap<>();

    public static void main(String[] args) {
        String htmlContent = "";
//            Document document = Jsoup.parse(htmlContent);
//            Elements elements = translateElements(document.getElementsByClass(""));
        System.out.println(translate("hello"));
    }


    /**
     * 清除不需要的标签属性
     *
     * @param elements 标签集合
     */
    private static Elements translateElements(Elements elements) {

        for (Element e : elements) {
            Elements children = e.children();
            if ("table".equals(e.nodeName())) {
                continue;
            }
            if (children.size() < 1) {
                String c = e.text();
                e.text(translate(c));
                continue;
            } else {
                String c = e.ownText();
                if (StrKit.notBlank(c)) {
                    String childrenHtml = translateElements(children).outerHtml();
                    e.text(translate(c));
                    e.append(childrenHtml);
                    continue;
                } else {
                    translateElements(e.children());
                }
            }
        }
        return elements;
    }

    private static void saveTranslate(String content) {
        Pattern pattern = Pattern.compile(">\\s?(.*?)\\s?<");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String group = matcher.group();
            String c = group.substring(1, group.length() - 1).trim();

            if (c.length() > 10 || c.matches("^[a-zA-Z]{3,10}$")) {
                String after = c.replaceAll("&nbsp;", " ").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
                String result = translationLib.get(c);
                if (StrKit.isBlank(result)) {
                    GoogleApi googleApi = new GoogleApi();
                    result = googleApi.translate(after, "", "zh");
                    translationLib.put(c, result);
                }
                System.out.println("\n----------------------------------------------");
                System.out.println(c);
                System.out.println(result);
            }
        }
    }

    private static String translate(String content) {
        if (content != null && content.length() < 10 && !content.matches("^[a-zA-Z]{3,10}$")) {
            return "";
        }
        String result = translationLib.get(content);
        if (StrKit.isBlank(result)) {
            GoogleApi googleApi = new GoogleApi();
            result = googleApi.translate(content, "", "zh");
            translationLib.put(content, result);
        }
        return result;
    }
}
