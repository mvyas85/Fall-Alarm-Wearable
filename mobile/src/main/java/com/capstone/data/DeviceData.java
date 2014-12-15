package com.capstone.data;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by manisha on 12/6/14.
 */
public class DeviceData implements Serializable {

    static final long serialVersionUID =7903520492787961684L;

    private String DeviceID;
    private double  AccX , AccY , AccZ , GyrX , GyrY , GyrZ , LocX , LocY ;

    public DeviceData(String deviceID, double accX, double accY, double accZ, double gyrX, double gyrY, double gyrZ, double locX, double locY) {
        DeviceID = deviceID;
        AccX = accX;
        AccY = accY;
        AccZ = accZ;
        GyrX = gyrX;
        GyrY = gyrY;
        GyrZ = gyrZ;
        LocX = locX;
        LocY = locY;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public double getAccX() {
        return AccX;
    }

    public void setAccX(double accX) {
        AccX = accX;
    }

    public double getAccY() {
        return AccY;
    }

    public void setAccY(double accY) {
        AccY = accY;
    }

    public double getAccZ() {
        return AccZ;
    }

    public void setAccZ(double accZ) {
        AccZ = accZ;
    }

    public double getGyrX() {
        return GyrX;
    }

    public void setGyrX(double gyrX) {
        GyrX = gyrX;
    }

    public double getGyrY() {
        return GyrY;
    }

    public void setGyrY(double gyrY) {
        GyrY = gyrY;
    }

    public double getGyrZ() {
        return GyrZ;
    }

    public void setGyrZ(double gyrZ) {
        GyrZ = gyrZ;
    }

    public double getLocX() {
        return LocX;
    }

    public void setLocX(double locX) {
        LocX = locX;
    }

    public double getLocY() {
        return LocY;
    }

    public void setLocY(double locY) {
        LocY = locY;
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }
    public static DeviceData deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);

        Log.i("PhoneMainnot", "Done byte stream" + b);
        ObjectInputStream o = new ObjectInputStream(b);

        Log.i("PhoneMainnot", "Object stream=" + o);

        DeviceData adata =  (DeviceData)o.readObject();

        Log.i("PhoneMainNot", adata.getDeviceID());
        return (DeviceData)o.readObject();
    }

}
