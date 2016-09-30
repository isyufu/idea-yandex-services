package org.idea.plugin.yandex.service.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * Created by Ivan on 28.09.2016.
 */
@com.intellij.openapi.components.State(
        name = "yandex.servises", storages = {
        @Storage(
                id = "yandex.servises",
                file = "$APP_CONFIG$/yandex.services.xml")
})
public class State implements PersistentStateComponent<State> {

    public String translateApiKey;
    public String translateDirection;

    public State() {
    }

    public void loadState(State state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public State getState() {
        return this;
    }
}