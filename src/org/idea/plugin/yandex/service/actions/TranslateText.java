package org.idea.plugin.yandex.service.actions;


import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import org.idea.plugin.yandex.service.state.State;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

/**
 * API Yandex Translate https://tech.yandex.ru/translate/doc/dg/reference/translate-docpage/
 */
public class TranslateText extends EditorAction {

    public TranslateText() {
        super(null);
        this.setupHandler(new EditorWriteActionHandler(true) {
            @Override
            public void executeWriteAction(Editor editor, @Nullable Caret caret, DataContext dataContext) {
                Document document = editor.getDocument();

                if (editor == null || document == null || !document.isWritable()) {
                    return;
                }

                final State state = ServiceManager.getService(State.class);

                SelectionModel selectionModel = editor.getSelectionModel();
                TextRange charsRange = new TextRange(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
                String text = document.getText().substring(charsRange.getStartOffset(), charsRange.getEndOffset());
                try {
                    String lang = state.translateDirection;
                    String apiKey = state.translateApiKey;
                    String url = "https://translate.yandex.net/api/v1.5/tr/translate?key="+apiKey+"&lang="+lang+"&text="+ URLEncoder.encode(text, "UTF-8");
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputStream is = new URL(url).openStream();
                    org.w3c.dom.Document doc = db.parse(is);
                    Node trsln = doc.getDocumentElement();
                    String code = trsln.getAttributes().getNamedItem("code").getNodeValue();
                    switch (code){
                        case "200" : text = trsln.getFirstChild().getTextContent(); break;
                        case "401" : Messages.showErrorDialog("Error code:401 Неправильный API-ключ", "TranslateText"); break;
                        case "402" : Messages.showErrorDialog("Error code:402 API-ключ заблокирован", "TranslateText"); break;
                        case "404" : Messages.showErrorDialog("Error code:404 Превышено суточное ограничение на объем переведенного текста", "TranslateText"); break;
                        case "413" : Messages.showErrorDialog("Error code:413 Превышен максимально допустимый размер текста", "TranslateText"); break;
                        case "422" : Messages.showErrorDialog("Error code:422 Текст не может быть переведен", "TranslateText"); break;
                        case "501" : Messages.showErrorDialog("Error code:501 Заданное направление перевода не поддерживается", "TranslateText"); break;
                        default: Messages.showErrorDialog("Error code:"+code,"TranslateText"); break;
                    }
                } catch (Exception e) {
                    PluginManager.getLogger().error("TranslateText",e);
                    Messages.showErrorDialog(e.getMessage(), "TranslateText");
                }
                document.replaceString(charsRange.getStartOffset(), charsRange.getEndOffset(), text);
            }
        });
    }
}
