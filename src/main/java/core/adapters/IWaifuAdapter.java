package core.adapters;

import core.entities.Dialog;
import core.entities.WaifuImage;

import java.util.List;
import java.util.Map;

public interface IWaifuAdapter {

    String getName();

    List<String> getSkinNames();

    int getSkinCount();

    WaifuImage[] getImageSizeSet(int skinNumber);

    Map<String, List<Dialog>> getDialogs();

    String onTouchEventKey();

    String onIdleEventKey();

    String onLoginEventKey();

}
