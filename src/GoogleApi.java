import com.alibaba.fastjson.JSONArray;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;

/**
 * @author coderyong
 */
public class GoogleApi {

    private static final String TKK = "TKK";
    private static final String PATH = "getTk.js";

    private static ScriptEngine engine = null;

    static {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("javascript");
        Reader scriptReader = null;
        try {
            scriptReader = new InputStreamReader(new FileInputStream(PATH), "utf-8");
            engine.eval(scriptReader);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scriptReader != null) {
                try {
                    scriptReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private GoogleApi() {
//        setProperty("122.224.227.202", "3128");
    }

    private static class GoogleApiHolder {
        private final static GoogleApi INSTANCE = new GoogleApi();
    }

    public static GoogleApi getInstance() {
        return GoogleApiHolder.INSTANCE;
    }

    public static void setProperty(String ip, String port) {
        System.setProperty("http.proxyHost", ip);
        System.setProperty("http.proxyPort", port);
    }

    private String getTKK() {
        try {
            String result = Http.get("https://translate.google.cn/");
            if (!Utils.isEmpty(result)) {
                if (result.contains(TKK)) {
                    //以TKK字符分割取后面，再以分号分割取第一串，结果为：='427731.663178634'
                    String tkk = result.split(TKK)[1].split(";")[0].trim();
                    tkk = tkk.substring(2, tkk.length() - 1);
                    return tkk;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTK(String word, String tkk) {
        String result = null;
        try {
            if (engine instanceof Invocable) {
                Invocable invocable = (Invocable) engine;
                result = (String) invocable.invokeFunction("tk", new Object[]{word, tkk});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String translate(String word, String from, String to) {
        String tkk = getTKK();
        if (Utils.isEmpty(word, tkk)) {
            return null;
        }
        String tk = getTK(word, tkk);
        try {
            word = URLEncoder.encode(word, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder("https://translate.google.cn/translate_a/single?client=t");
        builder.append("&sl=").append(from);
        builder.append("&tl=").append(to);
        builder.append("&hl=zh-CN&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&source=btn&kc=0");
        builder.append("&tk=").append(tk);
        builder.append("&q=").append(word);
        try {
            String result = Http.get(builder.toString());
            JSONArray array = ((JSONArray) JSONArray.parse(result)).getJSONArray(0);
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < array.size(); i++) {
                String str = array.getJSONArray(i).getString(0);
                if (!Utils.isEmpty(str)) {
                    b.append(str);
                }
            }
            return b.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String result = GoogleApi.getInstance()
                .translate("Patient laid in the lateral position. A soft cushion was placed beneath the waist to make it slightly protrude towards the affected side, in order to increase the height of the affected intervertebral foramen. The labeled operating space was scanned by a C-arm X-ray machine. The computer image processing system was used to measure the distance from the puncture point (for L3/4, the puncture point was 8–12 cm from the middle line; for L4/5 and L5/S1, the puncture point was 12–14 cm from the middle line). After the puncture site was marked, the operation was performed under local anesthesia combined with analgesic drugs. Local infiltration anesthesia was induced by 0.5% lidocaine. The puncture needle was inserted through the entry point, and the skin and fascia above the iliac crest were anesthetized. When the needle reached the bony structure, it was confirmed that the needle had reached the ventral margin of the articular facet of the superior articular process. Then, 2–3 ml of 0.5% lidocaine was locally injected. The puncture needle was slightly bent to make the tip and end of the needle bend towards the ventral side. The puncture needle was slightly pushed to the position between the spinous process and medial margin of the vertebral arch on the anteroposterior X-ray film, while the needle was positioned at the upper edge of the inferior vertebral body on the lateral film. A guide wire was inserted, and the puncture needle was removed. Then, a 0.8-cm long incision was made along the puncture site. A small amount of the tip of the facet of the superior articular process was abraded layer by layer with the aid of the expansion tube, guide rod, and trephine, in order to expand the lateral intervertebral foramen and establish surgical access. After inserting the access, a working channel slope was placed close to the intervertebral disc. A C-arm X-ray machine was used to determine whether the puncture needle entered into the intervertebral space along the channel. Discography was performed using the mixed solution of methylene blue and iohexol at a ratio of 1:9. The presence of exudation of the contrast agent to the spinal canal was observed on the anteroposterior film. The operation for the extirpation of the protruded intervertebral disc, decompression of the nerve root, intradiscal electrothermal annuloplasty, and hemostasis were performed using an endoscope. The degree of nerve root relaxation was determined by the nerve probe and the influence of water pressure on nerve fluctuation. After decompression, the surgical access was pulled out and the wound was sutured (Fig.  1).", "", "zh");
        System.out.println(result);
    }
}