package org.idea.plugin.yandex.service.actions;


import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * API Yandex speller https://tech.yandex.ru/speller/doc/dg/reference/checkText-docpage/
 */
public class CheckText extends EditorAction {

    public CheckText() {
        super(null);
        this.setupHandler(new EditorWriteActionHandler(true) {
            @Override
            public void executeWriteAction(Editor editor, @Nullable Caret caret, DataContext dataContext) {
                Document document = editor.getDocument();

                if (editor == null || document == null || !document.isWritable()) {
                    return;
                }

                SelectionModel selectionModel = editor.getSelectionModel();
                TextRange charsRange = new TextRange(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
                String text = document.getText().substring(charsRange.getStartOffset(), charsRange.getEndOffset());

                Map<String, String> repl = new HashMap<>();
                try {
                    String url = "http://speller.yandex.net/services/spellservice/checkText?options=7&text="+ URLEncoder.encode(text, "UTF-8");
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputStream is = new URL(url).openStream();
                    org.w3c.dom.Document doc = db.parse(is);
                    NodeList errors = doc.getDocumentElement().getElementsByTagName("error");
                    for(int i = 0; i < errors.getLength(); i++) {
                        Node e = errors.item(i);
                        //getFirst and getLast is bad.
                        String word = e.getFirstChild().getTextContent();
                        String s = e.getLastChild().getTextContent();
                        repl.put(word, s);
                    }
                } catch (Exception e) {
                    PluginManager.getLogger().error("CheckText",e);
                    Messages.showErrorDialog(e.getMessage(), "CheckText");
                }

                for(Map.Entry<String, String> me : repl.entrySet()) {
                    text = text.replaceAll(me.getKey(), me.getValue());
                }
                document.replaceString(charsRange.getStartOffset(), charsRange.getEndOffset(), text);
            }
        });
    }
}
