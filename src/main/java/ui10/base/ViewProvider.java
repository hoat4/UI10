package ui10.base;

import java.util.List;

public interface ViewProvider {

    ViewProviderResult makeView(Element model);

    sealed interface ViewProviderResult {
    }

    record ViewResult(Element view) implements ViewProviderResult {
    }

    enum NoViewResult implements ViewProviderResult {NO_VIEW, UNKNOWN_ELEMENT}

    static Element makeView(Element model, List<ViewProvider> viewProviders) {
        for (ViewProvider viewProvider : viewProviders) {
            ViewProviderResult e = viewProvider.makeView(model);
            switch (e) {
                case ViewResult vr:
                    return vr.view;
                case NoViewResult nr:
                    switch (nr) {
                        case NO_VIEW -> {
                            return null;
                        }
                        case UNKNOWN_ELEMENT -> {
                        }
                    }
            }
        }

        throw new RuntimeException("couldn't find view provider for " + model + " (providers: " + viewProviders + ")");
    }
}
