package com.sifli.sifliapp.modules.devicescan.model;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;



import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class BluetoothLeDevice implements Parcelable {

    protected static final int MAX_RSSI_LOG_SIZE = 10;
    private static final String PARCEL_EXTRA_BLUETOOTH_DEVICE = "bluetooth_device";
    private static final String PARCEL_EXTRA_CURRENT_RSSI = "current_rssi";
    private static final String PARCEL_EXTRA_CURRENT_TIMESTAMP = "current_timestamp";
    private static final String PARCEL_EXTRA_DEVICE_RSSI_LOG = "device_rssi_log";
    private static final String PARCEL_EXTRA_DEVICE_SCANRECORD = "device_scanrecord";
    private static final String PARCEL_EXTRA_DEVICE_SCANRECORD_STORE = "device_scanrecord_store";
    private static final String PARCEL_EXTRA_FIRST_RSSI = "device_first_rssi";
    private static final String PARCEL_EXTRA_FIRST_TIMESTAMP = "first_timestamp";
    private transient Set<BluetoothServiceType> mServiceSet;
    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    private final BluetoothDevice mDevice;
    private final Map<Long, Integer> mRssiLog;
    private final int mFirstRssi;
    private final long mFirstTimestamp;
    private final byte[] mScanRecord;
    private final AdRecordStore mRecordStore;
    private long mCurrentTimestamp;
    private static final long LOG_INVALIDATION_THRESHOLD = 10 * 1000;
    private int mCurrentRssi;
    private Map<String, BluetoothLeDevice> mDeviceMap;


    public BluetoothLeDevice(final BluetoothDevice device, final int rssi, final byte[] scanRecord, final long timestamp) {
        mDevice = device;
        mFirstRssi = rssi;
        mFirstTimestamp = timestamp;
        mRecordStore = new AdRecordStore(AdRecordStore.parseScanRecordAsSparseArray(scanRecord));
        mScanRecord = scanRecord;
        mRssiLog = new LinkedHashMap<>(MAX_RSSI_LOG_SIZE);
        updateRssiReading(timestamp, rssi);
    }

    /**
     * Instantiates a new Bluetooth LE device.
     *
     * @param device the device
     */
    public BluetoothLeDevice(final BluetoothLeDevice device) {
        mCurrentRssi = device.getRssi();
        mCurrentTimestamp = device.getTimestamp();
        mDevice = device.getDevice();
        mFirstRssi = device.getFirstRssi();
        mFirstTimestamp = device.getFirstTimestamp();
        mRecordStore = new AdRecordStore(AdRecordStore.parseScanRecordAsSparseArray(device.getScanRecord()));
        mRssiLog = device.getRssiLog();
        mScanRecord = device.getScanRecord();
    }

    /**
     * Instantiates a new bluetooth le device.
     *
     * @param in the in
     */
    protected BluetoothLeDevice(final Parcel in) {
        final Bundle b = in.readBundle(getClass().getClassLoader());

        mCurrentRssi = b.getInt(PARCEL_EXTRA_CURRENT_RSSI, 0);
        mCurrentTimestamp = b.getLong(PARCEL_EXTRA_CURRENT_TIMESTAMP, 0);
        mDevice = b.getParcelable(PARCEL_EXTRA_BLUETOOTH_DEVICE);
        mFirstRssi = b.getInt(PARCEL_EXTRA_FIRST_RSSI, 0);
        mFirstTimestamp = b.getLong(PARCEL_EXTRA_FIRST_TIMESTAMP, 0);
        mRecordStore = b.getParcelable(PARCEL_EXTRA_DEVICE_SCANRECORD_STORE);
        mRssiLog = (Map<Long, Integer>) b.getSerializable(PARCEL_EXTRA_DEVICE_RSSI_LOG);
        mScanRecord = b.getByteArray(PARCEL_EXTRA_DEVICE_SCANRECORD);
    }

    public static final Creator<BluetoothLeDevice> CREATOR = new Creator<BluetoothLeDevice>() {
        @Override
        public BluetoothLeDevice createFromParcel(Parcel in) {
            return new BluetoothLeDevice(in);
        }

        @Override
        public BluetoothLeDevice[] newArray(int size) {
            return new BluetoothLeDevice[size];
        }
    };

    /**
     * Gets the first rssi.
     *
     * @return the first rssi
     */
    public int getFirstRssi() {
        return mFirstRssi;
    }


    /**
     * Gets the scan record.
     *
     * @return the scan record
     */
    public byte[] getScanRecord() {
        return mScanRecord;
    }

    /**
     * Gets the rssi log.
     *
     * @return the rssi log
     */
    protected Map<Long, Integer> getRssiLog() {
        synchronized (mRssiLog) {
            return mRssiLog;
        }
    }

    /**
     * Gets the first timestamp.
     *
     * @return the first timestamp
     */
    public long getFirstTimestamp() {
        return mFirstTimestamp;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
@SuppressLint("MissingPermission")
public String getDeviceName() {
    return mDevice.getName();
}

    /**
     * Gets the address.
     *
     * @return the address
     */
    public String getAddress() {
        return mDevice.getAddress();
    }

    /**
     * Update rssi reading.
     *
     * @param timestamp   the timestamp
     * @param rssiReading the rssi reading
     */
    public void updateRssiReading(final long timestamp, final int rssiReading) {
        addToRssiLog(timestamp, rssiReading);
    }

    /**
     * Gets the device.
     *
     * @return the device
     */
    public BluetoothDevice getDevice() {
        return mDevice;
    }

    /**
     * Gets the bluetooth device bond state.
     *
     * @return the bluetooth device bond state
     */
    @SuppressLint("MissingPermission")
    public String getBluetoothDeviceBondState() {
        return resolveBondingState(mDevice.getBondState());
    }

    /**
     * Resolve bonding state.
     *
     * @param bondState the bond state
     * @return the string
     */
    private static String resolveBondingState(final int bondState) {
        switch (bondState) {
            case BluetoothDevice.BOND_BONDED://已配对
                return "Paired";
            case BluetoothDevice.BOND_BONDING://配对中
                return "Pairing";
            case BluetoothDevice.BOND_NONE://未配对
                return "UnBonded";
            default:
                return "Unknown";//未知状态
        }
    }

    /**
     * Gets the running average rssi.
     *
     * @return the running average rssi
     */
    public double getRunningAverageRssi() {
        int sum = 0;
        int count = 0;

        synchronized (mRssiLog) {

            for (final Long aLong : mRssiLog.keySet()) {
                count++;
                sum += mRssiLog.get(aLong);
            }
        }

        if (count > 0) {
            return sum / count;
        } else {
            return 0;
        }

    }

    /**
     * Gets the ad record store.
     *
     * @return the ad record store
     */
    public AdRecordStore getAdRecordStore() {
        return mRecordStore;
    }

    private void addToRssiLog(final long timestamp, final int rssiReading) {
        synchronized (mRssiLog) {
            if (timestamp - mCurrentTimestamp > LOG_INVALIDATION_THRESHOLD) {
                mRssiLog.clear();
            }

            mCurrentRssi = rssiReading;
            mCurrentTimestamp = timestamp;
            mRssiLog.put(timestamp, rssiReading);
        }
    }
@SuppressLint("MissingPermission")
    public Set<BluetoothServiceType> getBluetoothDeviceKnownSupportedServices() {
        if (mServiceSet == null) {
            synchronized (this) {
                if (mServiceSet == null) {
                    final Set<BluetoothServiceType> serviceSet = new HashSet<>();
                    for (final BluetoothServiceType service : BluetoothServiceType.values()) {
                        if (mDevice.getBluetoothClass().hasService(service.getCode())) {
                            serviceSet.add(service);
                        }
                    }
                    mServiceSet = Collections.unmodifiableSet(serviceSet);
                }
            }
        }

        return mServiceSet;
    }

    /**
     * Gets the bluetooth device class name.
     *
     * @return the bluetooth device class name
     */
    @SuppressLint("MissingPermission")
    public String getBluetoothDeviceClassName() {
        return BluetoothClassResolver.resolveDeviceClass(mDevice.getBluetoothClass().getDeviceClass());
    }

    /**
     * Gets the bluetooth device major class name.
     *
     * @return the bluetooth device major class name
     */
    @SuppressLint("MissingPermission")
    public String getBluetoothDeviceMajorClassName() {
        return BluetoothClassResolver.resolveMajorDeviceClass(mDevice.getBluetoothClass().getMajorDeviceClass());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BluetoothLeDevice [mDevice=" + mDevice + ", " +
                "mRssi=" + mFirstRssi + ", mScanRecord=" + encodeHexStr(mScanRecord) +
                ", mRecordStore=" + mRecordStore + ", getBluetoothDeviceBondState()=" +
                getBluetoothDeviceBondState() + ", getBluetoothDeviceClassName()=" +
                getBluetoothDeviceClassName() + "]";
    }



    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final BluetoothLeDevice other = (BluetoothLeDevice) obj;
        if (mCurrentRssi != other.mCurrentRssi) return false;
        if (mCurrentTimestamp != other.mCurrentTimestamp) return false;
        if (mDevice == null) {
            if (other.mDevice != null) return false;
        } else if (!mDevice.equals(other.mDevice)) return false;
        if (mFirstRssi != other.mFirstRssi) return false;
        if (mFirstTimestamp != other.mFirstTimestamp) return false;
        if (mRecordStore == null) {
            if (other.mRecordStore != null) return false;
        } else if (!mRecordStore.equals(other.mRecordStore)) return false;
        if (mRssiLog == null) {
            if (other.mRssiLog != null) return false;
        } else if (!mRssiLog.equals(other.mRssiLog)) return false;
        if (Arrays.equals(mScanRecord, other.mScanRecord)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + mCurrentRssi;
        result = prime * result + (int) (mCurrentTimestamp ^ (mCurrentTimestamp >>> 32));
        result = prime * result + ((mDevice == null) ? 0 : mDevice.hashCode());
        result = prime * result + mFirstRssi;
        result = prime * result + (int) (mFirstTimestamp ^ (mFirstTimestamp >>> 32));
        result = prime * result + ((mRecordStore == null) ? 0 : mRecordStore.hashCode());
        result = prime * result + ((mRssiLog == null) ? 0 : mRssiLog.hashCode());
        result = prime * result + Arrays.hashCode(mScanRecord);
        return result;
    }

    public long getTimestamp() {
        return mCurrentTimestamp;
    }
    /**
     * Gets the rssi.
     *
     * @return the rssi
     */
    public int getRssi() {
        return mCurrentRssi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    public void writeToParcel(final Parcel parcel, final int arg1) {
        final Bundle b = new Bundle(getClass().getClassLoader());

        b.putByteArray(PARCEL_EXTRA_DEVICE_SCANRECORD, mScanRecord);

        b.putInt(PARCEL_EXTRA_FIRST_RSSI, mFirstRssi);
        b.putInt(PARCEL_EXTRA_CURRENT_RSSI, mCurrentRssi);

        b.putLong(PARCEL_EXTRA_FIRST_TIMESTAMP, mFirstTimestamp);
        b.putLong(PARCEL_EXTRA_CURRENT_TIMESTAMP, mCurrentTimestamp);

        b.putParcelable(PARCEL_EXTRA_BLUETOOTH_DEVICE, mDevice);
        b.putParcelable(PARCEL_EXTRA_DEVICE_SCANRECORD_STORE, mRecordStore);
        b.putSerializable(PARCEL_EXTRA_DEVICE_RSSI_LOG, (Serializable) mRssiLog);

        parcel.writeBundle(b);
    }
    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data byte[]
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data        byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data     byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制char[]
     */
    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data byte[]
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data        byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data     byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制String
     */
    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        if (data == null) {
            //Log.e("this data is null.");
            return "";
        }
        return new String(encodeHex(data, toDigits));
    }


    public enum BluetoothServiceType {
        AUDIO(BluetoothClass.Service.AUDIO),    //音频服务
        CAPTURE(BluetoothClass.Service.CAPTURE),    //捕捉服务
        INFORMATION(BluetoothClass.Service.INFORMATION),    //信息服务
        LIMITED_DISCOVERABILITY(BluetoothClass.Service.LIMITED_DISCOVERABILITY),    //有限发现服务
        NETWORKING(BluetoothClass.Service.NETWORKING),  //网络服务
        OBJECT_TRANSFER(BluetoothClass.Service.OBJECT_TRANSFER),    //对象传输服务
        POSITIONING(BluetoothClass.Service.POSITIONING),    //定位服务
        RENDER(BluetoothClass.Service.RENDER),  //给予服务
        TELEPHONY(BluetoothClass.Service.TELEPHONY);    //电话服务

        private int code;

        BluetoothServiceType(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }
}
