package com.kitsunecode.mms.core.utils;

import com.kitsunecode.mms.core.adapters.IWaifuAdapter;
import com.kitsunecode.mms.core.entities.exceptions.StartFailedException;

import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

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
        return Files.readAllBytes(waifuAdapter.downloadFile(url, fileName).toPath());
    }

    public static Area getOutline(BufferedImage i, int targetTransp) {

        // construct the GeneralPath
        GeneralPath gp = new GeneralPath();
        gp.moveTo(0, 0);

        boolean drawing = false;
        for (int y = 0; y < i.getHeight(); y++) {
            for (int x = 0; x < i.getWidth(); x++) {

                int rgb = i.getRGB(x, y);
                boolean isTransp = (rgb >>> 24) <= targetTransp;

                if (isTransp) {
                    if (drawing) {
                        gp.closePath();
                    }
                    drawing = false;
                } else {
                    drawing = true;
                    gp.moveTo(x, y);
                    gp.lineTo(x + 1, y);
                    gp.lineTo(x + 1, y + 1);
                    gp.lineTo(x, y + 1);
                    gp.moveTo(x, y);

                }
            }
            gp.closePath();
        }
        gp.closePath();
        // construct the Area from the GP & return it
        return new Area(gp);
    }

}
