/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\ggiger\\Documents\\GitHub\\NavPalFloorplans\\Localization\\LocalizationLibrary\\src\\edu\\cmu\\ri\\rcommerce\\LocalizationIPC.aidl
 */
package edu.cmu.ri.rcommerce;
public interface LocalizationIPC extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements edu.cmu.ri.rcommerce.LocalizationIPC
{
private static final java.lang.String DESCRIPTOR = "edu.cmu.ri.rcommerce.LocalizationIPC";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an edu.cmu.ri.rcommerce.LocalizationIPC interface,
 * generating a proxy if needed.
 */
public static edu.cmu.ri.rcommerce.LocalizationIPC asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof edu.cmu.ri.rcommerce.LocalizationIPC))) {
return ((edu.cmu.ri.rcommerce.LocalizationIPC)iin);
}
return new edu.cmu.ri.rcommerce.LocalizationIPC.Stub.Proxy(obj);
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
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
edu.cmu.ri.rcommerce.LocalizationCallback _arg0;
_arg0 = edu.cmu.ri.rcommerce.LocalizationCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
edu.cmu.ri.rcommerce.LocalizationCallback _arg0;
_arg0 = edu.cmu.ri.rcommerce.LocalizationCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setLocation:
{
data.enforceInterface(DESCRIPTOR);
float _arg0;
_arg0 = data.readFloat();
float _arg1;
_arg1 = data.readFloat();
this.setLocation(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setOrientationOffset:
{
data.enforceInterface(DESCRIPTOR);
float _arg0;
_arg0 = data.readFloat();
this.setOrientationOffset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setCoordinateSystemToRobot:
{
data.enforceInterface(DESCRIPTOR);
this.setCoordinateSystemToRobot();
reply.writeNoException();
return true;
}
case TRANSACTION_setCoordinateSystemToGlobal:
{
data.enforceInterface(DESCRIPTOR);
this.setCoordinateSystemToGlobal();
reply.writeNoException();
return true;
}
case TRANSACTION_reset:
{
data.enforceInterface(DESCRIPTOR);
this.reset();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements edu.cmu.ri.rcommerce.LocalizationIPC
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
@Override public void registerCallback(edu.cmu.ri.rcommerce.LocalizationCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(edu.cmu.ri.rcommerce.LocalizationCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setLocation(float x, float y) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeFloat(x);
_data.writeFloat(y);
mRemote.transact(Stub.TRANSACTION_setLocation, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setOrientationOffset(float rad) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeFloat(rad);
mRemote.transact(Stub.TRANSACTION_setOrientationOffset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setCoordinateSystemToRobot() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setCoordinateSystemToRobot, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setCoordinateSystemToGlobal() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setCoordinateSystemToGlobal, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void reset() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setLocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setOrientationOffset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setCoordinateSystemToRobot = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setCoordinateSystemToGlobal = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_reset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
public void registerCallback(edu.cmu.ri.rcommerce.LocalizationCallback cb) throws android.os.RemoteException;
public void unregisterCallback(edu.cmu.ri.rcommerce.LocalizationCallback cb) throws android.os.RemoteException;
public void setLocation(float x, float y) throws android.os.RemoteException;
public void setOrientationOffset(float rad) throws android.os.RemoteException;
public void setCoordinateSystemToRobot() throws android.os.RemoteException;
public void setCoordinateSystemToGlobal() throws android.os.RemoteException;
public void reset() throws android.os.RemoteException;
}
