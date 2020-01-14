package com.kitsunecode.mms.core.utils;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class WaifuUtils {

    public static IWaifuAdapter getWaifuFromAdapterName(String adapterName, String shipName) {
        try {
            Class<?> c = Class.forName("com.kitsunecode.mms.core.adapters.impl." + adapterName);

            // Someone could cheat in some way (??)
            // Prevention is better than cure lol
            if (IWaifuAdapter.class.isAssignableFrom(c)) {
                Util.checkFolders(shipName); // Create folders
                return (IWaifuAdapter) c.getConstructor(String.class).newInstance(shipName);
            }

            throw new StartFailedException("The chosen adapter is not a WaifuAdapter!");
        } catch (ClassNotFoundException e) {
            throw new StartFailedException("Adapter '" + adapterName + "' does not exist");
        } catch (NoSuchMethodException e) {
            throw new StartFailedException("Adapter has no constructor that takes the name as parameter");
        } catch (IllegalAccessException | InstantiationException e) {
            throw new StartFailedException("Critical error while instancing the waifu");
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof StartFailedException) {
                throw (StartFailedException) e.getCause(); // Throw already handled exception
            }
            throw new StartFailedException(e.getCause().getMessage());
        } catch (Exception e) {
            throw new StartFailedException("Critical error while creating the adapter: " + e.getMessage());
        }
    }

    public static byte[] getShipImage(IWaifuAdapter waifuAdapter, int skinIndex) throws IOException {
        System.out.println("Getting skin index: " + skinIndex);
        String url = waifuAdapter.getSkin(skinIndex);
        String fileName = Util.fileFromUrl(url);
        return IWaifuAdapter.downloadFile(waifuAdapter, url, fileName);
    }

}
