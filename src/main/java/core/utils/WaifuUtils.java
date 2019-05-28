package core.utils;

import core.adapters.IWaifuAdapter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class WaifuUtils {

    public static IWaifuAdapter getWaifuFromAdapterName(String adapterName, String shipName) {
        try {
            Class<?> c = Class.forName("core.adapters.impl." + adapterName);

            // Someone could cheat in some way (??)
            // Prevention is better than cure lol
            if (IWaifuAdapter.class.isAssignableFrom(c)) {
                Util.checkFolders(shipName); // Create folders
                return (IWaifuAdapter) c.getConstructor(String.class).newInstance(shipName);
            }

            System.out.println("The chosen adapter is not a WaifuAdapter!");
        } catch (ClassNotFoundException e) {
            System.out.println("Adapter " + adapterName + " does not exist");
        } catch (NoSuchMethodException e) {
            System.out.println("Adapter has no constructor that takes the name as parameter");
        } catch (IllegalAccessException | InstantiationException e) {
            System.out.println("Critical error while instancing the waifu");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println("The " + adapterName + " failed to start, message: " + e.getCause().getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] getShipImage(IWaifuAdapter waifuAdapter, int skinIndex) throws IOException {
        System.out.println("Getting skin index: " + skinIndex);
        String url = waifuAdapter.getSkinUrl(skinIndex);
        String fileName = waifuAdapter.getName() + "_" + skinIndex + ".png";
        return IWaifuAdapter.downloadFile(waifuAdapter, url, fileName);
    }

}
