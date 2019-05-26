package core.utils;

import core.adapters.IWaifuAdapter;
import core.entities.WaifuImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WaifuUtils {

    public static IWaifuAdapter getWaifuFromAdapterName(String adapterName, String shipName) {
        try {
            Class<?> c = Class.forName("core.adapters.impl." + adapterName);

            // Someone could cheat in some way (??)
            // Prevention is better than cure lol
            if (IWaifuAdapter.class.isAssignableFrom(c)) {
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
        }

        return null;
    }

    public static byte[] getShipImage(IWaifuAdapter waifuAdapter, int skinIndex) throws IOException {
        System.out.println("Getting skin index: " + skinIndex);

        Path resourcePath = Paths.get("resources");
        if (!resourcePath.toFile().exists() && !resourcePath.toFile().mkdir()) {
            throw new IOException("Cannot make tmp resources dir");
        }

        Path imgPath = Paths.get("resources/" + waifuAdapter.getName() + "_" + skinIndex + ".png");

        byte[] imgData;

        if (Files.exists(imgPath)) {
            System.out.println("Image already downloaded");
            imgData = Files.readAllBytes(imgPath);
        } else {
            System.out.println("Image not in local memory, download and saving...");
            File file = imgPath.toFile();
            if (!file.createNewFile()) {
                throw new IOException("Cannot save temp img...");
            }
            try (FileOutputStream fos = new FileOutputStream(imgPath.toFile().getAbsolutePath())) {
                WaifuImage[] set = waifuAdapter.getImageSizeSet(skinIndex);
                if (set == null) {
                    return null;
                }
                imgData = set[set.length - 1].download();
                fos.write(imgData);
            }
        }

        return imgData;
    }

}
