package ui10.layout;

import ui10.base.LayoutContext1;
import ui10.base.LayoutProtocol;
import ui10.geom.Size;

import java.util.List;

// legyen ehelyett inkább extra paraméter (mapben?) a preferredSizeImpl-ben. de mi legyen a return érték?
public interface Flowable {

    // metódus név érthetetlen, inkább computeSizeInFlow vagy ilyesminek kéne lennie
    // a paraméterekben megadott értékeket hívjuk inkább északi és keleti constrainteknek?
    // viszont ami lényegesebb: mi legyen a replacementekkel?
    FlowableElementSize layoutFlowable(FlowableLayoutInput constraints, LayoutContext1 context);

    // kéne egy preferredHeight paraméter, amiben benne van hogy eddig milyen magasak voltak a sorok?
    // baseline?
    record FlowableLayoutInput(Size rightMax, Size bottomMax) {
    }

    record FlowableElementSize(boolean wrapBeforeFirst, List<Size> lineSizes) {
    }

    LayoutProtocol<FlowableLayoutInput, FlowableElementSize> PROTOCOL = (e, constraints, context)->{
        if (e instanceof Flowable f)
            return f.layoutFlowable(constraints, context);
        else {
            Size s = context.preferredSize(e, new BoxConstraints(Size.ZERO, constraints.rightMax).withUnboundedWidth());
            if (constraints.rightMax.width() >= s.width() && constraints.rightMax.height() >= s.height())
                return new FlowableElementSize(false, List.of(s));
            else {
                s = context.preferredSize(e, new BoxConstraints(Size.ZERO, constraints.bottomMax));
                return new FlowableElementSize(true, List.of(s));
            }
        }
    };
}
