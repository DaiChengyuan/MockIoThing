package com.dcy.mockiothing.platform.common.util;

import java.io.*;
import java.util.Map;

public class CloneUtil {

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(Map<String, String> obj) {
        T clonedObj = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clonedObj = (T) ois.readObject();
            ois.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clonedObj;
    }
}
