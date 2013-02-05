/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\ggiger\\Documents\\GitHub\\NavPalFloorplans\\PathPlanningWithConstraints\\src\\edu\\cmu\\ri\\rcommerce\\LocalizationCallback.aidl
 */
package edu.cmu.ri.rcommerce;
public interface LocalizationCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements edu.cmu.ri.rcommerce.LocalizationCallback
{
private static final java.lang.String DESCRIPTOR = "edu.cmu.ri.rcommerce.LocalizationCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an edu.cmu.ri.rcommerce.LocalizationCallback interface,
 * generating a proxy if needed.
 */
public static edu.cmu.ri.rcommerce.LocalizationCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof edu.cmu.ri.rcommerce.LocalizationCallback))) {
return ((edu.cmu.ri.rcommerce.LocalizationCallback)iin);
}
return new edu.cmu.ri.rcommerce.LocalizationCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_locationUpdate:
{
data.enforceInterface(DESCRIPTOR);
double _arg0;
_arg0 = data.readDouble();
double _arg1;
_arg1 = data.readDouble();
double _arg2;
_arg2 = data.readDouble();
long _arg3;
_arg3 = data.readLong();
this.locationUpdate(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements edu.cmu.ri.rcommerce.LocalizationCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void locationUpdate(double x, double y, double theta, long time) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeDouble(x);
_data.writeDouble(y);
_data.writeDouble(theta);
_data.writeLong(time);
mRemote.transact(Stub.TRANSACTION_locationUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_locationUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void locationUpdate(double x, double y, double theta, long time) throws android.os.RemoteException;
}
