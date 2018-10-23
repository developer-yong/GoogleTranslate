import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * @author coderyong
 * @date 2018/3/28
 */
public class Translate {
    private static Map<String, String> translationLib = new HashMap<>();

    public static void main(String[] args) {
//        String htmlContent = "";
//        Document document = Jsoup.parse(htmlContent);
//        Elements elements = translateElements(document.getElementsByClass(""));
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
            if (children.size() < 1) {
                String c = e.text();
                if (!Utils.isEmpty(c)) {
                    String translate = translate(c);
                    if (!Utils.isEmpty(translate)) {
                        e.text(translate);
                    }
                }
            } else {
                String c = e.ownText();
                if (!Utils.isEmpty(c)) {
                    String childrenHtml = translateElements(children).outerHtml();
                    String translate = translate(c);
                    if (Utils.isEmpty(translate)) {
                        e.text(translate);
                    }
                    e.append(childrenHtml);
                } else {
                    translateElements(e.children());
                }
            }
        }
        return elements;
    }

    private static String translate(String content) {
        if (content != null && content.length() < 10 && !content.matches("^[a-zA-Z]{3,10}$")) {
            return "";
        }
        String result = translationLib.get(content);
        if (Utils.isEmpty(result)) {
            result = GoogleApi.getInstance().translate(content, "", "zh");
            translationLib.put(content, result);
        }
        return result;
    }
}
