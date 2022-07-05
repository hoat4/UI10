package ui10.base;

import java.util.List;

public interface ViewProvider {

    Element makeView(ElementModel model);

    static Element makeView(ElementModel model, List<ViewProvider> viewProviders) {
        for (ViewProvider viewProvider : viewProviders) {
            Element e = viewProvider.makeView(model);
            if (e != null)
                return e;
        }

        throw new RuntimeException("couldn't find view provider for " + model+" (providers: "+viewProviders+")");
    }
}
